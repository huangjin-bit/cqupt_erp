const fs = require("fs/promises");
const path = require("path");
const { chromium } = require("playwright");

const BASE_URL = process.env.ERP_BASE_URL || "http://39.98.115.237/";
const PASSWORD = process.env.ERP_PASSWORD || "123456";
const ACCOUNTS = (process.env.ERP_ACCOUNTS || "黄进2024211137,张雪皎20242109,王永101311")
  .split(",")
  .map((userId) => userId.trim())
  .filter(Boolean);
const GROUP_NAME = process.env.ERP_GROUP_NAME || `UI${Date.now().toString().slice(-8)}`;
const OUT_DIR = path.resolve("target", "erp-ui-full-match");
const MAX_CYCLES = Number(process.env.ERP_MAX_CYCLES || 80);
const SKIP_PREPARE = process.env.ERP_SKIP_PREPARE === "1";
const CASH_LOAN_FLOOR = Number(process.env.ERP_CASH_LOAN_FLOOR || 30);
const CASH_LOAN_TARGET = Number(process.env.ERP_CASH_LOAN_TARGET || 45);
const HIGH_LOAN_UI_MAX = 99999;

const MENU_PATHS = {
  "/gameoperate/applyLoan": ["贷款与融资", "申请贷款"],
  "/gameoperate/materialPurchase": ["采购管理", "购买原料"],
  "/gameoperate/deliveryWithOrder": ["销售管理", "按订单交货"],
  "/gameoperate/workshopManagement": ["生产管理", "厂房管理"],
  "/gameoperate/advertisement": ["市场管理", "广告投放 & 订单"],
  "/gameoperate/time": ["时间管理"],
};

const summary = {
  startedAt: new Date().toISOString(),
  baseUrl: BASE_URL,
  groupName: GROUP_NAME,
  accounts: ACCOUNTS,
  steps: [],
  final: [],
};

async function ensureDir() {
  await fs.mkdir(OUT_DIR, { recursive: true });
}

async function saveSummary() {
  await ensureDir();
  await fs.writeFile(path.join(OUT_DIR, "summary.json"), JSON.stringify(summary, null, 2), "utf8");
}

function log(name, data = {}) {
  const entry = { at: new Date().toISOString(), name, data };
  summary.steps.push(entry);
  console.log(`[${entry.at}] ${name}`);
}

