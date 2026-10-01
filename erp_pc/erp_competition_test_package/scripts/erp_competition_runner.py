from __future__ import annotations

import argparse
import asyncio
import csv
import json
import math
import re
import sys
import time
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Any
from urllib.request import urlopen
from urllib.parse import quote

from playwright.async_api import Browser, BrowserContext, Page, TimeoutError, async_playwright


BASE_URL = "http://39.98.115.237/"
STUDENT_PASSWORD = "123456"
MENU_PATHS = {
    "/gameoperate/applyLoan": ("贷款与融资", "申请贷款"),
    "/gameoperate/materialPurchase": ("采购管理", "购买原料"),
    "/gameoperate/deliveryWithOrder": ("销售管理", "按订单交货"),
    "/gameoperate/workshopManagement": ("生产管理", "厂房管理"),
    "/gameoperate/advertisement": ("市场管理", "广告投放 & 订单"),
    "/gameoperate/marketManagement": ("市场管理", "市场开拓"),
    "/gameoperate/productResearch": ("产品研发&认证", "产品研发管理"),
    "/gameoperate/authenticationmanagement": ("产品研发&认证", "认证管理"),
    "/gameoperate/time": ("时间管理",),
}
CASH_LOAN_FLOOR = 30
CASH_LOAN_TARGET = 45
HIGH_LOAN_UI_MAX = 99999
EXPANDED_PRODUCTS = ("P2", "P3", "P4")
EXPANDED_MARKETS = ("区域市场", "国内市场", "亚洲市场", "国际市场")
EXPANDED_ISOS = ("PRODUCT9000质量认证", "PRODUCT1400环境认证")
BASIC_PRODUCTS = ("P1",)
BASIC_MATERIAL_PLAN = {"R1": 4}
EXPANDED_MATERIAL_PLAN = {"R1": 4, "R2": 4, "R3": 2, "R4": 1}
BASIC_RUNNER_NOTES = [
    "当前策略是 P1 基础经营流：投本地 P1 广告、选 P1 订单、采购 R1、生产 P1、交付订单。",
    "当前策略未开拓区域/国内/亚洲/国际市场，也未研发 P2/P3/P4 或 ISO，所以后期很多非 P1 订单无法选择。",
    "游戏结束页的“重新开始游戏”可以让前端业务状态显示为退出，但它只会把 gamegroupmember.STATUS 改成 -1，不会删除成员关系。",
    "为了确保测试账号能删除，清理时必须先调用 /erpn/group/quit 删除 gamegroupmember 成员关系，再调用教师删除账号接口。",
]
EXPANDED_RUNNER_NOTES = [
    "当前策略是扩展经营流：持续点击研发 P2/P3/P4、开拓区域/国内/亚洲/国际市场、研发 PRODUCT9000/PRODUCT1400 认证。",
    "广告投放会对页面上可见的 P1/P2/P3/P4 订单行各投 1 万；选单仍通过订货会页面真实点击完成。",
    "采购策略会购买 R1/R2/R3/R4，为 P2/P3/P4 生产准备原料；生产线转产会在研发完成后尽量通过页面点击触发。",
    "为了确保测试账号能删除，清理时必须先调用 /erpn/group/quit 删除 gamegroupmember 成员关系，再调用教师删除账号接口。",
]
RUNNER_NOTES = BASIC_RUNNER_NOTES


@dataclass(frozen=True)
class StudentAccount:
    user_id: str
    name: str
    sid: str
    sclass: str = "自动化测试班"
    major: str = "信息管理"
    college: str = "自动化测试学院"
    password: str = STUDENT_PASSWORD


@dataclass
class StudentSession:
    account: StudentAccount
    context: BrowserContext
    page: Page
    complete: bool = False
    bankrupt: bool = False


@dataclass
class RunnerConfig:
    base_url: str
    teacher_user: str
    teacher_password: str
    run_id: str
    group_name: str
    output_dir: Path
    browser_channel: str = "msedge"
    headless: bool = False
    max_cycles: int = 80
    cleanup: bool = True
    check_env: bool = False
    strategy: str = "expanded"


def generate_students(run_id: str) -> list[StudentAccount]:
    digits = "".join(ch for ch in run_id if ch.isdigit()) or str(int(time.time()))
    suffix = digits[-10:]
    specs = [("测甲", f"{suffix}01"), ("测乙", f"{suffix}02"), ("测丙", f"{suffix}03")]
    return [
        StudentAccount(f"{name}{sid}", name, sid)
        for name, sid in specs
    ]


def write_import_csv(output_dir: Path, students: list[StudentAccount]) -> Path:
    output_dir.mkdir(parents=True, exist_ok=True)
    csv_path = output_dir / "students-for-game.csv"
    with csv_path.open("w", encoding="gbk", newline="") as file:
        writer = csv.writer(file)
        writer.writerow(["用户ID（必填）", "姓名", "学号", "班级", "专业", "学院"])
        for student in students:
            writer.writerow(
                [student.user_id, student.name, student.sid, student.sclass, student.major, student.college]
            )
    return csv_path


def write_user_csv(output_dir: Path, students: list[StudentAccount]) -> Path:
    output_dir.mkdir(parents=True, exist_ok=True)
    csv_path = output_dir / "students-users.csv"
    with csv_path.open("w", encoding="gbk", newline="") as file:
        writer = csv.writer(file)
        writer.writerow(["姓名（必填）", "学号（必填）", "班级", "专业", "学院"])
        for student in students:
            writer.writerow([student.name, student.sid, student.sclass, student.major, student.college])
    return csv_path


def positive_int(value: Any) -> int:
    try:
        return max(0, math.floor(float(value)))
    except (TypeError, ValueError):
        return 0


def loan_by_type(loan_info: list[dict[str, Any]], loan_type: str) -> dict[str, Any] | None:
    return next((item for item in loan_info if item.get("loanTypeName") == loan_type), None)


def max_term(loan: dict[str, Any] | None) -> int:
    if not loan:
        return 0
    return positive_int(loan.get("permitPeriod") or loan.get("permitYear"))


