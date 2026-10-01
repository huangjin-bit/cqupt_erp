# ERP 竞赛浏览器自动化测试部署说明

本文档用于把 `scripts/erp_competition_runner.py` 交给其他电脑运行。脚本会通过真实浏览器完成教师导入学生、创建比赛、三个学生经营推进、生成报告，并在结束时删除本次创建的测试账号。

## 1. 运行前必须具备

- Windows 10/11。
- Python 3.10 或更高版本。
- Microsoft Edge 浏览器。
- 目标机器可以访问 ERP 地址，默认是 `http://39.98.115.237/`。
- 教师账号可用，默认是 `wangyong / 123456`。
- 数据库里没有同名测试账号残留；如果残留，需要先清理 `approveduser` 和 `gamegroupmember`。

脚本默认使用本机 Edge：

```powershell
--browser-channel msedge
```

因此不需要执行 `playwright install` 下载 Playwright 自带 Chromium。

## 2. 首次安装

进入前端项目目录，也就是包含 `scripts`、`tests`、`requirements-erp-runner.txt` 的项目根目录：

```powershell
cd <你的项目目录>\erp_pc
```

如果你已经在项目根目录，可以跳过这一步。后续命令都默认在项目根目录执行。

创建并启用虚拟环境：

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
```

安装依赖：

```powershell
python -m pip install -U pip
pip install -r requirements-erp-runner.txt
```

## 3. 环境自检

先运行自检，不会创建账号和比赛：

```powershell
.\scripts\run_erp_competition_test.ps1 -CheckEnv
```

看到下面几项即可继续：

```text
[CHECK] Python: ...
[CHECK] ERP reachable: 200
[CHECK] Browser channel: msedge
[CHECK] Login page rendered: OK
```

## 4. 运行完整比赛测试

推荐用启动脚本：

```powershell
.\scripts\run_erp_competition_test.ps1
```

如果不想用 PowerShell，可以用 CMD 脚本。该脚本默认显示浏览器，并且会自动优先使用当前包里的 `.venv`：

```bat
scripts\run_erp_competition_test.bat
```

Git Bash / WSL 可以用：

```sh
sh scripts/run_erp_competition_test.sh
```

指定唯一编号和小组名：

```bat
scripts\run_erp_competition_test.bat --run-id T202605300001 --group-name AUTO202605300001 --strategy expanded --max-cycles 80
```

如果需要看到浏览器界面：

```powershell
.\scripts\run_erp_competition_test.ps1 -Headed
```

使用 `.bat` 或 `.sh` 时默认就是可见浏览器；需要后台运行再加 `--headless`：

```bat
scripts\run_erp_competition_test.bat --headless
```

也可以直接运行 Python：

```powershell
python scripts\erp_competition_runner.py --headless --max-cycles 80
```

## 4.1 当前策略覆盖范围

当前脚本默认使用 `expanded` 扩展经营策略，覆盖：

- 本地市场 P1 广告投放。
- 研发 P2/P3/P4。
- 开拓区域/国内/亚洲/国际市场。
- 研发 PRODUCT9000/PRODUCT1400 ISO 认证。
- 对页面上可见的 P1/P2/P3/P4 广告行各投 1 万。
- 选择可选订单并点击“结束选单”，包含确认弹窗。
- 采购 R1/R2/R3/R4，为 P2/P3/P4 准备原料。
- 尝试将待生产产线转产到已研发完成的 P2/P3/P4。
- 通过“配置”浮层点击开始生产、继续生产、开始安装、继续转产。
- 按订单交货。
- 资金不足时按贷款额度申请贷款。
- 推进周期直到结束或破产。

如果需要回到旧的基础流，可以运行：

```powershell
.\scripts\run_erp_competition_test.ps1 -Strategy basic-p1
```

`basic-p1` 只做本地 P1 广告、P1 选单、R1 采购、初始产线生产 P1 和交货，适合和旧脚本行为对比。

当前脚本仍暂不覆盖：

- 新建厂房、租赁厂房、购买新生产线。
- 主动选择某个具体市场/产品的最优订单组合。
- 保证每张 P2/P3/P4 订单都一定能生产并交付；这取决于研发完成时间、产线状态、原料到货周期和订单账期。

## 5. 查看结果

每次运行会生成独立目录：

```text
target\erp-python-full-competition\<run-id>\
```

重点文件：

- `target\erp-python-full-competition\<run-id>\report.json`：完整测试报告。
- `target\erp-python-full-competition\<run-id>\students-users.csv`：导入学生账号用 CSV。
- `target\erp-python-full-competition\<run-id>\students-for-game.csv`：创建比赛用 CSV。
- `target\erp-python-full-competition\<run-id>\*-final.png`：每个学生最终页面截图。

`report.json` 里重点看：

```json
"cleanup": {
  "remaining": []
}
```

`remaining` 为空表示本次创建的测试账号已经删除干净。

## 6. 清理逻辑说明

脚本结束时会按下面顺序清理：

1. 三个学生分别登录。
2. 调用 `/erpn/group/quit` 删除 `gamegroupmember` 成员关系。
3. 教师调用 `/erpn/teacher/deletestus` 删除 `approveduser` 学生账号。
4. 再查教师用户列表确认没有残留。

不要把清理顺序改成先 `/erpn/user/replay`。`replay` 只会把 `gamegroupmember.STATUS` 改成 `-1`，不会删除成员关系，后续教师删除账号会被外键挡住。

页面上的“重新开始游戏”可以让前端显示为退出游戏，但它本质上也是类似 `replay` 的业务退出动作。它适合人工重新开始一局，不适合作为测试账号删除前的第一步。测试脚本为了保证账号能删干净，会先 `/erpn/group/quit` 删除成员关系，再执行教师删除账号。

## 7. 常见问题

### Edge 启动失败

确认系统已安装 Microsoft Edge。也可以改用其他本机浏览器通道：

```powershell
.\scripts\run_erp_competition_test.ps1 -BrowserChannel chrome -CheckEnv
```

### 登录页打不开

先用浏览器手动打开：

```text
http://39.98.115.237/
```

如果打不开，需要检查网络、服务器或 `-BaseUrl` 参数。

### 导入学生失败

通常是同名账号已存在。先查数据库：

```sql
SELECT * FROM approveduser WHERE USERID LIKE '测%';
SELECT * FROM gamegroupmember WHERE USERID LIKE '测%';
```

确认残留后再定向删除，不要使用超级管理员的“删除所有数据”。

### 结束后仍有 remaining

看 `report.json` 的 `cleanup.diagnosis`。如果是旧脚本先执行过 `replay` 导致的残留，需要数据库定向清理 `gamegroupmember` 后再删 `approveduser`。
