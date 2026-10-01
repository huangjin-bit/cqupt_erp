import React from 'react'
import { Switch } from 'react-router-dom'
import { connect } from 'react-redux'
import { actionCreators } from './store/index.js'

import RenderRoutes from './routes'
import Header from 'components/header/index'
import { Layout } from 'antd'
import './index.scss'

const { Header: AntdHeader, Sider, Content } = Layout

function UserPrepare(props) {
  return (
    <div className="userprepare-wrapper">
      <Layout>
        <AntdHeader>
          <Header></Header>
        </AntdHeader>
        <Layout>
          <Sider width="155">
            <div className="sidebar-wrapper">
              {RenderRoutes.renderSideBar(props.common.userStatus)}
            </div>
          </Sider>
          <Content>
            <Switch>{RenderRoutes.renderContent(props.common.userStatus)}</Switch>
          </Content>
        </Layout>
      </Layout>
    </div>
  )
}

const mapStateToProps = state => ({
  common: state.common,
  userprepare: state.userprepare,
})

const mapDispatchToProps = {
  changeGroupMessage: actionCreators.changeGroupMessage,
  getGroupMemberList: actionCreators.getGroupMemberList,
}

export default connect(mapStateToProps, mapDispatchToProps)(UserPrepare)