def build_loan_plan(
    info: dict[str, Any],
    loan_info: list[dict[str, Any]],
    cash_floor: int = CASH_LOAN_FLOOR,
    cash_target: int = CASH_LOAN_TARGET,
) -> dict[str, Any] | None:
    try:
        cash = float(info.get("cash"))
    except (TypeError, ValueError):
        return None
    if cash >= cash_floor:
        return None

    requested = max(1, math.ceil(cash_target - cash))
    candidates: list[dict[str, Any]] = []

    short_loan = loan_by_type(loan_info, "短期贷款")
    if short_loan:
        max_money = positive_int(short_loan.get("maxOfLoan"))
        term = max_term(short_loan)
        if max_money > 0 and term > 0:
            candidates.append({"type": "短期贷款", "money": min(requested, max_money), "term": term})

    long_loan = loan_by_type(loan_info, "长期贷款")
    if long_loan:
        max_money = positive_int(long_loan.get("maxOfLoan"))
        term = max_term(long_loan)
        remaining_years = positive_int(long_loan.get("totalYear")) - positive_int(long_loan.get("currentYear"))
        if (
            max_money > 0
            and term >= 2
            and positive_int(long_loan.get("beginYear")) == 1
            and remaining_years >= 2
        ):
            candidates.append(
                {"type": "长期贷款", "money": min(requested, max_money), "term": min(term, remaining_years)}
            )

    high_loan = loan_by_type(loan_info, "高利贷")
    if high_loan:
        term = max_term(high_loan)
        if term > 0:
            candidates.append({"type": "高利贷", "money": min(requested, HIGH_LOAN_UI_MAX), "term": term})

    return next((candidate for candidate in candidates if candidate["money"] > 0), None)


def normalize_strategy(strategy: str | None) -> str:
    return "basic-p1" if strategy == "basic-p1" else "expanded"


def development_targets_for_strategy(strategy: str | None) -> dict[str, tuple[str, ...]]:
    if normalize_strategy(strategy) == "basic-p1":
        return {"products": (), "markets": (), "isos": ()}
    return {"products": EXPANDED_PRODUCTS, "markets": EXPANDED_MARKETS, "isos": EXPANDED_ISOS}


def material_plan_for_strategy(strategy: str | None) -> dict[str, int]:
    if normalize_strategy(strategy) == "basic-p1":
        return dict(BASIC_MATERIAL_PLAN)
    return dict(EXPANDED_MATERIAL_PLAN)


def material_card_index_for_name(material_name: str) -> int | None:
    order = ("R1", "R2", "R3", "R4")
    try:
        return order.index(material_name)
    except ValueError:
        return None


def navigation_retry_delays(attempts: int) -> list[float]:
    delays = [0, 1.5, 3, 5, 8]
    return delays[: max(1, min(attempts, len(delays)))]


def ad_products_for_strategy(strategy: str | None) -> tuple[str, ...]:
    if normalize_strategy(strategy) == "basic-p1":
        return BASIC_PRODUCTS
    return BASIC_PRODUCTS + EXPANDED_PRODUCTS


def strategy_notes(strategy: str | None) -> list[str]:
    if normalize_strategy(strategy) == "basic-p1":
        return list(BASIC_RUNNER_NOTES)
    return list(EXPANDED_RUNNER_NOTES)


def needs_annual_planning(info: dict[str, Any], ad_progress: int | None) -> bool:
    return positive_int(info.get("currentPeriod")) == 1 and ad_progress != 5


def safe_name(value: str) -> str:
    return re.sub(r'[\\/:*?"<>|]', "_", value)


def cleanup_deleted(cleanup: dict[str, Any] | None) -> bool:
    return bool(cleanup is not None and cleanup.get("remaining") == [])


def cleanup_diagnosis(cleanup: dict[str, Any] | None) -> str | None:
    if cleanup_deleted(cleanup):
        return None
    if not cleanup:
        return "清理没有返回结果，无法确认测试账号是否删除。"
    if cleanup.get("remaining"):
        return (
            "仍有账号残留。后端 approveduser 被 gamegroupmember.USERID 外键引用；"
            "必须先调用 /erpn/group/quit 删除比赛成员行，再调用教师删除接口。"
            "如果之前已经先 replay，成员状态会变成 -1，公开接口通常无法再 quit，需要数据库或后端清理接口处理。"
        )
    return "教师删除接口未确认成功，请检查 cleanup.deleteResult。"


async def check_environment(config: RunnerConfig) -> None:
    print("[CHECK] Python:", sys.version.split()[0])
    print("[CHECK] Base URL:", config.base_url)
    try:
        with urlopen(config.base_url, timeout=10) as response:
            print("[CHECK] ERP reachable:", response.status)
    except Exception as error:
        raise RuntimeError(f"ERP 地址无法访问: {config.base_url}; {error}") from error

    async with async_playwright() as playwright:
        browser = await playwright.chromium.launch(channel=config.browser_channel, headless=True)
        page = await browser.new_page()
        try:
            last_error: Exception | None = None
            for delay in navigation_retry_delays(4):
                if delay:
                    await page.wait_for_timeout(int(delay * 1000))
                try:
                    await page.goto(config.base_url, wait_until="domcontentloaded", timeout=30000)
                    last_error = None
                    break
                except Exception as error:
                    last_error = error
                    print("[CHECK] Login page navigation retry:", error)
            if last_error:
                raise last_error
            await page.get_by_placeholder("请输入用户名").wait_for(timeout=10000)
            print("[CHECK] Browser channel:", config.browser_channel)
            print("[CHECK] Login page rendered: OK")
        finally:
            await browser.close()


