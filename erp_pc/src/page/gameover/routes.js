import {
  AnnualPlanningMeetingIcon,
  FinancialManagementIcon,
  SellManagementIcon,
  RDIcon
} from 'assets/icons/index'



import CheckProfitSheet from './pages/checkProfitSheet/index'
import CheckBalanceSheet from './pages/checkBalanceSheet/index'
import CheckJournalEntry from './pages/checkJournalEntry/index'
import CheckCash from './pages/checkCash/index'
import DownloadExperimentReport from './pages/downloadExperimentReport/index'
import FillExperienceReport from './pages/fillexperiencereport/index'
import MemberSet from './pages/memberSet/index'
import PreviewExperimentReport from './pages/previewExperimentReport/index'

export default [
  {
    name: '财务管理',
    icon: FinancialManagementIcon,
    children: [
      {
        path: '/gameover/checkJournalEntry',
        name: '查看会计分录',
        Englishname: 'Check Journal Entry',
        route: CheckJournalEntry,
      },
      {
        path: '/gameover/checkBalanceSheet',
        name: '查看资产负债表',
        Englishname: 'Check Balance Sheet',
        route: CheckBalanceSheet,
      },
      {
        path: '/gameover/checkProfitSheet',
        name: '查看利润表',
        Englishname: 'Check Profit Sheet',
        route: CheckProfitSheet,
      },
      {
        path: '/gameover/checkCash',
        name: '盘点现金',
        Englishname: 'Check Cash',
        route: CheckCash,
      },
    ],
  },
  {
    name: '实验报告',
    icon: SellManagementIcon,
    children: [
      {
        path: '/gameover/previewExperimentReport',
        name: '预览实验报告',
        Englishname: 'Preview Experiment Report',
        route: PreviewExperimentReport,
      },
      {
        path: '/gameover/downloadExperimentReport',
        name: '下载实验报告',
        Englishname: 'Download Experiment Report',
        route: DownloadExperimentReport,
      }
    ]
  },
  {
    path: '/gameover/fillexperiencereport',
    name: '填写实验心得',
    icon: AnnualPlanningMeetingIcon,
    route: FillExperienceReport,
    Englishname: 'Fill Experience Report',
  },
  {
    path: '/gameover/memberSet',
    name: '小组成员管理',
    icon: RDIcon,
    route: MemberSet,
    Englishname: 'Member Set',
  }
]