async function screenshot(page, account, label) {
  await ensureDir();
  const safeAccount = account.replace(/[\\/:*?"<>|]/g, "_");
  const file = path.join(OUT_DIR, `${safeAccount}-${label}.png`);
  await page.screenshot({ path: file, fullPage: true });
  return file;
}

async function textOf(page, selector = "body") {
  return await page.locator(selector).innerText({ timeout: 3000 }).catch(() => "");
}

async function clickIfVisible(page, locator, label, timeout = 1800) {
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
  let clicked = false;
  for (let i = 0; i < 4; i += 1) {
    const candidates = [
      page.locator(".ant-modal-confirm-btns .ant-btn-primary"),
      page.locator(".ant-popover-buttons .ant-btn-primary"),
      page.locator(".ant-modal button").filter({ hasText: "确认" }),
      page.locator(".ant-modal button").filter({ hasText: "确 认" }),
      page.getByRole("button", { name: "确认" }),
      page.getByRole("button", { name: "确 认" }),
    ];
    let clickedThisRound = false;
    for (const candidate of candidates) {
      const visible = candidate.filter({ visible: true });
      const count = await visible.count().catch(() => 0);
      if (count < 1) continue;
      await visible.last().click({ timeout: 2500, force: true }).catch(async () => {
        const box = await visible.last().boundingBox().catch(() => null);
        if (!box) throw new Error("confirm button is not clickable");
        await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2);
      });
      await page.waitForTimeout(700);
      log("confirm-dialog");
      clicked = true;
      clickedThisRound = true;
      break;
    }
    if (!clickedThisRound) break;
  }
  return clicked;
}

async function closeAnyModal(page) {
  await confirmModal(page);
  await page.waitForTimeout(300);
  const closeButton = page.locator(".ant-modal-close").filter({ visible: true });
  if ((await closeButton.count().catch(() => 0)) > 0) {
    await closeButton.first().click({ force: true, timeout: 1000 }).catch(() => {});
    await page.waitForTimeout(300);
    log("close-modal");
    return true;
  }
  return false;
}

async function apiGet(page, url) {
  return await page.evaluate(async (url) => {
    const response = await fetch(url, { credentials: "include" });
    const text = await response.text();
    let body;
    try {
      body = JSON.parse(text);
    } catch {
      body = text;
    }
    return { httpStatus: response.status, body };
  }, url);
}

function dataOf(result) {
  return result && result.body ? result.body.data : undefined;
}

async function getInfo(page) {
  const result = await apiGet(page, "/erpn/user/info");
  return dataOf(result);
}

async function getLoanInfo(page) {
  const result = await apiGet(page, "/erpn/loan/info");
  return dataOf(result) || [];
}

function absPeriod(info) {
  return (Number(info.years) - 1) * 4 + Number(info.currentPeriod);
}

async function loginUi(page, account) {
  await page.goto(BASE_URL);
  await page.waitForLoadState("domcontentloaded");
  await page.getByPlaceholder("请输入用户名").fill(account);
  await page.getByPlaceholder("请输入密码").fill(PASSWORD);
  await page.getByRole("button", { name: "登 录" }).click();
  await page.waitForTimeout(4500);
  log(`${account}-login`, { url: page.url(), text: (await textOf(page)).slice(0, 200) });
}

async function menuOpen(page, route, label) {
  await closeAnyModal(page);
  if (page.url().includes(`#${route}`)) {
    await page.waitForTimeout(800);
    log(label, { url: page.url(), alreadyThere: true });
    return;
  }
  const menuPath = MENU_PATHS[route];
  if (!menuPath) throw new Error(`No menu path for ${route}`);
  const menu = page.locator(".ant-menu").first();
  const topItem = menu.getByText(menuPath[0], { exact: true }).filter({ visible: true });
  if ((await topItem.count().catch(() => 0)) > 0) {
    await topItem.first().click();
    await page.waitForTimeout(700);
  }

  if (menuPath.length > 1) {
    const href = page.locator(`.ant-menu a[href="#${route}"]`).filter({ visible: true });
    if ((await href.count().catch(() => 0)) > 0) {
      await href.first().click();
      await page.waitForTimeout(1200);
      log(label, { url: page.url() });
      return;
    }

    const leafText = menuPath[menuPath.length - 1];
    const leaf = menu.getByText(leafText, { exact: true }).filter({ visible: true });
    if ((await leaf.count().catch(() => 0)) > 0) {
      await leaf.first().click();
      await page.waitForTimeout(1200);
      log(label, { url: page.url() });
      return;
    }

    await page.goto(new URL(`/#${route}`, BASE_URL).toString());
    await page.waitForTimeout(1800);
    log(label, { url: page.url(), recoveredByRoute: true, reason: `Menu item not found: ${leafText}` });
    return;
  }

  const href = page.locator(`.ant-menu a[href="#${route}"]`).filter({ visible: true });
  if ((await href.count().catch(() => 0)) > 0) {
    await href.first().click();
  } else if ((await topItem.count().catch(() => 0)) < 1) {
    await page.goto(new URL(`/#${route}`, BASE_URL).toString());
  }
  await page.waitForTimeout(1000);
  log(label, { url: page.url() });
}

async function restartAccountByClick(browser, account) {
  const context = await browser.newContext({ viewport: { width: 1366, height: 768 } });
  const page = await context.newPage();
  try {
    await loginUi(page, account);
    await page.goto(new URL("/#/gameover/checkJournalEntry", BASE_URL).toString());
    await page.waitForTimeout(2500);
    const restarted = await clickIfVisible(
      page,
      page.locator(".ant-menu").getByText("重新开始", { exact: true }),
      `${account}-click-restart`,
      3000
    );
    if (restarted) {
      await page.waitForTimeout(300);
      await confirmModal(page);
      await page.waitForTimeout(2500);
    }
    log(`${account}-restart-result`, { url: page.url(), text: (await textOf(page)).slice(0, 300) });
  } finally {
    await context.close();
  }
}

async function createGroupByClick(page, creator) {
  await loginUi(page, creator);
  await page.getByText("创建比赛", { exact: true }).click();
  await page.waitForTimeout(1200);

  const form = page.locator(".create-game-wrapper form");
  await form.locator("input").first().fill(GROUP_NAME);
  await form.locator(".ant-input-number input").fill(String(ACCOUNTS.length));
  await form.locator(".ant-select-selector").click();
  await page.waitForTimeout(500);
  const teacherOption = page.locator(".ant-select-item-option").filter({ hasText: "王永" });
  if ((await teacherOption.count().catch(() => 0)) > 0) {
    await teacherOption.first().click();
  } else {
    await page.locator(".ant-select-item-option").filter({ hasNotText: /^$/ }).first().click();
  }
  await page.waitForTimeout(300);
  await page.getByRole("button", { name: "创建比赛" }).click();
  await page.waitForTimeout(2500);
  log(`${creator}-created-group`, { groupName: GROUP_NAME, text: (await textOf(page)).slice(0, 400) });
}

async function joinGroupByClick(page, account) {
  await loginUi(page, account);
  await page.getByText("加入分组", { exact: true }).click();
  await page.waitForTimeout(2000);
  const card = page.locator(".card-wrapper").filter({ hasText: GROUP_NAME });
  if ((await card.count().catch(() => 0)) < 1) {
    throw new Error(`${account} cannot find group ${GROUP_NAME}`);
  }
  await card.first().locator(".icon").click();
  await page.waitForTimeout(2500);
  log(`${account}-joined-group`, { groupName: GROUP_NAME, text: (await textOf(page)).slice(0, 300) });
}

async function startGameByClick(page, creator) {
  await page.locator(".ant-menu a").filter({ hasText: "开始比赛" }).first().click();
  await page.waitForTimeout(1200);
  await page.getByRole("button", { name: "开始比赛" }).click();
  await page.waitForTimeout(4500);
  log(`${creator}-started-game`, { url: page.url(), text: (await textOf(page)).slice(0, 300) });
}

async function prepareGame(browser) {
  for (const account of ACCOUNTS) {
    await restartAccountByClick(browser, account);
    await saveSummary();
  }

  const sessions = [];
  for (const account of ACCOUNTS) {
    const context = await browser.newContext({ viewport: { width: 1366, height: 768 } });
    const page = await context.newPage();
    sessions.push({ account, context, page, complete: false, bankrupt: false });
  }

  await loginUi(sessions[0].page, sessions[0].account);
  const creatorText = await textOf(sessions[0].page);
  if (creatorText.includes("组名：") && creatorText.includes("开始比赛")) {
    log(`${sessions[0].account}-reuse-existing-group`, { text: creatorText.slice(0, 400) });
  } else {
    await createGroupByClick(sessions[0].page, sessions[0].account);
    for (const session of sessions.slice(1)) {
      await joinGroupByClick(session.page, session.account);
    }
  }
  await startGameByClick(sessions[0].page, sessions[0].account);

  for (const session of sessions.slice(1)) {
    await loginUi(session.page, session.account);
    await session.page.waitForTimeout(2500);
    const text = await textOf(session.page);
    if (!session.page.url().includes("/gameoperate")) {
      await session.page.goto(new URL("/#/gameoperate/main", BASE_URL).toString());
      await session.page.waitForTimeout(2500);
    }
    log(`${session.account}-entered-game`, { url: session.page.url(), text: text.slice(0, 200) });
  }
  await saveSummary();
  return sessions;
}

async function enterExistingGame(browser) {
  const sessions = [];
  for (const account of ACCOUNTS) {
    const context = await browser.newContext({ viewport: { width: 1366, height: 768 } });
    const page = await context.newPage();
    await loginUi(page, account);
    if (!page.url().includes("/gameoperate")) {
      await page.goto(new URL("/#/gameoperate/main", BASE_URL).toString());
      await page.waitForTimeout(2500);
    }
    log(`${account}-entered-existing-game`, { url: page.url(), text: (await textOf(page)).slice(0, 250) });
    sessions.push({ account, context, page, complete: false, bankrupt: false });
  }
  await saveSummary();
  return sessions;
}

async function applyLoanByClick(session, type = "高利贷", money = 100, term = 4) {
  const { page, account } = session;
  try {
    await menuOpen(page, "/gameoperate/applyLoan", `${account}-open-loan`);
    const loanArea = { 长期贷款: ".long-loan", 短期贷款: ".short-loan", 高利贷: ".high-loan" }[type];
    const area = page.locator(loanArea);
    const box = await area.boundingBox().catch(() => null);
    if (!box) return false;
    await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2);
    await page.waitForTimeout(1500);
    log(`${account}-click-${type}`);
    const moneyInput = page.locator(".loanModal input.money-input").filter({ visible: true });
    if ((await moneyInput.count().catch(() => 0)) < 1) {
      log(`${account}-loan-modal-not-open`, { type, money });
      return false;
    }
    await moneyInput.first().fill(String(money));
    await page.locator(".loanModal .ant-select-selector").filter({ visible: true }).first().click();
    await page.waitForTimeout(400);
    await page.locator(".ant-select-item-option").filter({ hasText: String(term), visible: true }).first().click();
    await page.waitForTimeout(300);
    await page.keyboard.press("Escape").catch(() => {});
    const applyButton = page.locator(".loanModal button.bottom-btn").filter({ hasText: /申\s*请/, visible: true });
    if ((await applyButton.count().catch(() => 0)) < 1) {
      log(`${account}-loan-apply-button-not-found`, { type, money, text: (await textOf(page)).slice(0, 250) });
      return false;
    }
    await applyButton.first().click({ timeout: 3000, force: true }).catch(async () => {
      const box = await applyButton.first().boundingBox().catch(() => null);
      if (!box) throw new Error("loan apply button is not clickable");
      await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2);
    });
    await page.waitForTimeout(1500);
    log(`${account}-loan-${type}-${money}`, { text: (await textOf(page)).slice(0, 250) });
    return true;
  } catch (error) {
    log(`${account}-loan-failed`, { type, money, message: error.message });
    await closeAnyModal(page);
    return false;
  }
}

