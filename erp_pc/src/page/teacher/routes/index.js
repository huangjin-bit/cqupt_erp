import {
  MainIcon,
  MarketManagementIcon,
  ProductionManagementIcon,
  SellManagementIcon,
  RDIcon,
  InventoryManagementIcon,
  TimeIcon
} from 'assets/icons/index'
import React from 'react'

import Main from '../pages/main/index'

import UserList from '../pages/manageUsers/userList'
import UserApproval from '../pages/manageUsers/userApproval/index'

// 导入创建比赛模块
import BatchImport from '../pages/createGame/batchImport/index'
import FilterUser from '../pages/createGame/filterUser/index'
// 导入管理分组模块
import TeamProgress from '../pages/manageGroups/teamProgress/index'
import ContributionConfirmation from '../pages/manageGroups/contributionConfirmation/index'
import CompetitionReport from '../pages/manageGroups/competitionReport/index'
import SetGradeRatio from '../pages/manageGroups/setGradeRatio/index'
import HistoryCompetition from '../pages/manageGroups/historyCompetition/index'

import AdvertisingInvestmentAnalysis from '../pages/businessValuation/advertisingInvestmentAnalysis/index'
import ComprehensiveMarketShareAnalysis from '../pages/businessValuation/comprehensiveMarketShareAnalysis/index'
import ProductMarketShareAnalysis from '../pages/businessValuation/productMarketShareAnalysis/index'
import CostToSalesAnalysis from '../pages/businessValuation/costToSalesAnalysis/index'
import CostToSalesChange from '../pages/businessValuation/costToSalesChange/index'
import ProfitAnalysisOfProductContribution from '../pages/businessValuation/profitAnalysisOfProductContribution/index'
import ProductionCapacityAnalysis from '../pages/businessValuation/productionCapacityAnalysis/index'
import OwnerEquityAnalysis from '../pages/businessValuation/ownerEquityAnalysis/index'

import HistoryAdvertisingInvestmentAnalysis from '../pages/viewHistoricalReports/historyCostToSalesChange/index'
import HistoryComprehensiveMarketShareAnalysis from '../pages/viewHistoricalReports/historyOwnerEquityAnalysis/index'
import HistoryProductMarketShareAnalysis from '../pages/viewHistoricalReports/historyProductionCapacityAnalysis/index'
import HistoryCostToSalesAnalysis from '../pages//viewHistoricalReports/historyCostToSalesAnalysis/index'
import HistoryCostToSalesChange from '../pages/viewHistoricalReports/historyCostToSalesChange/index'
import HistoryProfitAnalysisOfProductContribution from '../pages/viewHistoricalReports/historyProfitAnalysisOfProductContribution/index'
import HistoryProductionCapacityAnalysis from '../pages/viewHistoricalReports/historyProductionCapacityAnalysis/index'
import HistoryOwnerEquityAnalysis from '../pages/viewHistoricalReports/historyOwnerEquityAnalysis/index'

import HistoryReports from '../pages/historyReports/index'

// route名字太长，根据需求可以缩短优化

