//     /*开发过程中暂时注释掉了鉴权路由组件，避免每次刷新都回到登录界面************************************/
//     /*鉴权路由组件在src\components\authRouter\index.js */
import React from 'react'
import { BrowserRouter, Switch, Route, Link } from 'react-router-dom'
import { connect } from 'react-redux'
import routes from './routes/index'
import styles from './index.module.scss'
import { Menu, Layout, PageHeader } from 'antd'
import GameoperateHeader from './components/header/index'
// 引入样式文件
import './modifyAntdCss.scss'
import 'assets/icons/iconfont.css'
import "./global.scss"

const { Header: AntdHeader, Sider, Content } = Layout

const { SubMenu } = Menu
const rootSubmenuKeys = routes.map(item => item.name)

function Teacher() {
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
  return (
    <div className={styles["game-operate-wrapper"]}>
      <Layout>
        <AntdHeader>
          <GameoperateHeader></GameoperateHeader>
        </AntdHeader>
        <Layout>
          <Sider>
            <Menu openKeys={openKeys} onOpenChange={onOpenChange} mode="inline"  >
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
            </Menu>
          </Sider>
          <Content>
            <Switch>
              <div Layout="inline">
                {routes.map(item => {
                  if (item.children !== undefined) {
                    const icon = item.icon
                    const fatherBreadcrumbName = item.name
                    return item.children.map(item => (
                      <Route key={item.path} path={item.path}>
                        <div className={styles["nav-header"]}>
                          <PageHeader
                            className={styles["site-page-header"]}
                            onBack={() => null}
                            // title={item.name}
                            // backIcon={icon}
                            breadcrumb={{
                              routes: [
                                {
                                  path: item.path,
                                  breadcrumbName: fatherBreadcrumbName,
                                },
                                {
                                  path: item.path,
                                  breadcrumbName: item.name,
                                },
                              ],
                            }}
                          // subTitle={item.Englishname}
                          />
                        </div>
                        <div className={styles["content-wrapper"]}>{item.route}</div>
                      </Route>
                    ))
                  } else {
                    if (item.name === '主页') {
                      return (
                        <Route key={item.path} path={item.path}>
                          <div className={styles["content-wrapper"]}>
                            {item.route}
                            {/* {' '}
                            {item.route()}{' '} */}
                          </div>
                        </Route>
                      )
                    }
                    return (
                      <Route key={item.path} path={item.path}>
                        <>
                          <div className={styles["nav-header"]}>
                            <PageHeader
                              className={styles["site-page-header"]}
                              onBack={() => null}
                              title={item.name}
                              subTitle={item.Englishname}
                              backIcon={item.icon}
                              breadcrumb={{
                                routes: [
                                  {
                                    path: item.path,
                                    breadcrumbName: '主页',
                                  },
                                  {
                                    path: item.path,
                                    breadcrumbName: item.name,
                                  },
                                ],
                              }}
                            />
                          </div>
                          <div className={styles["content-wrapper"]}> {item.route} </div>
                        </>
                      </Route>
                    )
                  }
                })}
              </div>
            </Switch>
          </Content>
        </Layout>
      </Layout>
      <br />
    </div>
  )
}

const mapStateToProps = state => ({
  common: state.common,
})

export default connect(mapStateToProps, null)(Teacher)