function loanByType(loanInfo, type) {
  return loanInfo.find((item) => item.loanTypeName === type);
}

function positiveInteger(value) {
  return Math.max(0, Math.floor(Number(value) || 0));
}

function maxTerm(loan) {
  return positiveInteger(loan && (loan.permitPeriod || loan.permitYear));
}

function buildLoanPlan(info, loanInfo) {
  const cash = Number(info.cash);
  if (!Number.isFinite(cash) || cash >= CASH_LOAN_FLOOR) return null;

  const requested = Math.max(1, Math.ceil(CASH_LOAN_TARGET - cash));
  const candidates = [];
  const shortLoan = loanByType(loanInfo, "短期贷款");
  const longLoan = loanByType(loanInfo, "长期贷款");
  const highLoan = loanByType(loanInfo, "高利贷");

  if (shortLoan) {
    const maxMoney = positiveInteger(shortLoan.maxOfLoan);
    const term = maxTerm(shortLoan);
    if (maxMoney > 0 && term > 0) {
      candidates.push({ type: "短期贷款", money: Math.min(requested, maxMoney), term });
    }
  }

  if (longLoan) {
    const maxMoney = positiveInteger(longLoan.maxOfLoan);
    const term = maxTerm(longLoan);
    const remainingYears = Number(longLoan.totalYear) - Number(longLoan.currentYear);
    if (maxMoney > 0 && term >= 2 && Number(longLoan.beginYear) === 1 && remainingYears >= 2) {
      candidates.push({ type: "长期贷款", money: Math.min(requested, maxMoney), term: Math.min(term, remainingYears) });
    }
  }

  if (highLoan) {
    const term = maxTerm(highLoan);
    if (term > 0) {
      candidates.push({ type: "高利贷", money: Math.min(requested, HIGH_LOAN_UI_MAX), term });
    }
  }

  return candidates.find((candidate) => candidate.money > 0) || null;
}