export default [
  // {
  //   // path: '/teacher',
  //   path: '/teacher/main',
  //   name: '主页',
  //   icon: MainIcon,
  //   route: <Main></Main>,
  // },
  {
    name: '用户管理',
    icon: MarketManagementIcon,
    children: [
      {
        path: '/teacher/userList',
        name: '用户列表',
        route: <UserList></UserList>,
        Englishname: 'User List'
      },
      {
        path: '/teacher/userApproval',
        name: '审批用户',
        route: <UserApproval></UserApproval>,
        Englishname: 'User Approval'
      }
    ]
  },
  {
    name: '创建比赛',
    icon: ProductionManagementIcon,
    children: [
      {
        path: '/teacher/filterUserCreation',
        name: '筛选用户创建',
        Englishname: 'Filter User Creation',
        route: <FilterUser></FilterUser>
      },
      {
        path: '/teacher/batchImportCreation',
        name: '批量导入创建',
        Englishname: 'Batch import creation',
        route: <BatchImport></BatchImport>
      }
    ]
  },
  {
    name: '管理比赛',
    icon: RDIcon,
    children: [
      {
        path: '/teacher/teamProgress',
        name: '进度管理',
        Englishname: 'teamProgress',
        route: <TeamProgress></TeamProgress>,
      },
      {
        path: '/teacher/contributionConfirmation',
        name: '贡献度查看与确认',
        Englishname: 'contributionConfirmation',
        route: <ContributionConfirmation></ContributionConfirmation>,
      },
      {
        path: '/teacher/competitionReport',
        name: '比赛报告下载',
        Englishname: 'competitionReport',
        route: <CompetitionReport></CompetitionReport>,
      },
      {
        path: '/teacher/setGradeRatio',
        name: '分数设置',
        Englishname: 'setGradeRatio',
        route: <SetGradeRatio></SetGradeRatio>,
      },
      {
        path: '/teacher/historyCompetition',
        name: '历史比赛信息管理',
        Englishname: 'historyCompetition',
        route: <HistoryCompetition></HistoryCompetition>,
      }
    ]
  },
  {
    name: '企业评价',
    icon: SellManagementIcon,
    children: [
      {
        path: '/teacher/businessValuation/1',
        name: '广告投入产出分析',
        Englishname: 'Advertising Investment Analysis',
        route: <AdvertisingInvestmentAnalysis></AdvertisingInvestmentAnalysis>
      },
      {
        path: '/teacher/businessValuation/2',
        name: '综合市场占有率分析',
        Englishname: 'Comprehensive Market Share Analysis',
        route: <ComprehensiveMarketShareAnalysis></ComprehensiveMarketShareAnalysis>
      },
      {
        path: '/teacher/businessValuation/3',
        name: '产品市场占有率分析',
        Englishname: 'Product Market Share Analysis',
        route: <ProductMarketShareAnalysis></ProductMarketShareAnalysis>
      },
      {
        path: '/teacher/businessValuation/4',
        name: '成本费用占销售比例分析',
        Englishname: 'Analyze The Proportion Of Cost In Sales',
        route: <CostToSalesAnalysis></CostToSalesAnalysis>
      },
      {
        path: '/teacher/businessValuation/5',
        name: '成本费用占销售比例变化',
        Englishname: 'Cost changes in proportion to sales',
        route: <CostToSalesChange></CostToSalesChange>
      },
      {
        path: '/teacher/businessValuation/6',
        name: '产品贡献利润分析',
        Englishname: 'Profit Analysis Of Product Contribution',
        route: <ProfitAnalysisOfProductContribution></ProfitAnalysisOfProductContribution>
      },
      {
        path: '/teacher/businessValuation/7',
        name: '生产能力分析',
        Englishname: 'Production Capacity Analysis',
        route: <ProductionCapacityAnalysis></ProductionCapacityAnalysis>
      },
      {
        path: '/teacher/businessValuation/8',
        name: '所有者权益分析',
        Englishname: 'Owner Equity Analysis',
        route: <OwnerEquityAnalysis></OwnerEquityAnalysis>
      }
    ]
  },
  {
    name: '查看历史数据',
    icon: InventoryManagementIcon,
    children: [
      {
        path: '/teacher/historicalData/1',
        name: '广告投入产出分析',
        Englishname: 'Advertising Investment Analysis',
        route: (
          <HistoryAdvertisingInvestmentAnalysis></HistoryAdvertisingInvestmentAnalysis>
        )
      },
      {
        path: '/teacher/historicalData/2',
        name: '综合市场占有率分析',
        Englishname: 'Comprehensive Market Share Analysis',
        route: (
          <HistoryComprehensiveMarketShareAnalysis></HistoryComprehensiveMarketShareAnalysis>
        )
      },
      {
        path: '/teacher/historicalData/3',
        name: '产品市场占有率分析',
        Englishname: 'Product Market Share Analysis',
        route: (
          <HistoryProductMarketShareAnalysis></HistoryProductMarketShareAnalysis>
        )
      },
      {
        path: '/teacher/historicalData/4',
        name: '成本费用占销售比例分析',
        Englishname: 'Analyze The Proportion Of Cost In Sales',
        route: <HistoryCostToSalesAnalysis></HistoryCostToSalesAnalysis>
      },
      {
        path: '/teacher/historicalData/5',
        name: '成本费用占销售比例变化',
        Englishname: 'Filter User Creation',
        route: <HistoryCostToSalesChange></HistoryCostToSalesChange>
      },
      {
        path: '/teacher/historicalData/6',
        name: '产品贡献利润分析',
        Englishname: 'Profit Analysis Of Product Contribution',
        route: (
          <HistoryProfitAnalysisOfProductContribution></HistoryProfitAnalysisOfProductContribution>
        )
      },
      {
        path: '/teacher/historicalData/7',
        name: '生产能力分析',
        Englishname: 'Production Capacity Analysis',
        route: (
          <HistoryProductionCapacityAnalysis></HistoryProductionCapacityAnalysis>
        )
      },
      {
        path: '/teacher/historicalData/8',
        name: '所有者权益分析',
        Englishname: 'Owner Equity Analysis',
        route: <HistoryOwnerEquityAnalysis></HistoryOwnerEquityAnalysis>
      }
    ]
  },
  {
    path: '/teacher/historyReports',
    name: '历史报告',
    icon: TimeIcon,
    Englishname: 'History Reports',
    route: <HistoryReports></HistoryReports>
  },
  // // 404 Not Found页面待完善
  // {
  //   path: '/',
  //   name: 'NotFound',
  //   route: <div>404</div>,
  // },
]