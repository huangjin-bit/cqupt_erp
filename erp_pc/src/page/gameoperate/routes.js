import {
  MainIcon,
  AnnualPlanningMeetingIcon,
  MarketManagementIcon,
  ProductionManagementIcon,
  SellManagementIcon,
  PurchaseManagementIcon,
  InventoryManagementIcon,
  RDIcon,
  FinancialManagementIcon,
  TaxesIcon,
  LoanIcon,
  TimeIcon,
  OverIcon,
} from 'assets/icons/index'

import Main from './pages/main/index'
import AnnualPlanningMeeting from './pages/annualPlanningMeeting/index'
import MarketManagement from './pages/marketManagement/index'
import Advertisement from './pages/advertisement/index'
import MarketForecast from './pages/marketForecast/index'
import WorkshopManagement from './pages/workshopManagement/index'
import CompositionOfProduct from './pages/compositionOfProduct/index'
import CheckOrder from './pages/checkOrder/index'
import DeliveryWithOrder from './pages/deliveryWithOrder/index'
import MaterialPurchase from './pages/materialPurchase/index'
import CheckPurchaseOrders from './pages/checkPurchaseOrders/index'
import WareManage from './pages/wareManage/index'
import CheckProfitSheet from './pages/checkProfitSheet/index'
import CheckBalanceSheet from './pages/checkBalanceSheet/index'
import CheckJournalEntry from './pages/checkJournalEntry/index'
import ApplyLoan from './pages/applyLoan/index'
import DiscountAccountsReceivable from './pages/discountAccountsReceivable/index'
import ProductResearch from './pages/productResearch/index'
import Authenticationmanagement from './pages/authenticationmanagement/index'
import LoanManagement from './pages/loanManagement/index'
import LongLoan from './pages/longLoan/index'
import Time from './pages/time/index'
import Taxes from './pages/taxes/index'
export default [
  {
    path: '/gameoperate/main',
    name: '主页',
    icon: MainIcon,
    route: Main,
  },
  {
    path: '/gameoperate/annualPlanningMeeting',
    name: '年度规划会议',
    icon: AnnualPlanningMeetingIcon,
    route: AnnualPlanningMeeting,
    Englishname: 'Annual Planning Meeting',
  },
  {
    name: '市场管理',
    icon: MarketManagementIcon,
    children: [
      {
        path: '/gameoperate/marketManagement',
        name: '各类市场管理',
        route: MarketManagement,
        Englishname: 'Market Management',
      },
      {
        path: '/gameoperate/advertisement',
        name: '广告投放 & 订单',
        route: Advertisement,
        Englishname: 'Advertisement&Order（注：若页面长时间没反应的时候请手动刷新一下页面）',
      },
      {
        path: '/gameoperate/marketForecast',
        name: '市场预测信息',
        Englishname: 'Market Forecast',
        route: MarketForecast,
      },
    ],
  },
  {
    name: '生产管理',
    icon: ProductionManagementIcon,
    children: [
      {
        path: '/gameoperate/workshopManagement',
        name: '厂房管理',
        Englishname: 'Workshop Management',
        route: WorkshopManagement,
      },
      {
        path: '/gameoperate/compositionOfProduct',
        name: '产品原料构成说明',
        Englishname: 'Composition Of Product',
        route: CompositionOfProduct,
      },
    ],
  },
  {
    name: '销售管理',
    icon: SellManagementIcon,
    children: [
      {
        path: '/gameoperate/checkOrder',
        name: '查看订单',
        Englishname: 'Check Order',
        route: CheckOrder,
      },
      {
        path: '/gameoperate/deliveryWithOrder',
        name: '按订单交货',
        Englishname: 'Delivery With Order',
        route: DeliveryWithOrder,
      },
    ],
  },
  {
    name: '采购管理',
    icon: PurchaseManagementIcon,
    children: [
      {
        path: '/gameoperate/materialPurchase',
        name: '购买原料',
        Englishname: 'Material Purchase',
        route: MaterialPurchase,
      },
      {
        path: '/gameoperate/checkPurchaseOrders',
        name: '查看原料采购单',
        Englishname: 'Check Purchase Orders',
        route: CheckPurchaseOrders,
      },
    ],
  },
  {
    Englishname: 'Inventory Manage',
    name: '库存管理',
    icon: InventoryManagementIcon,
    path: '/gameoperate/inventoryManage',
    route: WareManage
  },
  {
    name: '产品研发&认证',
    icon: RDIcon,
    children: [
      {
        path: '/gameoperate/productResearch',
        name: '产品研发管理',
        Englishname: 'Product Research',
        route: ProductResearch,
      },
      {
        path: '/gameoperate/authenticationmanagement',
        name: '认证管理',
        Englishname: 'Authentication management',
        route: Authenticationmanagement,
      },
    ],
  },
  {
    name: '财务管理',
    icon: FinancialManagementIcon,
    children: [
      {
        path: '/gameoperate/checkJournalEntry',
        name: '查看会计分录',
        Englishname: 'Check Journal Entry',
        route: CheckJournalEntry,
      },
      {
        path: '/gameoperate/checkBalanceSheet',
        name: '查看资产负债表',
        Englishname: 'Check Balance Sheet',
        route: CheckBalanceSheet,
      },
      {
        path: '/gameoperate/checkProfitSheet',
        name: '查看利润表',
        Englishname: 'Check Profit Sheet',
        route: CheckProfitSheet,
      },
      {
        path: '/gameoperate/discountAccountsReceivable',
        name: '应收账款贴现',
        Englishname: 'Discount Accounts Receivable',
        route: DiscountAccountsReceivable,
      },
    ],
  },
  {
    name: '贷款与融资',
    icon: LoanIcon,
    children: [
      {
        path: '/gameoperate/applyLoan',
        name: '申请贷款',
        Englishname: 'ApplyLoan',
        route: ApplyLoan,
      },
      {
        path: '/gameoperate/loanManagement',
        name: '贷款管理',
        Englishname: 'Loan Management',
        route: LoanManagement,
      },
      {
        path: '/gameoperate/longLoan',
        name: '一年内的长期负债',
        Englishname: 'Long Loan',
        route: LongLoan,
      }
    ],
  },

  {
    Englishname: 'Tax Manage',
    path: '/gameoperate/taxes',
    name: '税金管理',
    icon: TaxesIcon,
    route: Taxes,
  },

  {
    Englishname: 'Time Manage',
    path: '/gameoperate/time',
    name: '时间管理',
    icon: TimeIcon,
    route: Time,
  },
]