async function buyR1ByClick(session, amount = 4) {
  const { page, account } = session;
  await menuOpen(page, "/gameoperate/materialPurchase", `${account}-open-material`);
  const cards = page.locator(".content-item .item");
  const count = await cards.count().catch(() => 0);
  for (let i = 0; i < count; i += 1) {
    const card = cards.nth(i);
    const text = await card.innerText().catch(() => "");
    if (!text.includes("R1")) continue;
    await card.locator("input").fill(String(amount));
    await card.getByRole("button", { name: "购买" }).click();
    await page.waitForTimeout(300);
    await confirmModal(page);
    await page.waitForTimeout(1200);
    log(`${account}-buy-r1-${amount}`);
    return true;
  }
  return false;
}

async function deliverByClick(session) {
  const { page, account } = session;
  await menuOpen(page, "/gameoperate/deliveryWithOrder", `${account}-open-delivery`);
  for (let i = 0; i < 8; i += 1) {
    const buttons = page.locator("button.delivery-button").filter({ hasText: "交货" });
    const count = await buttons.count().catch(() => 0);
    if (count < 1) break;
    await buttons.first().click();
    await page.waitForTimeout(1000);
    log(`${account}-delivery-click`, { before: count });
  }
}

async function startProductionByClick(session) {
  const { page, account } = session;
  await menuOpen(page, "/gameoperate/workshopManagement", `${account}-open-workshop`);
  for (let i = 0; i < 8; i += 1) {
    const buttons = page.locator("button").filter({ hasText: "开始生产" });
    const count = await buttons.count().catch(() => 0);
    if (count < 1) break;
    await buttons.first().click();
    await page.waitForTimeout(900);
    log(`${account}-start-production-click`, { before: count });
  }
}

