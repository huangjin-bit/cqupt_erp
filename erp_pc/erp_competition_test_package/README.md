# ERP 竞赛浏览器自动化测试包

这个文件夹可以单独复制到其他 Windows 电脑运行，不依赖前端源码。

## 快速开始

在本文件夹根目录执行：

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements-erp-runner.txt

.\scripts\run_erp_competition_test.ps1 -CheckEnv
.\scripts\run_erp_competition_test.ps1
```

不想用 PowerShell 的话，也可以直接用普通命令行脚本：

```bat
scripts\run_erp_competition_test.bat
```

Git Bash / WSL 可以用：

```sh
sh scripts/run_erp_competition_test.sh
```

## 文件说明

- `scripts\erp_competition_runner.py`：主测试脚本。
- `scripts\run_erp_competition_test.ps1`：Windows 启动脚本。
- `scripts\run_erp_competition_test.bat`：Windows CMD 启动脚本，参数直接透传给 Python。
- `scripts\run_erp_competition_test.sh`：Git Bash / WSL 启动脚本，参数直接透传给 Python。
- `requirements-erp-runner.txt`：Python 依赖。
- `docs\erp_competition_runner_deployment.md`：完整部署和排错说明。
- `tests\test_erp_competition_runner.py`：单元测试。

## 常用命令

只检查环境，不创建比赛：

```bat
scripts\run_erp_competition_test.bat --check-env
```

运行完整测试：

```bat
scripts\run_erp_competition_test.bat --run-id T202605300001 --group-name AUTO202605300001
```

看到浏览器运行：

```bat
scripts\run_erp_competition_test.bat
```

后台运行：

```bat
scripts\run_erp_competition_test.bat --headless
```

回退旧的 P1 基础流：

```bat
scripts\run_erp_competition_test.bat --strategy basic-p1
```

运行单元测试：

```powershell
python -m pytest tests\test_erp_competition_runner.py -q
```

## 当前覆盖范围

当前默认是 `expanded` 扩展经营流：研发 P2/P3/P4，开拓区域/国内/亚洲/国际市场，研发 PRODUCT9000/PRODUCT1400，给可见的 P1/P2/P3/P4 广告行投放，采购 R1/R2/R3/R4，尝试把待生产产线转产到已研发产品，并通过页面“配置”浮层继续生产/转产。

`basic-p1` 仍保留，用于和旧脚本对比：本地 P1 广告、P1 选单、采购 R1、生产 P1、交货、贷款、推进周期。

测试账号删除时不要先点“重新开始游戏”。该动作只会让前端业务状态显示退出，不会删除 `gamegroupmember` 成员关系。脚本会先退出分组删除成员关系，再删除账号。