class ErpCompetitionRunner:
    def __init__(self, config: RunnerConfig, students: list[StudentAccount]):
        self.config = config
        self.students = students
        self.steps: list[dict[str, Any]] = []
        self.final: list[dict[str, Any]] = []
        self.teacher_context: BrowserContext | None = None
        self.teacher_page: Page | None = None

    def log(self, name: str, data: Any | None = None) -> None:
        entry = {"at": time.strftime("%Y-%m-%dT%H:%M:%S%z"), "name": name, "data": data or {}}
        self.steps.append(entry)
        print(f"[{entry['at']}] {name}")

    async def save_report(self, extra: dict[str, Any] | None = None) -> Path:
        self.config.output_dir.mkdir(parents=True, exist_ok=True)
        report = {
            "baseUrl": self.config.base_url,
            "teacher": self.config.teacher_user,
            "runId": self.config.run_id,
            "groupName": self.config.group_name,
            "strategy": normalize_strategy(self.config.strategy),
            "notes": strategy_notes(self.config.strategy),
            "students": [asdict(student) for student in self.students],
            "steps": self.steps,
            "final": self.final,
        }
        if extra:
            report.update(extra)
        report_path = self.config.output_dir / "report.json"
        report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
        return report_path

    def abs_url(self, hash_route: str) -> str:
        return self.config.base_url.rstrip("/") + "/#" + hash_route

    async def body_text(self, page: Page) -> str:
        try:
            return await page.locator("body").inner_text(timeout=3000)
        except TimeoutError:
            return ""

    async def goto_with_retry(
        self,
        page: Page,
        url: str,
        label: str,
        attempts: int = 5,
        timeout: int = 45000,
    ) -> None:
        last_error: Exception | None = None
        for attempt_index, delay in enumerate(navigation_retry_delays(attempts), start=1):
            if delay:
                await page.wait_for_timeout(int(delay * 1000))
            try:
                await page.goto(url, wait_until="domcontentloaded", timeout=timeout)
                return
            except Exception as error:
                last_error = error
                self.log(
                    f"{label}-goto-retry",
                    {
                        "attempt": attempt_index,
                        "url": url,
                        "error": f"{type(error).__name__}: {error}",
                    },
                )
                try:
                    await page.wait_for_load_state("domcontentloaded", timeout=3000)
                    text = await self.body_text(page)
                    if text:
                        self.log(f"{label}-goto-partial-page", {"text": text[:200], "url": page.url})
                        return
                except Exception:
                    pass
        if last_error:
            raise last_error

    async def click_if_visible(self, locator, label: str, timeout: int = 1800) -> bool:
        if await locator.count() < 1:
            return False
        target = locator.first
        try:
            if not await target.is_visible(timeout=timeout):
                return False
            await target.click(timeout=timeout, force=True)
            await asyncio.sleep(0.5)
            self.log(label)
            return True
        except TimeoutError:
            return False

    async def confirm_dialogs(self, page: Page, label: str = "confirm-dialog") -> bool:
        clicked = False
        for _ in range(4):
            selectors = [
                ".ant-modal-confirm-btns .ant-btn-primary",
                ".ant-popover-buttons .ant-btn-primary",
                ".ant-modal button:has-text('确认')",
                ".ant-modal button:has-text('确 认')",
            ]
            clicked_this_round = False
            for selector in selectors:
                locator = page.locator(selector).filter(visible=True)
                if await locator.count() < 1:
                    continue
                target = locator.last
                try:
                    await target.click(timeout=2500, force=True)
                except TimeoutError:
                    box = await target.bounding_box()
                    if not box:
                        continue
                    await page.mouse.click(box["x"] + box["width"] / 2, box["y"] + box["height"] / 2)
                await page.wait_for_timeout(700)
                self.log(label)
                clicked = True
                clicked_this_round = True
                break
            if not clicked_this_round:
                break
        return clicked

    async def close_or_confirm_modals(self, page: Page) -> None:
        await self.confirm_dialogs(page)
        close_buttons = page.locator(".ant-modal-close").filter(visible=True)
        if await close_buttons.count() > 0:
            await close_buttons.first.click(timeout=1000, force=True)
            await page.wait_for_timeout(300)
            self.log("close-modal")

    async def api_get(self, page: Page, path: str) -> Any:
        return await page.evaluate(
            """async (path) => {
                const response = await fetch(path, { credentials: 'include' });
                const text = await response.text();
                try { return JSON.parse(text); } catch { return text; }
            }""",
            path,
        )

    async def api_post(self, page: Page, path: str, form: dict[str, Any] | None = None) -> Any:
        return await page.evaluate(
            """async ({ path, form }) => {
                const options = { method: 'POST', credentials: 'include' };
                if (form) {
                    const body = new URLSearchParams();
                    for (const [key, value] of Object.entries(form)) body.append(key, String(value));
                    options.body = body;
                }
                const response = await fetch(path, options);
                const text = await response.text();
                try { return JSON.parse(text); } catch { return text; }
            }""",
            {"path": path, "form": form},
        )

    async def data_get(self, page: Page, path: str) -> Any:
        result = await self.api_get(page, path)
        return result.get("data") if isinstance(result, dict) else None

    async def get_info(self, page: Page) -> dict[str, Any] | None:
        data = await self.data_get(page, "/erpn/user/info")
        return data if isinstance(data, dict) else None

    async def get_loan_info(self, page: Page) -> list[dict[str, Any]]:
        data = await self.data_get(page, "/erpn/loan/info")
        return data if isinstance(data, list) else []

    async def get_ad_progress(self, page: Page) -> int | None:
        data = await self.data_get(page, "/erpn/ad/getCurrentProgress")
        return positive_int(data) if data is not None else None

    async def login_teacher(self, browser: Browser) -> Page:
        self.teacher_context = await browser.new_context(viewport={"width": 1366, "height": 768})
        page = await self.teacher_context.new_page()
        self.teacher_page = page
        await self.goto_with_retry(page, self.config.base_url, "teacher-login")
        await page.get_by_placeholder("请输入用户名").fill(self.config.teacher_user)
        await page.get_by_placeholder("请输入密码").fill(self.config.teacher_password)
        await page.locator(".ant-radio-wrapper").filter(has_text="教师").click(force=True)
        await page.locator("button").filter(has_text=re.compile(r"登\s*录")).first.click()
        await page.wait_for_timeout(3500)
        self.log("teacher-login", {"url": page.url, "text": (await self.body_text(page))[:300]})
        if "/teacher" not in page.url:
            raise RuntimeError("教师登录失败，请检查账号、密码或教师身份选择")
        return page

    async def import_students_and_create_game(self, page: Page, csv_path: Path) -> None:
        await self.goto_with_retry(page, self.abs_url("/teacher/batchImportCreation"), "teacher-batch-import")
        await page.wait_for_timeout(2500)
        await page.locator("input[type=file]").set_input_files(str(csv_path))
        for student in self.students:
            await page.get_by_text(student.user_id, exact=False).wait_for(timeout=15000)
        self.log("teacher-import-students", {"csv": str(csv_path)})

        await page.locator("button").filter(has_text="创建比赛").first.click()
        await page.locator(".ant-modal input").fill(self.config.group_name)
        await page.locator(".ant-modal button").filter(has_text="开始比赛").first.click()
        await page.wait_for_timeout(3500)
        self.log("teacher-create-game", {"groupName": self.config.group_name, "text": (await self.body_text(page))[:400]})

    async def import_registered_students(self, page: Page, csv_path: Path) -> None:
        await self.goto_with_retry(page, self.abs_url("/teacher/userList"), "teacher-user-list")
        await page.wait_for_timeout(2500)
        await page.locator("input[type=file]").set_input_files(str(csv_path))
        await page.wait_for_timeout(3000)

        expected = {student.user_id for student in self.students}
        for _ in range(20):
            users = await self.data_get(page, "/erpn/teacher/findAllApprovedUser")
            if isinstance(users, list):
                existing = {user.get("userId") for user in users}
                if expected.issubset(existing):
                    self.log("teacher-import-registered-students", {"csv": str(csv_path), "users": sorted(expected)})
                    return
            await page.wait_for_timeout(1000)
        raise RuntimeError(f"用户导入后未在用户列表中找到: {sorted(expected)}")

    async def login_student(self, browser: Browser, student: StudentAccount) -> StudentSession:
        context = await browser.new_context(viewport={"width": 1366, "height": 768})
        page = await context.new_page()
        await self.goto_with_retry(page, self.config.base_url, f"{student.user_id}-login")
        await page.get_by_placeholder("请输入用户名").fill(student.user_id)
        await page.get_by_placeholder("请输入密码").fill(student.password)
        await page.locator("button").filter(has_text=re.compile(r"登\s*录")).first.click()
        await page.wait_for_timeout(4500)
        if not ("/gameoperate" in page.url or "/gameover" in page.url):
            await self.goto_with_retry(page, self.abs_url("/gameoperate/main"), f"{student.user_id}-login-main")
            await page.wait_for_timeout(2500)
        self.log(f"{student.user_id}-login", {"url": page.url, "text": (await self.body_text(page))[:250]})
        return StudentSession(student, context, page)

    async def menu_open(self, page: Page, route: str, label: str) -> None:
        await self.close_or_confirm_modals(page)
        if f"#{route}" in page.url:
            await page.wait_for_timeout(800)
            self.log(label, {"url": page.url, "alreadyThere": True})
            return
        path = MENU_PATHS[route]
        menu = page.locator(".ant-menu").first
        top = menu.get_by_text(path[0], exact=True).filter(visible=True)
        if await top.count() > 0:
            await top.first.click(force=True)
            await page.wait_for_timeout(700)
        if len(path) > 1:
            href = page.locator(f".ant-menu a[href='#{route}']").filter(visible=True)
            if await href.count() > 0:
                await href.first.click(force=True)
                await page.wait_for_timeout(1200)
                self.log(label, {"url": page.url})
                return
            leaf = menu.get_by_text(path[-1], exact=True).filter(visible=True)
            if await leaf.count() > 0:
                await leaf.first.click(force=True)
                await page.wait_for_timeout(1200)
                self.log(label, {"url": page.url})
                return
        await self.goto_with_retry(page, self.abs_url(route), label)
        await page.wait_for_timeout(1800)
        self.log(label, {"url": page.url, "recoveredByRoute": True})

    async def apply_loan(self, session: StudentSession, plan: dict[str, Any]) -> bool:
        page = session.page
        loan_type = plan["type"]
        money = positive_int(plan["money"])
        term = positive_int(plan["term"])
        try:
            await self.menu_open(page, "/gameoperate/applyLoan", f"{session.account.user_id}-open-loan")
            loan_selector = {"长期贷款": ".long-loan", "短期贷款": ".short-loan", "高利贷": ".high-loan"}[loan_type]
            box = await page.locator(loan_selector).bounding_box()
            if not box:
                return False
            await page.mouse.click(box["x"] + box["width"] / 2, box["y"] + box["height"] / 2)
            await page.wait_for_timeout(1500)
            self.log(f"{session.account.user_id}-click-{loan_type}", {"money": money, "term": term})
            money_input = page.locator(".loanModal input.money-input").filter(visible=True)
            if await money_input.count() < 1:
                self.log(f"{session.account.user_id}-loan-modal-not-open", plan)
                return False
            await money_input.first.fill(str(money))
            await page.locator(".loanModal .ant-select-selector").filter(visible=True).first.click(force=True)
            await page.wait_for_timeout(400)
            await page.locator(".ant-select-item-option").filter(has_text=str(term), visible=True).first.click(force=True)
            await page.keyboard.press("Escape")
            button = page.locator(".loanModal button.bottom-btn").filter(has_text=re.compile(r"申\s*请"), visible=True)
            await button.first.click(timeout=3000, force=True)
            await page.wait_for_timeout(1500)
            self.log(f"{session.account.user_id}-loan-{loan_type}", {"money": money, "term": term})
            return True
        except Exception as error:
            self.log(f"{session.account.user_id}-loan-failed", {"plan": plan, "error": str(error)})
            await self.close_or_confirm_modals(page)
            return False

    async def maybe_loan(self, session: StudentSession) -> None:
        info = await self.get_info(session.page)
        if not info:
            return
        loan_info = await self.get_loan_info(session.page)
        if normalize_strategy(self.config.strategy) == "expanded":
            plan = build_loan_plan(info, loan_info, cash_floor=55, cash_target=75)
        else:
            plan = build_loan_plan(info, loan_info)
        self.log(f"{session.account.user_id}-loan-plan", {"cash": info.get("cash"), "plan": plan, "loanInfo": loan_info})
        if plan:
            await self.apply_loan(session, plan)

    async def click_cards_by_name(
        self,
        session: StudentSession,
        route: str,
        card_selector: str,
        target_names: tuple[str, ...],
        action_texts: tuple[str, ...],
        label_prefix: str,
    ) -> list[str]:
        page = session.page
        clicked: list[str] = []
        if not target_names:
            return clicked
        await self.menu_open(page, route, f"{session.account.user_id}-open-{label_prefix}")
        await page.wait_for_timeout(1200)
        action_regex = re.compile("^(" + "|".join(re.escape(text) for text in action_texts) + ")$")

        for target_name in target_names:
            cards = page.locator(card_selector)
            found_target = False
            for index in range(await cards.count()):
                card = cards.nth(index)
                try:
                    text = await card.inner_text(timeout=1200)
                except TimeoutError:
                    continue
                if target_name not in text:
                    continue
                found_target = True
                buttons = card.locator("button").filter(has_text=action_regex)
                for button_index in range(await buttons.count()):
                    button = buttons.nth(button_index)
                    try:
                        if not await button.is_visible(timeout=800) or not await button.is_enabled(timeout=800):
                            continue
                        await button.click(timeout=2000, force=True)
                        await page.wait_for_timeout(1200)
                        await self.confirm_dialogs(page)
                        clicked.append(target_name)
                        self.log(
                            f"{session.account.user_id}-{label_prefix}-click",
                            {"target": target_name, "actions": action_texts},
                        )
                        break
                    except TimeoutError:
                        continue
                break
            if not found_target:
                self.log(f"{session.account.user_id}-{label_prefix}-target-not-visible", {"target": target_name})
        return clicked

    async def strategic_development(self, session: StudentSession) -> None:
        if normalize_strategy(self.config.strategy) != "expanded":
            return
        targets = development_targets_for_strategy(self.config.strategy)
        await self.click_cards_by_name(
            session,
            "/gameoperate/productResearch",
            ".product-item",
            targets["products"],
            ("开始研发", "继续研发"),
            "product-research",
        )
        await self.click_cards_by_name(
            session,
            "/gameoperate/marketManagement",
            ".div-box",
            targets["markets"],
            ("开始开拓", "继续开拓"),
            "market-development",
        )
        await self.click_cards_by_name(
            session,
            "/gameoperate/authenticationmanagement",
            ".product-item",
            targets["isos"],
            ("开始研发", "继续研发"),
            "iso-development",
        )

    async def get_developed_product_names(self, page: Page) -> tuple[str, ...]:
        data = await self.data_get(page, "/erpn/product/developeds")
        if not isinstance(data, list):
            return ()
        names = []
        for item in data:
            if isinstance(item, dict) and item.get("productName"):
                names.append(str(item["productName"]))
        return tuple(names)

    async def buy_r1(self, session: StudentSession, amount: int = 4) -> None:
        await self.buy_materials(session, {"R1": amount})

    async def buy_materials(self, session: StudentSession, plan: dict[str, int]) -> None:
        page = session.page
        await self.menu_open(page, "/gameoperate/materialPurchase", f"{session.account.user_id}-open-material")
        await page.wait_for_timeout(800)
        for material_name, amount in plan.items():
            if amount <= 0:
                continue
            cards = page.locator(".content-item .item")
            card_index = material_card_index_for_name(material_name)
            if card_index is None or await cards.count() <= card_index:
                self.log(f"{session.account.user_id}-buy-material-card-not-found", {"material": material_name})
                continue
            card = cards.nth(card_index)
            await card.scroll_into_view_if_needed(timeout=3000)
            await card.locator("input.item-input").fill(str(amount), timeout=5000)
            await card.locator("button.item-button").click(timeout=5000, force=True)
            await page.wait_for_timeout(300)
            await self.confirm_dialogs(page)
            await page.wait_for_timeout(900)
            self.log(f"{session.account.user_id}-buy-material", {"material": material_name, "amount": amount})

    async def deliver_orders(self, session: StudentSession) -> None:
        page = session.page
        await self.menu_open(page, "/gameoperate/deliveryWithOrder", f"{session.account.user_id}-open-delivery")
        for _ in range(8):
            buttons = page.locator("button.delivery-button").filter(has_text="交货")
            count = await buttons.count()
            if count < 1:
                return
            await buttons.first.click(force=True)
            await page.wait_for_timeout(1000)
            self.log(f"{session.account.user_id}-delivery-click", {"before": count})

    async def start_production(self, session: StudentSession) -> None:
        page = session.page
        await self.menu_open(page, "/gameoperate/workshopManagement", f"{session.account.user_id}-open-workshop")
        for _ in range(8):
            buttons = page.locator("button").filter(has_text="开始生产")
            count = await buttons.count()
            if count < 1:
                break
            await buttons.first.click(force=True)
            await page.wait_for_timeout(900)
            self.log(f"{session.account.user_id}-start-production", {"before": count})
        await self.configure_product_lines(session)

    async def configure_product_lines(self, session: StudentSession) -> None:
        page = session.page
        for _ in range(10):
            config_buttons = page.locator(".management_detail span.detail_btn").filter(has_text=re.compile(r"^配置$"))
            count = await config_buttons.count()
            if count < 1:
                return
            acted = False
            for index in range(count):
                try:
                    button = config_buttons.nth(index)
                    if not await button.is_visible(timeout=600):
                        continue
                    await button.click(timeout=1500, force=True)
                    await page.wait_for_timeout(300)
                    action = page.locator(".ant-popover .config_btn").filter(
                        has_text=re.compile(r"^(开始生产|继续生产|开始安装|继续转产)$")
                    )
                    if await action.count() < 1:
                        await page.keyboard.press("Escape")
                        continue
                    await action.first.click(timeout=2000, force=True)
                    await page.wait_for_timeout(900)
                    self.log(f"{session.account.user_id}-line-config-action", {"before": count})
                    acted = True
                    break
                except TimeoutError:
                    await page.keyboard.press("Escape")
            if not acted:
                return

    async def try_change_product_line(self, session: StudentSession) -> None:
        if normalize_strategy(self.config.strategy) != "expanded":
            return
        page = session.page
        developed = [name for name in await self.get_developed_product_names(page) if name != "P1"]
        if not developed:
            return
        await self.menu_open(page, "/gameoperate/workshopManagement", f"{session.account.user_id}-open-workshop-change")
        await page.wait_for_timeout(1000)
        details = page.locator(".management_detail").filter(has_text="待生产")
        for product_name in developed:
            for index in range(await details.count()):
                detail = details.nth(index)
                try:
                    detail_text = await detail.inner_text(timeout=1000)
                    if f"：{product_name}" in detail_text or f":{product_name}" in detail_text:
                        continue
                    transfer = detail.locator("span.detail_btn").filter(has_text=re.compile(r"^转产$"))
                    for button_index in range(await transfer.count()):
                        button = transfer.nth(button_index)
                        class_name = await button.get_attribute("class")
                        if class_name and "unactive" in class_name:
                            continue
                        if not await button.is_visible(timeout=800):
                            continue
                        await button.click(timeout=2000, force=True)
                        await page.wait_for_timeout(500)
                        selector = page.locator(".ant-popover .ant-select-selector").filter(visible=True)
                        if await selector.count() < 1:
                            await page.keyboard.press("Escape")
                            continue
                        await selector.first.click(force=True)
                        await page.wait_for_timeout(300)
                        options = page.locator(".ant-select-item-option").filter(has_text=product_name, visible=True)
                        if await options.count() < 1:
                            await page.keyboard.press("Escape")
                            continue
                        await options.first.click(force=True)
                        await page.wait_for_timeout(300)
                        change = page.locator(".ant-popover .change_btn").filter(has_text="开始转产", visible=True)
                        if await change.count() < 1:
                            await page.keyboard.press("Escape")
                            continue
                        await change.first.click(timeout=2000, force=True)
                        await page.wait_for_timeout(1200)
                        self.log(f"{session.account.user_id}-change-product-line", {"product": product_name})
                        return
                except TimeoutError:
                    await page.keyboard.press("Escape")

    async def put_and_confirm_ad(self, session: StudentSession) -> None:
        page = session.page
        await self.menu_open(page, "/gameoperate/advertisement", f"{session.account.user_id}-open-ad")
        await page.wait_for_timeout(1200)
        next_button = page.locator("button").filter(has_text="已投放完毕，下一步>")
        if await next_button.count() > 0:
            rows = page.locator("tbody tr")
            placed_products: set[str] = set()
            products = ad_products_for_strategy(self.config.strategy)
            for index in range(await rows.count()):
                row = rows.nth(index)
                row_text = await row.inner_text()
                product_name = next((product for product in products if product in row_text), None)
                if not product_name or product_name in placed_products:
                    continue
                await row.locator("input").fill("1")
                await row.locator("button").filter(has_text=re.compile(r"提\s*交")).click(force=True)
                await page.wait_for_timeout(800)
                placed_products.add(product_name)
                self.log(f"{session.account.user_id}-put-ad", {"product": product_name, "row": row_text[:120]})
            await next_button.first.click(force=True)
            await page.wait_for_timeout(1000)
            self.log(f"{session.account.user_id}-ad-next")

        confirm_button = page.locator("button").filter(has_text="确认订单，下一步 >")
        if await confirm_button.count() > 0:
            await confirm_button.first.click(force=True)
            await page.wait_for_timeout(300)
            await self.confirm_dialogs(page)
            await page.wait_for_timeout(1500)
            self.log(f"{session.account.user_id}-ad-confirmed")

    async def choose_and_finish_orders(self, session: StudentSession) -> None:
        page = session.page
        await self.menu_open(page, "/gameoperate/advertisement", f"{session.account.user_id}-open-order-hall")
        await page.wait_for_timeout(2500)
        await self.close_or_confirm_modals(page)
        for _ in range(8):
            buttons = page.locator("button.pBtn").filter(has_text="选择订单")
            count = await buttons.count()
            if count < 1:
                break
            await buttons.first.click(force=True)
            await page.wait_for_timeout(300)
            await self.confirm_dialogs(page)
            await page.wait_for_timeout(1500)
            self.log(f"{session.account.user_id}-choose-order-click", {"before": count})

        finish = page.locator("button.forthBtn").filter(has_text="结束选单")
        opened = await self.click_if_visible(finish, f"{session.account.user_id}-open-finish-order", 2500)
        if not opened:
            await page.reload(wait_until="domcontentloaded")
            await page.wait_for_timeout(3500)
            opened = await self.click_if_visible(finish, f"{session.account.user_id}-open-finish-order-after-reload", 2500)
        if opened:
            clicked_finish_all = await self.click_if_visible(
                page.locator(".ant-modal button").filter(has_text="结束所有订单"),
                f"{session.account.user_id}-finish-all-orders",
                2500,
            )
            if clicked_finish_all:
                await page.wait_for_timeout(400)
                await self.confirm_dialogs(page)
                await page.wait_for_timeout(1600)
                self.log(f"{session.account.user_id}-confirm-finish-all-orders")
            await self.close_or_confirm_modals(page)

    async def annual_planning(self, sessions: list[StudentSession]) -> None:
        self.log("annual-planning-start", [session.account.user_id for session in sessions])
        for _ in range(5):
            pending = False
            for session in sessions:
                if session.complete or session.bankrupt:
                    continue
                info = await self.get_info(session.page)
                if not info or positive_int(info.get("currentPeriod")) != 1:
                    continue
                progress = await self.get_ad_progress(session.page)
                if progress == 5:
                    continue
                pending = True
                if progress in (1, 2):
                    await self.put_and_confirm_ad(session)
                elif progress == 4:
                    await self.choose_and_finish_orders(session)
                else:
                    await self.menu_open(session.page, "/gameoperate/advertisement", f"{session.account.user_id}-open-ad-wait")
            await asyncio.sleep(1.8)
            if not pending:
                return

    async def advance(self, session: StudentSession) -> bool:
        page = session.page
        before = await self.get_info(page)
        await self.menu_open(page, "/gameoperate/time", f"{session.account.user_id}-open-time")
        button_img = page.locator('img[alt="进入下一周期"]')
        if await button_img.count() < 1:
            return False
        await button_img.first.click(force=True)
        await page.wait_for_timeout(300)
        await self.confirm_dialogs(page)
        await page.wait_for_timeout(3000)
        text = await self.body_text(page)
        after = await self.get_info(page)
        moved = True
        if before and after:
            before_abs = (positive_int(before.get("years")) - 1) * 4 + positive_int(before.get("currentPeriod"))
            after_abs = (positive_int(after.get("years")) - 1) * 4 + positive_int(after.get("currentPeriod"))
            moved = before_abs != after_abs
        if "/gameover" in page.url or "游戏已结束" in text or "比赛完毕" in text:
            session.complete = True
        if "破产" in text:
            session.bankrupt = True
        self.log(f"{session.account.user_id}-advance-click", {"url": page.url, "before": before, "after": after, "moved": moved, "text": text[:300]})
        return moved or session.complete or session.bankrupt

    async def capture_state(self, sessions: list[StudentSession], label: str) -> list[dict[str, Any]]:
        states = []
        for session in sessions:
            info = await self.get_info(session.page)
            text = await self.body_text(session.page)
            states.append({"account": session.account.user_id, "url": session.page.url, "info": info, "text": text[:220]})
        self.log(label, states)
        return states

    async def run_business(self, sessions: list[StudentSession]) -> None:
        for cycle in range(self.config.max_cycles):
            states = await self.capture_state(sessions, f"cycle-{cycle}-state")
            for session, state in zip(sessions, states):
                if "/gameover" in state["url"] or "游戏已结束" in state["text"] or "比赛完毕" in state["text"]:
                    session.complete = True
                if "破产" in state["text"]:
                    session.bankrupt = True
            if all(session.complete or session.bankrupt for session in sessions):
                return

            planning = []
            for session in sessions:
                if session.complete or session.bankrupt:
                    continue
                info = await self.get_info(session.page)
                progress = await self.get_ad_progress(session.page)
                if info and needs_annual_planning(info, progress):
                    planning.append(session)
            if planning:
                await self.annual_planning(planning)

            for session in sessions:
                if session.complete or session.bankrupt:
                    continue
                await self.maybe_loan(session)
                await self.strategic_development(session)
                await self.deliver_orders(session)
                await self.buy_materials(session, material_plan_for_strategy(self.config.strategy))
                await self.try_change_product_line(session)
                await self.start_production(session)
                await self.maybe_loan(session)

            for session in sessions:
                if session.complete or session.bankrupt:
                    continue
                advanced = await self.advance(session)
                if not advanced:
                    blocked = []
                    for item in sessions:
                        info = await self.get_info(item.page)
                        progress = await self.get_ad_progress(item.page)
                        if info and needs_annual_planning(info, progress):
                            blocked.append(item)
                    if blocked:
                        await self.annual_planning(blocked)
                        await self.advance(session)
            await self.save_report()

    async def final_verify(self, browser: Browser, student: StudentAccount) -> dict[str, Any]:
        context = await browser.new_context(viewport={"width": 1366, "height": 768})
        page = await context.new_page()
        try:
            try:
                await self.goto_with_retry(page, self.config.base_url, f"{student.user_id}-final-verify", attempts=4)
                await page.get_by_placeholder("请输入用户名").fill(student.user_id)
                await page.get_by_placeholder("请输入密码").fill(student.password)
                await page.locator("button").filter(has_text=re.compile(r"登\s*录")).first.click()
                await page.wait_for_timeout(4500)
                info = await self.get_info(page)
                screenshot = self.config.output_dir / f"{safe_name(student.user_id)}-final.png"
                await page.screenshot(path=str(screenshot), full_page=True)
                return {
                    "account": student.user_id,
                    "url": page.url,
                    "info": info,
                    "text": (await self.body_text(page))[:500],
                    "screenshot": str(screenshot),
                }
            except Exception as error:
                result = {
                    "account": student.user_id,
                    "url": page.url,
                    "error": {"type": type(error).__name__, "message": str(error)},
                    "text": (await self.body_text(page))[:500],
                }
                self.log(f"{student.user_id}-final-verify-failed", result)
                return result
        finally:
            await context.close()

    async def reset_students_for_cleanup(self, browser: Browser) -> list[dict[str, Any]]:
        results = []
        for student in self.students:
            context = await browser.new_context(viewport={"width": 1366, "height": 768})
            page = await context.new_page()
            try:
                await self.goto_with_retry(page, self.config.base_url, f"{student.user_id}-reset-cleanup", attempts=4)
                await page.get_by_placeholder("请输入用户名").fill(student.user_id)
                await page.get_by_placeholder("请输入密码").fill(student.password)
                await page.locator("button").filter(has_text=re.compile(r"登\s*录")).first.click()
                await page.wait_for_timeout(2500)
                result = await page.evaluate(
                    """async () => {
                        const response = await fetch('/erpn/user/replay', { method: 'POST', credentials: 'include' });
                        const text = await response.text();
                        try { return JSON.parse(text); } catch { return text; }
                    }"""
                )
                results.append({"userId": student.user_id, "result": result})
            finally:
                await context.close()
        self.log("students-reset-before-delete", results)
        return results

    async def quit_students_from_group_for_cleanup(self, browser: Browser) -> list[dict[str, Any]]:
        results = []
        for student in self.students:
            context = await browser.new_context(viewport={"width": 1366, "height": 768})
            page = await context.new_page()
            try:
                await self.goto_with_retry(page, self.config.base_url, f"{student.user_id}-quit-cleanup", attempts=4)
                await page.get_by_placeholder("请输入用户名").fill(student.user_id)
                await page.get_by_placeholder("请输入密码").fill(student.password)
                await page.locator("button").filter(has_text=re.compile(r"登\s*录")).first.click()
                await page.wait_for_timeout(2500)
                result = await self.api_post(
                    page,
                    f"/erpn/group/quit?groupName={quote(self.config.group_name, safe='')}&userId={quote(student.user_id, safe='')}",
                )
                results.append({"userId": student.user_id, "result": result, "url": page.url})
            except Exception as error:
                results.append({"userId": student.user_id, "error": str(error)})
            finally:
                await context.close()
        self.log("students-quit-group-before-delete", results)
        return results

    async def cleanup_students(self, page: Page, quit_results: list[dict[str, Any]] | None = None) -> dict[str, Any]:
        user_ids = ",".join(quote(student.user_id, safe="") for student in self.students)
        result = await self.api_get(page, f"/erpn/teacher/deletestus?userIds={user_ids}")
        users = await self.data_get(page, "/erpn/teacher/findAllApprovedUser")
        expected = {student.user_id for student in self.students}
        remaining = []
        if isinstance(users, list):
            remaining = [user for user in users if user.get("userId") in expected]
        cleanup = {
            "userIds": [student.user_id for student in self.students],
            "deleteResult": result,
            "remaining": remaining,
            "quitGroupResults": quit_results or [],
        }
        diagnosis = cleanup_diagnosis(cleanup)
        if diagnosis:
            cleanup["diagnosis"] = diagnosis
        self.log("teacher-delete-students", cleanup)
        return cleanup

    async def run(self) -> Path:
        user_csv_path = write_user_csv(self.config.output_dir, self.students)
        game_csv_path = write_import_csv(self.config.output_dir, self.students)
        cleanup_result: dict[str, Any] | None = None
        error_result: dict[str, Any] | None = None
        async with async_playwright() as playwright:
            browser = await playwright.chromium.launch(
                channel=self.config.browser_channel,
                headless=self.config.headless,
            )
            sessions: list[StudentSession] = []
            try:
                teacher_page = await self.login_teacher(browser)
                await self.import_registered_students(teacher_page, user_csv_path)
                await self.import_students_and_create_game(teacher_page, game_csv_path)
                sessions = [await self.login_student(browser, student) for student in self.students]
                await self.run_business(sessions)
                for session in sessions:
                    await session.context.close()
                sessions = []
                for student in self.students:
                    self.final.append(await self.final_verify(browser, student))
                if self.config.cleanup and self.teacher_page:
                    quit_results = await self.quit_students_from_group_for_cleanup(browser)
                    cleanup_result = await self.cleanup_students(self.teacher_page, quit_results)
                    if not cleanup_deleted(cleanup_result):
                        raise RuntimeError("清理失败：仍有测试账号残留，详见 report.json 的 cleanup.remaining")
            except Exception as error:
                error_result = {"type": type(error).__name__, "message": str(error)}
                self.log("run-error", error_result)
                if self.config.cleanup and self.teacher_page and not cleanup_deleted(cleanup_result):
                    try:
                        quit_results = await self.quit_students_from_group_for_cleanup(browser)
                        cleanup_result = await self.cleanup_students(self.teacher_page, quit_results)
                    except Exception as cleanup_error:
                        cleanup_result = {"error": str(cleanup_error)}
                raise
            finally:
                for session in sessions:
                    await session.context.close()
                if self.teacher_context:
                    await self.teacher_context.close()
                await browser.close()
                await self.save_report(
                    {
                        "cleanup": cleanup_result,
                        "error": error_result,
                        "finishedAt": time.strftime("%Y-%m-%dT%H:%M:%S%z"),
                    }
                )
        return await self.save_report({"cleanup": cleanup_result, "error": error_result, "finishedAt": time.strftime("%Y-%m-%dT%H:%M:%S%z")})