async function adPutAndConfirmByClick(session) {
  const { page, account } = session;
  await menuOpen(page, "/gameoperate/advertisement", `${account}-open-ad`);
  await page.waitForTimeout(1200);
  const adNext = page.getByRole("button", { name: "已投放完毕，下一步>" });
  if ((await adNext.count().catch(() => 0)) > 0) {
    const rows = page.locator("tbody tr");
    const rowCount = await rows.count().catch(() => 0);
    for (let i = 0; i < rowCount; i += 1) {
      const row = rows.nth(i);
      const text = await row.innerText().catch(() => "");
      if (!text.includes("P1")) continue;
      await row.locator("input").fill("1");
      await row.locator("button").filter({ hasText: /提\s*交/ }).first().click();
      await page.waitForTimeout(800);
      log(`${account}-put-ad-p1`);
      break;
    }
    await adNext.click();
    await page.waitForTimeout(1000);
    log(`${account}-ad-next`);
  }

  const confirmAd = page.getByRole("button", { name: "确认订单，下一步 >" });
  if ((await confirmAd.count().catch(() => 0)) > 0) {
    await confirmAd.click();
    await page.waitForTimeout(300);
    await confirmModal(page);
    await page.waitForTimeout(1500);
    log(`${account}-ad-confirmed`);
  }
}

