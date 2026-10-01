// import React from 'react'
// import './index.scss'
// import { withRouter } from 'react-router'
// import {
//   BrowserRouter as Router,
//   Switch,
//   Route,
//   Link,
//   useRouteMatch,
//   useParams,
// } from 'react-router-dom'
// import { Menu } from 'antd'
// const { SubMenu } = Menu

// // import { AppstoreOutlined, MailOutlined, SettingOutlined } from '@ant-design/icons'

// class SideBar extends React.Component {
//   // constructor(props) {
//   //   super(props)
//   // }
//   rootSubmenuKeys = ['sub1', 'sub2', 'sub3']
//   state = {
//     openKeys: ['sub1'],
//   }
//   onOpenChange = openKeys => {
//     const latestOpenKey = openKeys.find(key => this.state.openKeys.indexOf(key) === -1)
//     if (this.rootSubmenuKeys.indexOf(latestOpenKey) === -1) {
//       this.setState({ openKeys })
//     } else {
//       this.setState({
//         openKeys: latestOpenKey ? [latestOpenKey] : [],
//       })
//     }
//   }
//   render() {
//     return (
//       <div className="sidebar-wrapper">
//         <Menu mode="inline" defaultSelectedKeys={['1']}>
//           <Menu.Item key="1">
//             <Link to="/useroperate/status">查看审核状态</Link>
//           </Menu.Item>
//           <Menu.Item key="2">
//             <Link to="/useroperate/modify">修改注册信息</Link>
//           </Menu.Item>
//           <Menu.Item key="4">
//             <Link to="/useroperate/joingroup">加入分组</Link>
//           </Menu.Item>
//           <Menu.Item key="5">
//             <Link to="/useroperate/creategame">创建比赛</Link>
//           </Menu.Item>
//           <Menu.Item key="6">退出</Menu.Item>
//         </Menu>
//       </div>
//     )
//   }
// }

// export default withRouter(SideBar)
