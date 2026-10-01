const fs = require("fs/promises");
const path = require("path");
const { chromium } = require("playwright");

const BASE_URL = process.env.ERP_BASE_URL || "http://39.98.115.237/";
const PASSWORD = process.env.ERP_PASSWORD || "123456";
const ACCOUNTS = (process.env.ERP_ACCOUNTS || "黄进2024211137,张雪皎20242109,王永101311")
  .split(",")
  .map((userId) => userId.trim())
  .filter(Boolean);
const OUT_DIR = path.resolve("target", "erp-ui-click-runner");
const MAX_CYCLES = Number(process.env.ERP_MAX_CYCLES || 80);
const STOP_AT = process.env.ERP_STOP_AT_YEAR_PERIOD || "第6年，第4期";

const summary = {
  startedAt: new Date().toISOString(),
  baseUrl: BASE_URL,
  accounts: ACCOUNTS,
  steps: [],
  final: [],
};

const MENU_PATHS = {
  "/gameoperate/applyLoan": ["贷款与融资", "申请贷款"],
  "/gameoperate/materialPurchase": ["采购管理", "购买原料"],
  "/gameoperate/deliveryWithOrder": ["销售管理", "按订单交货"],
  "/gameoperate/workshopManagement": ["生产管理", "厂房管理"],
  "/gameoperate/advertisement": ["市场管理", "广告投放 & 订单"],
  "/gameoperate/time": ["时间管理"],
};

function stamp() {
  return new Date().toISOString();
}

async function ensureDir() {
  await fs.mkdir(OUT_DIR, { recursive: true });
}

async function saveSummary() {
  await ensureDir();
  await fs.writeFile(
    path.join(OUT_DIR, "summary.json"),
    JSON.stringify(summary, null, 2),
    "utf8"
  );
}

function log(name, data = {}) {
  const entry = { at: stamp(), name, data };
  summary.steps.push(entry);
  console.log(`[${entry.at}] ${name}`);
}

async function textOf(page, selector = "body") {
  return await page.locator(selector).innerText({ timeout: 3000 }).catch(() => "");
}