async function chooseAndFinishOrdersByClick(session) {
  const { page, account } = session;
  await menuOpen(page, "/gameoperate/advertisement", `${account}-open-order-hall`);
  await page.waitForTimeout(2500);
  await closeAnyModal(page);
  for (let i = 0; i < 8; i += 1) {
    const choose = page.locator("button.pBtn").filter({ hasText: "选择订单" });
    const count = await choose.count().catch(() => 0);
    if (count < 1) break;
    await choose.first().click();
    await page.waitForTimeout(300);
    await confirmModal(page);
    await page.waitForTimeout(1500);
    log(`${account}-choose-order-click`, { before: count });
  }

  const finish = page.locator("button.forthBtn").filter({ hasText: "结束选单" });
  let openedFinish = await clickIfVisible(page, finish, `${account}-open-finish-order`, 2500);
  if (!openedFinish) {
    await page.reload({ waitUntil: "domcontentloaded" }).catch(() => {});
    await page.waitForTimeout(3500);
    openedFinish = await clickIfVisible(page, finish, `${account}-open-finish-order-after-reload`, 2500);
  }
  if (openedFinish) {
    const clickedFinishAll = await clickIfVisible(
      page,
      page.locator(".ant-modal button").filter({ hasText: "结束所有订单" }),
      `${account}-finish-all-orders`,
      2500
    );
    if (clickedFinishAll) {
      await page.waitForTimeout(400);
      await confirmModal(page);
      await page.waitForTimeout(1600);
      log(`${account}-confirm-finish-all-orders`);
    }
    await closeAnyModal(page);
  }
}

async function annualPlanningAllByClick(sessions) {
  log("annual-planning-all-start", sessions.map((s) => s.account));
  for (let round = 0; round < 4; round += 1) {
    let pending = false;
    for (const session of sessions) {
      if (session.complete || session.bankrupt) continue;
      const info = await getInfo(session.page).catch(() => null);
      if (!info || Number(info.currentPeriod) !== 1) continue;
      const progress = await getAdProgress(session);
      if (progress === 5) continue;
      pending = true;
      if (progress === 1 || progress === 2) {
        await adPutAndConfirmByClick(session);
      } else if (progress === 4) {
        await chooseAndFinishOrdersByClick(session);
      } else {
        await menuOpen(session.page, "/gameoperate/advertisement", `${session.account}-open-ad-wait`);
      }
    }
    await pageWait(1800);
    if (!pending) break;
  }
  await saveSummary();
}

async function pageWait(ms) {
  await new Promise((resolve) => setTimeout(resolve, ms));
}

async function advanceByClick(session) {
  const { page, account } = session;
  const beforeInfo = await getInfo(page).catch(() => null);
  await menuOpen(page, "/gameoperate/time", `${account}-open-time`);
  const img = page.locator('img[alt="进入下一周期"]');
  if ((await img.count().catch(() => 0)) < 1) return false;
  await img.first().click();
  await page.waitForTimeout(300);
  await confirmModal(page);
  await page.waitForTimeout(3000);
  const text = await textOf(page);
  const afterInfo = await getInfo(page).catch(() => null);
  const moved = !beforeInfo || !afterInfo || absPeriod(beforeInfo) !== absPeriod(afterInfo);
  log(`${account}-advance-click`, { url: page.url(), beforeInfo, afterInfo, moved, text: text.slice(0, 300) });
  if (page.url().includes("/gameover") || text.includes("游戏已结束") || text.includes("比赛完毕")) {
    session.complete = true;
  }
  if (page.url().includes("/gameover")) session.complete = true;
  if (text.includes("破产")) session.bankrupt = true;
  return moved || session.complete || session.bankrupt;
}

async function maybeLoanByState(session) {
  const info = await getInfo(session.page).catch(() => null);
  if (!info) return;
  const loanInfo = await getLoanInfo(session.page).catch(() => []);
  const plan = buildLoanPlan(info, loanInfo);
  log(`${session.account}-loan-plan`, { cash: info.cash, plan, loanInfo });
  if (plan) {
    await applyLoanByClick(session, plan.type, plan.money, plan.term);
  }
}

