
import {
    AnnualPlanningMeetingIcon,
    MarketManagementIcon,
} from 'assets/icons/index'

import ManageUser from './manageUser/index'
import ReportList from './reportList/index'


export default [
    {
        path: '/superadmin/manageUser',
        name: '管理员用户列表',
        route: ManageUser,
        icon: MarketManagementIcon
    },
    {
        path: '/superadmin/reportList',
        name: '报告文件列表',
        route: ReportList,
        icon: AnnualPlanningMeetingIcon
    },
]