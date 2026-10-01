import React from 'react'
import { Route, Redirect, Link } from 'react-router-dom'

import { PoweroffOutlined } from '@ant-design/icons'
import { Menu } from 'antd'
import Login from '../login'
import MessageCard from 'components/messageCard/index'
import ModifyForm from './component/modifyForm/index'
import JoinGroup from './component/joinGroup'
import Logout from 'components/logout'
import StartGame from './component/startGame'
import CreateGame from './component/createGame'
class RenderRoutes {
  routes = [
    {
      state: [1, 5],
      key: 1,
      path: '/userprepare/status',
      route: (
        <MessageCard>
          你好！你现在的账号处于&nbsp;&nbsp;<b>待审批</b>&nbsp;&nbsp;状态！
          <br />
          管理员通知后可以尝试刷新页面
        </MessageCard>
      ),
      sideBar: '查看审核状态',
    },
    {
      state: [1, 3, 5],
      key: 2,
      path: '/userprepare/modify',
      route: <ModifyForm></ModifyForm>,
      sideBar: '修改注册信息',
    },
    {
      state: [3, 5],
      key: 3,
      path: '/userprepare/joingroup',
      route: <JoinGroup></JoinGroup>,
      sideBar: '加入分组',
    },
    {
      state: [3, 5],
      key: 4,
      path: '/userprepare/creategame',
      route: <CreateGame></CreateGame>,
      sideBar: '创建比赛',
    },
    {
      state: [7, 8, 5],
      key: 5,
      path: '/userprepare/startgame',
      route: <StartGame></StartGame>,
      sideBar: '开始比赛',
    },
    {
      state: [-1],
      key: 6,
      path: '/login',
      route: <Login></Login>,
      sideBar: '用户登录'
    }
  ]
  renderSideBar = userStatus => {
    const sideBar = this.routes
      .filter(item => item.state.includes(userStatus))
      .map(item => (
        <Menu.Item key={item.key}>
          <Link to={item.path}>{item.sideBar}</Link>
        </Menu.Item>
      ))
    sideBar.push(
      <Logout key="999">
        退出登录 &ensp; <PoweroffOutlined />
      </Logout>
    )
    return (
      <Menu mode="inline" defaultSelectedKeys={[sideBar[0].key]}>
        {sideBar}
      </Menu>
    )
  }
  renderContent = userStatus => {
    const content = this.routes
      .filter(item => item.state.includes(userStatus))
      .map(item => (
        <Route key={item.key} path={item.path}>
          {item.route}
        </Route>
      ))
    content.push(
      <Route path="/userprepare" key="100">
        <Redirect to={content[0].props.path} />
      </Route>
    )
    return content
  }
}
export default new RenderRoutes()