async function isYearBeginNeedsPlanning(session) {
  const info = await getInfo(session.page).catch(() => null);
  if (!info) return false;
  const adProgress = await getAdProgress(session);
  return Number(info.currentPeriod) === 1 && adProgress !== 5;
}

async function getAdProgress(session) {
  const progress = await apiGet(session.page, "/erpn/ad/getCurrentProgress").catch(() => null);
  return Number(dataOf(progress));
}

async function captureState(sessions, label) {
  const states = [];
  for (const session of sessions) {
    const info = await getInfo(session.page).catch((error) => ({ error: String(error) }));
    const loginText = await textOf(session.page);
    states.push({
      account: session.account,
      url: session.page.url(),
      info,
      text: loginText.slice(0, 160),
    });
  }
  log(label, states);
  return states;
}

async function runBusinessToCompletion(sessions) {
  for (let cycle = 0; cycle < MAX_CYCLES; cycle += 1) {
    const states = await captureState(sessions, `cycle-${cycle}-state`);
    for (const session of sessions) {
      const state = states.find((item) => item.account === session.account);
      if (state && (state.url.includes("/gameover") || state.text.includes("游戏已结束") || state.text.includes("比赛完毕"))) {
        session.complete = true;
      }
      if (state && state.text.includes("破产")) session.bankrupt = true;
    }
    if (sessions.every((session) => session.complete || session.bankrupt)) break;

    const needsPlanning = [];
    for (const session of sessions) {
      if (!session.complete && !session.bankrupt && (await isYearBeginNeedsPlanning(session))) {
        needsPlanning.push(session);
      }
    }
    if (needsPlanning.length > 0) await annualPlanningAllByClick(needsPlanning);

    for (const session of sessions) {
      if (session.complete || session.bankrupt) continue;
      await maybeLoanByState(session);
      await deliverByClick(session);
      await startProductionByClick(session);
      await buyR1ByClick(session, 4);
      await maybeLoanByState(session);
    }

    for (const session of sessions) {
      if (session.complete || session.bankrupt) continue;
      const advanced = await advanceByClick(session);
      if (!advanced && (await isYearBeginNeedsPlanning(session))) {
        const blockedSessions = [];
        for (const item of sessions) {
          if (!item.complete && !item.bankrupt && (await isYearBeginNeedsPlanning(item))) blockedSessions.push(item);
        }
        await annualPlanningAllByClick(blockedSessions);
        await advanceByClick(session);
      }
    }
    await saveSummary();
  }
}

async function freshVerify(browser, account) {
  const context = await browser.newContext({ viewport: { width: 1366, height: 768 } });
  const page = await context.newPage();
  try {
    await loginUi(page, account);
    const info = await getInfo(page).catch((error) => ({ error: String(error) }));
    const file = await screenshot(page, account, "final");
    const text = await textOf(page);
    return { account, info, url: page.url(), text: text.slice(0, 500), screenshot: file };
  } finally {
    await context.close();
  }
}

async function main() {
  await ensureDir();
  const browser = await chromium.launch({ channel: "msedge", headless: false });
  let sessions = [];
  try {
    sessions = SKIP_PREPARE ? await enterExistingGame(browser) : await prepareGame(browser);
    await runBusinessToCompletion(sessions);
    for (const session of sessions) {
      await session.context.close();
    }
    sessions = [];
    for (const account of ACCOUNTS) {
      const final = await freshVerify(browser, account);
      summary.final.push(final);
      log(`${account}-final-verify`, final);
    }
  } finally {
    for (const session of sessions) {
      await session.context.close().catch(() => {});
    }
    summary.finishedAt = new Date().toISOString();
    await saveSummary();
    await browser.close();
  }
}

if (require.main === module) {
  main().catch(async (error) => {
    summary.error = { message: error.message, stack: error.stack };
    await saveSummary();
    console.error(error);
    process.exit(1);
  });
}

module.exports = {
  buildLoanPlan,
};
