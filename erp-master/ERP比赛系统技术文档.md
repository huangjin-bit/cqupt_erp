# ERP 沙盘模拟比赛系统 — 技术文档

## 目录

1. [项目概述](#1-项目概述)
2. [技术架构](#2-技术架构)
3. [业务模块详解](#3-业务模块详解)
4. [核心业务流程](#4-核心业务流程)
5. [数据库设计](#5-数据库设计)
6. [API 接口概览](#6-api-接口概览)
7. [WebSocket 推送系统](#7-websocket-推送系统)
8. [部署说明](#8-部署说明)
9. [常见问题与排查](#9-常见问题与排查)

---

## 1. 项目概述

### 1.1 系统定位

ERP 沙盘模拟比赛系统，用于高校经管类实验教学。学生分组经营虚拟企业，在多个比赛周期内完成市场开拓、产品研发、生产管理、广告投放、选单、融资、财务核算等完整的企业经营流程。

### 1.2 核心数据

| 项目 | 默认值 |
|------|--------|
| 比赛年数 | 6 年 |
| 每年周期数 | 4 期 |
| 每组人数 | 最多 6 人 |
| 总周期数 | 24 期 |
| 初始现金 | 由 `inigame` 存储过程初始化 |

### 1.3 技术栈

| 层面 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.3.1 |
| ORM | MyBatis | 2.1.3 |
| 数据库 | MySQL | 8.0+ |
| 缓存/会话 | Redis + Spring Session | - |
| API 文档 | Swagger 2.9.2 + Swagger Bootstrap UI | - |
| 前端框架 | React | 16.13 |
| UI 组件 | Ant Design | 4.17 |
| 状态管理 | Redux | - |
| 实时通信 | WebSocket (Spring) | - |
| 图表 | ECharts, @antv/g2 | - |
| 构建 | Maven (后端), CRA/Yarn (前端) | - |

---

## 2. 技术架构

### 2.1 后端分层

```
Controller (接收 HTTP 请求)
    ↓
Service (业务逻辑层)
    ↓
DAO (MyBatis Mapper/数据访问层)
    ↓
MySQL (数据库)
```

- **Controller**: REST 接口，接收前端请求，调用 Service，返回 `Result` 统一响应体
- **Service**: 业务逻辑，标注 `@Transactional` 管理事务
- **DAO**: MyBatis Mapper 接口，XML 中写 SQL
- 认证方式：基于 Session（`HttpSession`），`LoginStatusCheckFilter` 做登录校验

### 2.2 项目包结构

```
com.cqupt.mislab.erpn/
├── ErpnApplication.java              # 启动类，@MapperScan
├── commons/
│   ├── config/filter/                # 登录校验过滤器
│   ├── constant/                     # 常量与枚举
│   ├── exception/                    # 自定义异常
│   ├── utils/                        # 工具类（报表、财务计算、策略工厂）
│   └── websocket/                    # WebSocket 端点
├── controller/                       # REST 控制器（18 个业务模块）
├── dao/                              # MyBatis Mapper 接口
├── model/
│   ├── entity/                       # 数据实体
│   ├── dto/                          # 数据传输对象
│   ├── vo/                           # 视图对象
│   └── statusChange/                 # 状态枚举
└── service/                          # Service 接口与实现
```

### 2.3 统一响应格式

```java
public class Result<T> {
    private int code;       // 状态码（200 成功，其余为错误码）
    private String message; // 提示信息
    private T data;         // 数据载荷
}
```

### 2.4 业务状态码约定

| 状态 | 含义 |
|------|------|
| `user.status = 0` | 已破产 |
| `user.status = 1` | 正常经营 |
| `user.status = 2` | 已完成比赛 |
| `user.status = 10` | 已加入小组但比赛未开始 |
| `finishAdvertiseFlag = 0` | 未完成广告投放 |
| `finishAdvertiseFlag = 1` | 已完成广告投放 |
| `chooseOrderFlag = 0` | 选单未开始 |
| `chooseOrderFlag = 1` | 选单进行中 |
| `finishOrderFlag = 0` | 未完成选单 |
| `finishOrderFlag = 1` | 已完成选单 |

---

## 3. 业务模块详解

### 3.1 用户管理

**数据模型**：三层继承关系

```
BasicUser (基类)
  userId / userName / password / major / sClass / sId / email / tel / gender
    ↓
RegisterUser (注册用户)
  + status (审批状态)
    ↓
ApprovedUser (已审批用户)
  + rem2
```

**业务流程**：

1. 学生注册 → `registeruser` 表
2. 教师审批 → 移至 `approveduser` 表
3. 学生登录 → 进入小组准备页面

**关键 API**：

| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/regester/addregester` | POST | 学生注册 |
| `/user/login` | POST | 登录（userID + password） |
| `/user/logout` | GET | 登出 |
| `/user/info` | GET | 获取当前用户游戏信息 |
| `/user/getBasic` | GET | 获取用户基本个人信息 |

### 3.2 比赛小组管理

**数据模型**：

- `GameGroupInfo`：比赛组（组名、创建者、期数、年数、成绩比例等）
- `GameGroupMemberInfo`：组员（userUnique、currentPeriod、status）
- `GameGroupMemberDetail`：组员详情（角色、贡献度、成绩）

**userUnique**：用户在某个比赛组中的唯一标识，格式为 `{小组名}_{userId}`。

**业务流程**：

1. 教师/学生创建小组
2. 学生加入小组
3. 创建者点击"开始比赛" → 调用 `inigame` 存储过程初始化数据
4. 生成市场预测、初始化订单池、ISO 资质

**关键 API**：

| 接口 | 方法 | 说明 |
|------|------|------|
| `/group/create` | POST | 创建小组 |
| `/group/join` | POST | 加入小组 |
| `/group/quit` | POST | 退出小组 |
| `/group/kick` | POST | 踢出成员 |
| `/group/startGame` | POST | 开始比赛（synchronized） |
| `/group/stopOperation` | POST | 结束经营 |
| `/group/getGroupList` | GET | 获取小组列表 |
| `/group/getGroupMemberList` | GET | 获取组成员列表 |

**比赛初始化流程** (`IniGameServiceImpl.java`):

1. 调用 MySQL 存储函数 `inigame(groupName)` — 初始化基础数据（现金、厂房、生产线等）
2. 生成市场预测 — 对每个市场/产品/年份生成价格和需求量（含随机波动）
3. 初始化订单池 — 每个市场/产品/年份生成 `1.5 * 人数` 个订单
4. 为订单分配 ISO 需求 — 越往后 ISO 需求概率越高

### 3.3 市场与产品

**市场实体**：

- `MarketBasic`：市场基础信息（名称、研发周期、研发費、维护费）
- `DevelopingMarket`：正在开拓的市场
- `DevelopedMarket`：已开拓的市场

**产品实体**：

- `ProductBasic`：产品基础信息
- `ProductDetailBasic`：产品物料清单（BOM）
- `DevelopingProduct`：正在研发的产品
- `DevelopedProduct`：已研发完成的产品

**关键 API**：

| 模块 | 接口 | 说明 |
|------|------|------|
| 市场 | `/market/getAllMarket` | 获取所有市场状态 |
| 市场 | `/market/startUndevelopMarket` | 开始开拓市场 |
| 市场 | `/market/pauseDevelopingMarket` | 暂停开拓 |
| 产品 | `/product/undevelops` | 未研发产品列表 |
| 产品 | `/product/start` | 开始研发产品 |
| 产品 | `/product/products` | 产品库存查询 |

### 3.4 厂房与生产线

**厂房类型**：

| 类型 | 容量 | 方式 |
|------|------|------|
| 大厂房 | 4 条生产线 | 购买/租赁 |
| 小厂房 | 2 条生产线 | 购买/租赁 |

**生产线类型**：

| 类型 | 安装周期 | 生产周期 | 转产周期 | 转产费 |
|------|----------|----------|----------|--------|
| 手工 | 0 | 3 | 0 | 0 |
| 半自动 | 2 | 2 | 1 | 有 |
| 全自动 | 4 | 1 | 2 | 有 |

**生产线状态**：安装中(0) → 暂停安装(1) → 生产中(2) → 暂停生产(3) → 空闲(4) → 转产中(5) → 暂停转产(6)

### 3.5 物料与采购

- `MaterialBasic`：物料基础（名称、价格、到货延迟期数）
- `MaterialOfUser`：用户物料库存
- `BuyingMaterial`：在途采购订单

采购是周期推进时自动触发的业务：每推进一个周期，检查在途物资是否到货。

### 3.6 ISO 认证

- `ISOBasic`：认证基础信息
- `ISOUndevelop` / `ISODeveloping` / `ISODeveloped`：三类状态跟踪

ISO 认证影响选单 —— 如果订单要求 ISO9000 或 ISO14000，企业必须已获得相应认证才能选择该订单。

### 3.7 广告投放与选单生成（核心模块）

**数据模型**：

- `adofuser`：广告投放记录（userUnique, period, marketName, productName, money）
- `adstatusofuser`：广告状态（finishAdvertiseFlag, finishOrderFlag, chooseOrderFlag）

**业务流程**：

```
每年初 → 初始化广告条目（每个市场/产品一行，money=0）
       → 学生输入广告费
       → 确认广告投放（校验现金是否足够）
       → 等待同组其他成员完成
       → 全部完成后 → 自动生成选单顺序
       → 进入选单环节
```

**选单顺序算法**（`generateChooseOrders`）：

```
每用户得分 = (广告排名分 + 历史交货排名分) / 2

广告排名分: 按广告费高低排序，最高得 2N 分，依次递减 2 分
历史交货分: (已交货金额 - 未交货金额) 排序，最高得 N 分，依次递减 1 分

得分越低 → 选单优先级越高（先选）
```

关键约束：
- 每个市场/产品独立排序
- 广告费为 0 的用户也有保底排名分（不跳过）
- 交货表现差（违约多）的用户会被降低优先级

### 3.8 选单（订单选择）

**数据模型**：

- `AllOrdersOfGroup`：小组可用订单池（productName, pNumber, price, marketName, needTime, penalPercent, specialRem, status, period）
- `ChooseOrder`：选单记录（chooseValue = 选单顺序号）
- `OrdersOfUser`：用户已选订单

**业务流程**：

```
全部广告完成后 → 为每个市场/产品生成选单队列
               → 按得分排序，得分最低的用户先选
               → 用户选一个订单 → 轮到下一个用户
               → 直到该市场/产品的订单选完或用户结束选单
```

**选单校验**（按序检查）：

1. 订单是否已被选（`ReentrantLock` 并发控制）
2. 该市场是否已开拓
3. 该订单是否要求 ISO → 是否已获得该认证
4. 是否为该用户的选择轮次

**返回码**：

| 码 | 含义 |
|----|------|
| 1 | 选单成功 |
| 2 | 不是你的轮次 |
| 3 | ISO 未开发 |
| 4 | 市场未开拓 |
| 5 | 订单已被选 |

**结束选单方式**：
- 按产品结束：`/chooseorder/finishOrderByMarketNameAndProductName`
- 全部结束：`/chooseorder/finishAll`
- 按顺序号结束：`/chooseorder/endSelectOrder`

### 3.9 贷款

| 贷款类型 | 利率方式 | 贷款时机 | 额度限制 |
|----------|----------|----------|----------|
| 短期贷款 | 期末付息 | 随时 | 无限制 |
| 长期贷款 | 年末付息 | 年初 | 权益倍数 |

### 3.10 财务系统

| 模块 | 说明 |
|------|------|
| 总账 | `AccountHead` + `AccountDetail`，记录每笔业务的分录 |
| 资产负债表 | `BalanceSheet`，资产 = 负债 + 权益 |
| 利润表 | `ProfitSheet`，收入 - 成本 = 利润 |
| 现金流量表 | 通过 `CashDao` 跟踪 |
| 应收账款 | `WillReceive`，可贴现（贴现率由 `WillReceiveToCashBasic` 配置） |
| 税务 | `TaxSheet` |

### 3.11 周期推进（核心游戏循环）

**接口**：`GET /forward/cycle`

**推进流程**（`ForwardQuaterServiceImpl.forwardStatus`）：

```
1. 检查用户是否破产（status=0）→ 返回 status 0
2. 如果是年初且未完成广告+选单 → 返回 status 2
3. 调用 MySQL 存储函数 biz_next_deal(userUnique)
   - 处理：原材料到货、产品完工、折旧、贷款利息等
4. 检查是否破产（资产负债比 1.5 倍、现金为负）
   → 破产：设置 status=0，插入占位记录，广播通知
5. 检查是否完成全部 24 期 → 设置 status=2（比赛结束）
6. 推进周期：CURRENTPERIOD = 当前值 + 1
7. 更新小组的 currentPeriod
8. 如果推进后是年初 → 初始化广告条目和状态
9. 返回 status 1（推进成功）
```

**前端状态码**：

| 码 | 含义 |
|----|------|
| 0 | 已破产 |
| 1 | 正常经营，可推进到下一周期 |
| 2 | 年初但未完成广告和选单 |
| 3 | 比赛已结束 |

**破产判定公式**：
```
破产条件：1.5 × 总资产 < 总负债 或 现金 < 0
```

### 3.12 教师管理功能

**主要模块**：

| 功能 | 说明 |
|------|------|
| 学生管理 | 批量导入、审批注册、重置密码、删除 |
| 比赛管理 | 创建比赛、查看进度、强制推进、强制结束 |
| 成绩管理 | 设置分数比例、确认成员贡献度 |
| 分析报表 | 市场份额、广告 ROI、成本结构、产能分析 |
| 报表下载 | 实验报告 .doc 下载 |

### 3.13 超级管理员

| 功能 | 说明 |
|------|------|
| 管理员管理 | 增删改查所有教师账号 |
| 数据清理 | 一键清空所有比赛数据 |

---

## 4. 核心业务流程

### 4.1 完整比赛生命周期

```
0. 教师导入学生 → 学生注册 → 教师审批
1. 学生创建/加入小组
2. 开始比赛

   ==== 第 1 年 ====
   第 1 期：
     - 年初 → 初始化广告
     - 所有成员投放广告
     - 生成选单顺序 → 选单
     - 经营：生产、采购、交货、融资
   第 2 期：
     - 继续经营
   第 3 期：
     - 继续经营
   第 4 期（年末）：
     - 继续经营
     - 推进 → 第 2 年第 1 期（周期 5）

   ==== 重复 6 年 ====

3. 比赛结束 → 生成实验报告
4. 成员贡献度确认 → 教师评分
```

### 4.2 广告+选单流程图

```
每年初（周期 1 或 5 或 9 或 13 或 17 或 21）
    │
    ├─ → initAdOfUser() 创建广告条目
    │     每个(市场,产品)一行，money=0
    │
    ├─ → initAdStatusOfUser() 创建广告状态行
    │     finishAdvertiseFlag=0
    │     finishOrderFlag=0
    │     chooseOrderFlag=0
    │
    ├─ → 用户设置广告费（多次调用 updateMoneyByAdId）
    │
    ├─ → 用户确认广告（updateAdvertisementFinish）
    │     ├─ 校验 cash >= 总广告费
    │     ├─ 生成会计分录
    │     ├─ 扣除现金
    │     ├─ finishAdvertiseFlag = 1
    │     └─ 检查同组是否全部完成
    │           ├─ 否 → 广播"某某已完成广告"
    │           └─ 是 → 生成选单顺序（isCheckOtherMember）
    │                   ├─ 计算每个(市场,产品)的顺序
    │                   ├─ chooseOrderFlag = 1（全部成员）
    │                   └─ 广播"进入选单环节"
    │
    └─ → 选单环节
          ├─ 按顺序选单
          ├─ 可选：结束某个产品的选单
          └─ 可选：全部结束选单（finishAll）
```

### 4.3 选单顺序生成算法

```
输入：当前组所有活跃成员、市场名、产品名、周期

步骤1：计算历史交货分
  A = 该市场/产品该组已交货总金额
  B = 该市场/产品该组未交货总金额  
  moneyScore = A - B
  按 A-B 降序排列，得分 = rank * 1（最低得 1 分）

步骤2：计算广告分
  按广告费降序排列，得分 = rank * 2（最低得 2 分）
  未投放广告的用户：fallbackScore = 人数 * 2 + 2 + 序号

步骤3：计算综合分
  totalScore = (adScore + moneyScore) / 2

步骤4：按综合分升序排列
  综合分最低的用户最先选单

步骤5：写入选单顺序表（chooseorder）

注意：
- 综合分中的广告分权重是交货分的 2 倍
- 第 1 期（无历史数据）使用特殊权重 0
```

---

## 5. 数据库设计

### 5.1 核心表结构

**用户相关**：

| 表名 | 主键 | 说明 |
|------|------|------|
| `registeruser` | USERID | 注册用户（待审批） |
| `approveduser` | USERID | 已审批用户 |
| `adminuser` | ADMINID | 管理员/教师 |

**比赛组**：

| 表名 | 主键 | 说明 |
|------|------|------|
| `gamegroup` | GROUPNAME | 比赛组信息 |
| `gamegroupmember` | (GROUPNAME, USERID) | 组员关系 |
| `gamegroupmemberdetail` | USERUNIQUE | 组员详情与成绩 |

**业务数据**：

| 表名 | 主键/唯一键 | 说明 |
|------|-------------|------|
| `marketbasic` | MARKETNAME | 市场基础 |
| `productbasic` | PRODUCTNAME | 产品基础 |
| `isobasic` | ISONAME | ISO 基础 |
| `adofuser` | ADID / UK(USERUNIQUE,PERIOD,MARKETNAME,PRODUCTNAME) | 广告投放 |
| `adstatusofuser` | UK(USERUNIQUE,PERIOD) | 广告与选单状态 |
| `chooseorder` | CHOOSEID / UK(USERUNIQUE,PERIOD,MARKETNAME,PRODUCTNAME,GROUPNAME) | 选单顺序 |
| `allordersofgroup` | ORDERID | 订单池 |
| `ordersofuser` | (USERUNIQUE, ORDERID) | 用户已选订单 |
| `cashsheet` | USERUNIQUE | 现金 |
| `accounthead` | ACCOUNTID | 总账头 |
| `accountdetail` | (USERUNIQUE, ACCOUNTID) | 总账明细 |
| `balancesheet` | (USERUNIQUE, PERIOD, ONEITEM) | 资产负债表 |
| `profitandloss` | (USERUNIQUE, PERIOD, ONEITEM) | 利润表 |

### 5.2 关键唯一键约束

```sql
-- adstatusofuser: 每人每期一条状态
UNIQUE KEY (USERUNIQUE, PERIOD)

-- adofuser: 每人每期每个市场每个产品一条广告
UNIQUE KEY (USERUNIQUE, PERIOD, MARKETNAME, PRODUCTNAME)

-- chooseorder: 每人每期每个市场产品一条选单顺序
UNIQUE KEY (USERUNIQUE, PERIOD, MARKETNAME, PRODUCTNAME, GROUPNAME)
```

---

## 6. API 接口概览

### 6.1 基础路径

所有 API 以 `/erpn` 为 context-path，运行在 81 端口。

### 6.2 控制器与接口数量

| 控制器路径 | 模块 | 接口数 |
|-----------|------|--------|
| `/user` | 用户管理 | 7 |
| `/group` | 比赛小组 | 12 |
| `/ad` | 广告系统 | 8 |
| `/chooseorder` | 选单 | 9 |
| `/orderofusers` | 订单管理 | 3 |
| `/market` | 市场 | 8 |
| `/product` | 产品 | 7 |
| `/productline` | 生产线 | 12 |
| `/factory` | 厂房 | 10 |
| `/material` | 物料 | 4 |
| `/iso` | ISO 认证 | 8 |
| `/loan` | 贷款 | 5 |
| `/forward` | 周期推进 | 1 |
| `/account` | 总账 | 1 |
| `/balance` | 资产负债表 | 1 |
| `/profit` | 利润表 | 1 |
| `/willreceive` | 应收账款 | 2 |
| `/tax` | 税务 | 1 |
| `/teacher` | 教师功能 | 25 |
| `/teacherGroup` | 教师小组管理 | 8 |
| `/teacherEvaluate` | 企业分析 | 8 |
| `/superadmin` | 超级管理员 | 10 |
| `/report` | 实验报告 | 2 |
| `/experience` | 实验心得 | 2 |
| `/members` | 成员详情 | 2 |

### 6.3 Swagger 文档

启动后端服务后访问：`http://localhost:81/erpn/swagger-ui.html`

---

## 7. WebSocket 推送系统

### 7.1 连接方式

前端通过 `/erpn/websocket/{groupName}/{userUnique}` 建立 WebSocket 连接。

### 7.2 消息类型

| type | 含义 | 触发时机 |
|------|------|----------|
| 6 | 某成员广告投放完成 | 调用 `updateAdvertisementFinish` 后 |
| 7 | 全部成员广告完成，进入选单 | 全员完成广告后 |
| 8 | 选单状态变更 | 选单/结束选单后 |
| 9 | 破产通知 | 推进周期时检测到破产 |

### 7.3 推送服务类

`AdAndOrderRefreshService` — 封装了所有推送逻辑：
- `advertiseComplete` — 推送 type 6
- `allMembersComplete` — 推送 type 7
- `changeOrder` — 推送 type 8
- `finishOrder` — 推送 type 8
- `publishNextOrder` — 推送下一选单人

---

## 8. 部署说明

### 8.1 环境要求

| 组件 | 要求 |
|------|------|
| JDK | 8（Corretto-8.482 或 Oracle JDK 8） |
| MySQL | 8.0+ |
| Redis | 任意版本 |
| Maven | 3.6+ |
| Node.js | 12+（前端构建） |
| Nginx | 用于反向代理（可选） |

### 8.2 后端构建与部署

```bash
# JDK 8 编译（必须 JDK 8，21 不兼容 Lombok）
export JAVA_HOME="C:/tools/java-tools/jdk8/jdk8"
export PATH="$JAVA_HOME/bin:$PATH"

# 打包
mvn clean package -DskipTests=true

# jar 位置
# target/erpn-0.0.1-SNAPSHOT.jar

# 启动
java -jar erpn-0.0.1-SNAPSHOT.jar
```

### 8.3 前端构建

```bash
cd erp_pc

# 安装依赖
yarn install

# 构建
yarn build

# 构建产物在 build/ 目录
```

### 8.4 Nginx 配置参考

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    root /path/to/erp_pc/build;
    index index.html;

    # API 反向代理
    location /erpn/ {
        proxy_pass http://127.0.0.1:81;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # WebSocket 代理
    location /erpn/websocket/ {
        proxy_pass http://127.0.0.1:81;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

## 9. 常见问题与排查

### 9.1 JDK 版本问题

**现象**：编译报 `IllegalAccessError` 或 Lombok 相关异常。
**原因**：JDK 21+ 与 Lombok 不兼容。
**解决**：使用 JDK 8 编译。

### 9.2 周期推进后周期被重置

**现象**：第 1 年第 4 期推进后变成第 1 年第 1 期。
**原因**：MySQL 存储函数 `biz_next_deal` 在年末执行时可能重置 `CURRENTPERIOD`。
**修复**：已在 `ForwardQuaterServiceImpl` 中增加绝对赋值保护，在调用 `biz_next_deal` 前保存当前周期，随后直接设置为 `periodBefore + 1`，不受存储函数干扰。

### 9.3 广告确认返回"已进行过广告投放"

**现象**：同组部分学生确认广告时提示已投放。
**原因**：`biz_next_deal` 预创建 `adstatusofuser` 行且 `FINISHADFLAG=1`。
**修复**：`AdvertisementStatusOfUserMapper.xml` 中已将 INSERT 改为 `ON DUPLICATE KEY UPDATE`，覆盖存储函数预置的值。

### 9.4 选单时人机验证不通过

**现象1**：后端记录 `2023 not equals 2010` 的警告
**原因**：前端携带的 cookie（`JSESSIONID`）发生变化或丢失，导致分布式会话（Redis）中的 session 与当前请求不匹配
**排查**：检查 Redis 中的 session 是否过期，或前端请求是否携带了正确的 cookie

**现象2**：请求参数传递中文出现乱码
**原因**：前端未对 URL 中的中文参数进行编码
**修复**：前端 API 封装层对 query 参数使用 `encodeURIComponent` 编码

### 9.5 密码安全性

当前系统密码以明文存储在数据库中。建议在生产环境中增加 MD5/BCrypt 加密。

---

## 附录：开发环境调试

### 启动后端

```bash
export JAVA_HOME="C:/tools/java-tools/jdk8/jdk8"
export PATH="$JAVA_HOME/bin:$PATH"
mvn spring-boot:run
```

### 运行测试

```bash
# 单测
mvn test -Dtest=AdvertisementServiceOrderGenerationTest

# E2E 测试
mvn test -Dtest=ErpThreeStudentBusinessChainH2Test

# 全部测试
mvn test
```

### IDEA 配置

- JDK: 选择 JDK 8
- Lombok: 安装 Lombok 插件并启用 Annotation Processing
- 启动参数: `--spring.profiles.active=dev`（可选）