async function screenshot(page, account, label) {
  await ensureDir();
  const safeAccount = account.replace(/[\\/:*?"<>|]/g, "_");
  const file = path.join(OUT_DIR, `${safeAccount}-${label}.png`);
  await page.screenshot({ path: file, fullPage: true });
  return file;
}

async function clickIfVisible(page, locator, label, timeout = 1500) {
  const count = await locator.count().catch(() => 0);
  if (count < 1) return false;
  const target = locator.first();
  const visible = await target.isVisible({ timeout }).catch(() => false);
  if (!visible) return false;
  await target.click();
  await page.waitForTimeout(500);
  log(label);
  return true;
}

async function confirmModal(page) {
  const ok = page.locator(".ant-modal-confirm-btns button").filter({ hasText: "确认" });
  if (await clickIfVisible(page, ok, "modal-confirm")) return true;
  const spacedOk = page.locator(".ant-modal-confirm-btns button").filter({ hasText: "确 认" });
  if (await clickIfVisible(page, spacedOk, "modal-confirm")) return true;
  const primaryOk = page.locator(".ant-modal-confirm-btns .ant-btn-primary");
  if (await clickIfVisible(page, primaryOk, "modal-confirm")) return true;
  return await clickIfVisible(page, page.getByRole("button", { name: "确认" }), "modal-confirm");
}

async function loginUi(page, account) {
  await page.goto(BASE_URL);
  await page.waitForLoadState("domcontentloaded");
  await page.getByPlaceholder("请输入用户名").fill(account);
  await page.getByPlaceholder("请输入密码").fill(PASSWORD);
  await page.getByRole("button", { name: "登 录" }).click();
  await page.waitForTimeout(4500);
  log(`${account}-login-clicked`, { url: page.url() });
}

async function openRoute(page, route, label) {
  const menuPath = MENU_PATHS[route];
  if (!menuPath) {
    throw new Error(`No menu path configured for ${route}`);
  }

  const menu = page.locator(".ant-menu").first();
  for (const menuText of menuPath) {
    const item = menu.getByText(menuText, { exact: true });
    if ((await item.count().catch(() => 0)) === 0) {
      throw new Error(`Menu item not found: ${menuText}`);
    }
    await item.first().click();
    await page.waitForTimeout(600);
  }
  await page.waitForTimeout(1200);
  log(label, { url: page.url() });
}

async function applyLoanByClick(page, account, type = "高利贷", money = 30, term = 4) {
  await openRoute(page, "/gameoperate/applyLoan", `${account}-open-apply-loan`);

  const loanArea = {
    长期贷款: ".long-loan",
    短期贷款: ".short-loan",
    高利贷: ".high-loan",
  }[type];
  if (!loanArea) return false;

  if (!(await clickIfVisible(page, page.locator(loanArea), `${account}-click-${type}`, 2500))) {
    return false;
  }

  const moneyInput = page.locator(".loanModal .money-input input");
  if ((await moneyInput.count()) > 0) {
    await moneyInput.fill(String(money));
  }

  const select = page.locator(".loanModal .ant-select-selector").first();
  if ((await select.count()) > 0) {
    await select.click();
    await page.waitForTimeout(300);
    const option = page.locator(".ant-select-item-option").filter({ hasText: String(term) }).first();
    if ((await option.count()) > 0) await option.click();
  }

  const apply = page.locator(".loanModal button").filter({ hasText: "申请" });
  if (await clickIfVisible(page, apply, `${account}-submit-loan`, 2500)) {
    await page.waitForTimeout(1200);
    return true;
  }
  return false;
}

async function buyMaterialByClick(page, account, materialName = "R1", amount = 4) {
  await openRoute(page, "/gameoperate/materialPurchase", `${account}-open-material-purchase`);
  const cards = page.locator(".content-item .item");
  const count = await cards.count();
  for (let index = 0; index < count; index += 1) {
    const card = cards.nth(index);
    const text = await card.innerText().catch(() => "");
    if (!text.includes(materialName)) continue;
    await card.locator("input").fill(String(amount));
    await card.getByRole("button", { name: "购买" }).click();
    await page.waitForTimeout(300);
    await confirmModal(page);
    await page.waitForTimeout(1000);
    log(`${account}-buy-${materialName}-${amount}`);
    return true;
  }
  return false;
}

async function deliverOrdersByClick(page, account) {
  await openRoute(page, "/gameoperate/deliveryWithOrder", `${account}-open-delivery`);
  for (let attempt = 0; attempt < 8; attempt += 1) {
    const buttons = page.locator("button.delivery-button").filter({ hasText: "交货" });
    const count = await buttons.count().catch(() => 0);
    if (count === 0) break;
    await buttons.first().click();
    await page.waitForTimeout(1000);
    log(`${account}-click-delivery`, { remainingBeforeClick: count });
  }
}

async function startProductionByClick(page, account) {
  await openRoute(page, "/gameoperate/workshopManagement", `${account}-open-workshop`);
  for (let attempt = 0; attempt < 8; attempt += 1) {
    const buttons = page.locator("button").filter({ hasText: "开始生产" });
    const count = await buttons.count().catch(() => 0);
    if (count === 0) break;
    await buttons.first().click();
    await page.waitForTimeout(800);
    log(`${account}-click-start-production`, { remainingBeforeClick: count });
  }
}

async function annualPlanningByClick(page, account) {
  await openRoute(page, "/gameoperate/advertisement", `${account}-open-advertisement`);

  if ((await page.getByRole("button", { name: "已投放完毕，下一步>" }).count().catch(() => 0)) > 0) {
    const rows = page.locator("tbody tr");
    const rowCount = await rows.count().catch(() => 0);
    for (let index = 0; index < rowCount; index += 1) {
      const row = rows.nth(index);
      const rowText = await row.innerText().catch(() => "");
      if (!rowText.includes("P1")) continue;
      await row.locator("input").fill("1");
      await row.getByRole("button", { name: "提交" }).click();
      await page.waitForTimeout(700);
      log(`${account}-put-ad-p1`);
      break;
    }
    await clickIfVisible(page, page.getByRole("button", { name: "已投放完毕，下一步>" }), `${account}-ad-next`, 2500);
  }

  const confirmAdButton = page.getByRole("button", { name: "确认订单，下一步 >" });
  if ((await confirmAdButton.count().catch(() => 0)) > 0) {
    await confirmAdButton.click();
    await page.waitForTimeout(300);
    await confirmModal(page);
    await page.waitForTimeout(1500);
    log(`${account}-confirm-ad`);
  }

  const canChooseCount = await page.locator("button.pBtn").filter({ hasText: "选择订单" }).count().catch(() => 0);
  const canFinishCount = await page.locator("button.forthBtn").filter({ hasText: "结束选单" }).count().catch(() => 0);
  if (canChooseCount > 0 || canFinishCount > 0) {
    for (let attempt = 0; attempt < 8; attempt += 1) {
      const choose = page.locator("button.pBtn").filter({ hasText: "选择订单" });
      if ((await choose.count().catch(() => 0)) === 0) break;
      await choose.first().click();
      await page.waitForTimeout(300);
      await confirmModal(page);
      await page.waitForTimeout(1200);
      log(`${account}-choose-order`);
    }
    const endButton = page.locator("button.forthBtn").filter({ hasText: "结束选单" });
    if (await clickIfVisible(page, endButton, `${account}-open-finish-order-modal`, 2500)) {
      await page.waitForTimeout(300);
      await clickIfVisible(
        page,
        page.locator(".ant-modal button").filter({ hasText: "结束所有订单" }),
        `${account}-finish-all-orders`,
        2500
      );
    }
  }
}

async function advanceByClick(page, account) {
  await openRoute(page, "/gameoperate/time", `${account}-open-time`);
  const before = await textOf(page);
  if (before.includes(STOP_AT) || before.includes("比赛完毕") || page.url().includes("/gameover")) {
    log(`${account}-skip-advance-complete`, { marker: STOP_AT });
    return false;
  }
  const image = page.locator('img[alt="进入下一周期"]');
  if ((await image.count().catch(() => 0)) === 0) return false;
  await image.click();
  await page.waitForTimeout(300);
  await confirmModal(page);
  await page.waitForTimeout(2500);
  log(`${account}-advance-click`, { url: page.url(), text: (await textOf(page)).slice(0, 300) });
  return true;
}

async function operateOneCycleByClick(page, account) {
  await annualPlanningByClick(page, account);
  await deliverOrdersByClick(page, account);
  await startProductionByClick(page, account);
  await buyMaterialByClick(page, account, "R1", 4);
  const body = await textOf(page);
  if (/现金:\s*[0-9.]+/.test(body) && body.includes("现金: 0")) {
    await applyLoanByClick(page, account, "高利贷", 30, 4);
  }
  await advanceByClick(page, account);
}

async function runAccount(browser, account) {
  const context = await browser.newContext({ viewport: { width: 1366, height: 768 } });
  const page = await context.newPage();
  try {
    await loginUi(page, account);
    await page.waitForTimeout(2500);
    const loginText = await textOf(page);
    const loginShot = await screenshot(page, account, "after-login");
    log(`${account}-after-login`, { url: page.url(), text: loginText.slice(0, 500), screenshot: loginShot });

    if (loginText.includes("比赛完毕") || loginText.includes(STOP_AT) || page.url().includes("/gameover")) {
      summary.final.push({ account, status: "比赛完毕", screenshot: loginShot });
      return;
    }

    for (let cycle = 0; cycle < MAX_CYCLES; cycle += 1) {
      const currentText = await textOf(page);
      if (currentText.includes("比赛完毕") || currentText.includes(STOP_AT) || page.url().includes("/gameover")) {
        summary.final.push({ account, status: "比赛完毕", cycle });
        return;
      }
      await operateOneCycleByClick(page, account);
      await saveSummary();
    }

    const finalShot = await screenshot(page, account, "max-cycle-stop");
    summary.final.push({ account, status: "达到最大循环未完赛", screenshot: finalShot });
  } finally {
    await context.close();
  }
}

(async () => {
  const browser = await chromium.launch({ channel: "msedge", headless: false });
  try {
    for (const account of ACCOUNTS) {
      await runAccount(browser, account);
      await saveSummary();
    }
  } finally {
    summary.finishedAt = stamp();
    await saveSummary();
    await browser.close();
  }
})().catch(async (error) => {
  summary.error = { message: error.message, stack: error.stack };
  await saveSummary();
  console.error(error);
  process.exit(1);
});