def parse_args() -> RunnerConfig:
    parser = argparse.ArgumentParser(description="ERP 竞赛系统端到端浏览器测试")
    parser.add_argument("--base-url", default=BASE_URL)
    parser.add_argument("--teacher", default="wangyong")
    parser.add_argument("--teacher-password", default="123456")
    parser.add_argument("--run-id", default="T" + time.strftime("%Y%m%d%H%M%S"))
    parser.add_argument("--group-name", default=None)
    parser.add_argument("--output-dir", default=None)
    parser.add_argument("--browser-channel", default="msedge")
    parser.add_argument("--headless", action="store_true")
    parser.add_argument("--max-cycles", type=int, default=80)
    parser.add_argument("--no-cleanup", action="store_true")
    parser.add_argument("--check-env", action="store_true", help="只检查 Python、ERP 地址和本机浏览器环境，不创建比赛")
    parser.add_argument(
        "--strategy",
        choices=["expanded", "basic-p1"],
        default="expanded",
        help="expanded 会研发 P2/P3/P4、开拓市场、做 ISO 并采购 R1-R4；basic-p1 保留旧的 P1 基础流",
    )
    args = parser.parse_args()
    group_name = args.group_name or f"AUTO{args.run_id[-10:]}"
    output_dir = Path(args.output_dir or Path("target") / "erp-python-full-competition" / args.run_id).resolve()
    return RunnerConfig(
        base_url=args.base_url,
        teacher_user=args.teacher,
        teacher_password=args.teacher_password,
        run_id=args.run_id,
        group_name=group_name,
        output_dir=output_dir,
        browser_channel=args.browser_channel,
        headless=args.headless,
        max_cycles=args.max_cycles,
        cleanup=not args.no_cleanup,
        check_env=args.check_env,
        strategy=args.strategy,
    )


async def main() -> None:
    config = parse_args()
    if config.check_env:
        await check_environment(config)
        return
    students = generate_students(config.run_id)
    runner = ErpCompetitionRunner(config, students)
    report_path = await runner.run()
    print(f"REPORT={report_path}")


if __name__ == "__main__":
    asyncio.run(main())
