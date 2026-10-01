import React from 'react'
import { Switch, Route, Link, useHistory } from 'react-router-dom'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import routes from './routes'
import './index.scss'
import 'assets/icons/iconfont.css'
import { PageHeader, Layout, Menu, Modal, message } from 'antd'
import { OverIcon } from 'assets/icons/index'
import httpUtil from 'utils/request'
import * as actionCreators from '../../store/common/actionCreators'

import GameoperateHeader from './components/header'
import GameoperateMemberset from '../gameover/pages/memberSet'
import FillReport from '../gameover/pages/fillexperiencereport'
import Viewreport from '../gameover/pages/previewExperimentReport'
const { Header: AntdHeader, Sider, Content } = Layout

const { SubMenu } = Menu
const rootSubmenuKeys = routes.map(item => item.name)

function GameOperate(props) {
  const history = useHistory()
  // antd 只展开当前活动的导航菜单
  const [openKeys, setOpenKeys] = React.useState(['主页'])
  const onOpenChange = keys => {
    const latestOpenKey = keys.find(key => openKeys.indexOf(key) === -1)
    if (rootSubmenuKeys.indexOf(latestOpenKey) === -1) {
      setOpenKeys(keys)
    } else {
      setOpenKeys(latestOpenKey ? [latestOpenKey] : [])
    }
  }

  const over = () => {
    Modal.confirm({
      title: '是否结束经营？',
      okText: '确认',
      cancelText: '取消',
      onOk() {
        httpUtil.overGame().then(res => {
          console.log(res);
          if (!res.status) {
            history.push(`/gameover/checkJournalEntry`)
          } else {
            message.error(res.data)
          }
        })
      },
      onCancel() {
      },
    })
  }

  return (
    <div className='game-operate-wrapper' >
      <Layout style={{ height: '100%' }}>
        {/* <AntdHeader> */}
        <GameoperateHeader></GameoperateHeader>
        {/* </AntdHeader> */}
        <Layout>
          <Sider>
            <Menu openKeys={openKeys} onOpenChange={onOpenChange} mode='inline'>
              {routes.map(item => {
                if (item.children !== undefined) {
                  return (
                    <SubMenu icon={item.icon} key={item.name} title={item.name}>
                      {item.children
                        ? item.children.map(item => {
                          return (
                            <Menu.Item key={item.name}>
                              <Link to={item.path}>{item.name}</Link>
                            </Menu.Item>
                          )
                        })
                        : null}
                    </SubMenu>
                  )
                } else {
                  return (
                    <Menu.Item icon={item.icon} key={item.name}>
                      <Link to={item.path}>{item.name}</Link>
                    </Menu.Item>
                  )
                }
              })}
              <Menu.Item icon={OverIcon} onClick={over}>结束经营</Menu.Item>
            </Menu>
          </Sider>
          <Content>
            <Switch>
              {routes.map(item => {
                if (item.children !== undefined) {
                  const icon = item.icon
                  const fatherBreadcrumbName = item.name
                  return item.children.map(item => {
                    const ItemRoute = item.route
                    return (
                      <Route key={item.path} path={item.path}>
                        <>

                          <div className={`nav-header ${item.Englishname}`}>
                            <PageHeader
                              className='site-page-header'
                              onBack={() => null}
                              title={item.name != '申请贷款' ? item.name : ''}
                              backIcon={icon}
                              breadcrumb={{
                                routes: [
                                  {
                                    path: item.path,
                                    key: 1,
                                    breadcrumbName: fatherBreadcrumbName,
                                  },
                                  {
                                    path: item.path,
                                    key: 2,
                                    breadcrumbName: item.name,
                                  },
                                ],
                              }}
                              subTitle={item.name != '申请贷款' ? item.Englishname : ''}
                            />
                          </div>
                          <div className='content-wrapper'>
                            {<item.route />}
                          </div>
                        </>
                      </Route>
                    )
                  })
                } else {
                  if (item.name === '主页') {
                    return (
                      <Route key={item.path} path={item.path}>
                        <div className="content-wrapper"> {<item.route />} </div>
                      </Route>
                    )
                  }
                  return (
                    <Route key={item.path} path={item.path}>
                      <>
                        <div className='nav-header'>
                          <PageHeader
                            className='site-page-header'
                            onBack={() => null}
                            title={item.name}
                            subTitle={item.Englishname}
                            backIcon={item.icon}
                            breadcrumb={{
                              routes: [
                                {
                                  path: item.path,
                                  key: 1,
                                  breadcrumbName: '主页',
                                },
                                {
                                  path: item.path,
                                  key: 2,
                                  breadcrumbName: item.name,
                                },
                              ],
                            }}
                          />
                        </div>
                        <div className="content-wrapper"> {<item.route />} </div>
                      </>
                    </Route>
                  )
                }
              })}
              <Route key='gameoperate-setMember' path='/gameoperate/memberset'>
                <GameoperateMemberset />
              </Route>
              <Route key='gameoperate-fillReport' path='/gameoperate/fillreport'>
                <FillReport />
              </Route>
              <Route key='gameoperate-viewReport' path='/gameoperate/viewreport'>
                <Viewreport />
              </Route>
            </Switch>
          </Content>
        </Layout>
      </Layout>
      <br />
    </div >
  )
}

const mapStateToProps = state => ({
  common: state.common,
})
const mapDispatchToProps = {
  getBasic: actionCreators.getBasic,
}

export default connect(mapStateToProps, mapDispatchToProps)(withRouter(GameOperate))
