import React from 'react'
import { Switch, Route, Link } from 'react-router-dom'
import { PageHeader, Layout, Menu, Modal, message } from 'antd'
import httpUtil from 'utils/request'
import SuperAdminHeader from './components/header'

import routes from './routes'

import { nanoid } from 'nanoid'
import './index.scss'
import {
    OverIcon
} from 'assets/icons/index'

const { Header: AntdHeader, Sider, Content } = Layout


export default function SuperAdmin() {

    const deleteAll = () => {
        Modal.confirm({
            title: '是否确认删除所有数据？',
            okText: '确认',
            cancelText: '取消',
            onOk() {
                httpUtil.delAllDatas().then(res => {
                    if (res.code == 10000) {
                        message.success('删除所有数据成功')
                    }
                    else {
                        message.error('删除所有数失败')
                    }
                })
            },
            ontouchcancel() { }
        })

    }

    return (
        <div className='superadmin-wrapper' >
            <Layout style={{ height: '100%' }}>
                {/* <AntdHeader> */}
                <SuperAdminHeader />
                {/* </AntdHeader> */}
                <Layout>
                    <Sider>
                        <Menu>
                            {
                                routes.map(item => {
                                    return <Menu.Item key={nanoid()} className='menuItem' icon={item.icon} key={nanoid()}>
                                        <Link to={item.path}>{item.name}</Link>
                                    </Menu.Item>
                                })
                            }
                            <Menu.Item key={nanoid()} className='menuItem' icon={OverIcon} onClick={deleteAll}>删除所有数据</Menu.Item>
                        </Menu>
                    </Sider>
                    <Content>
                        <Switch>
                            {routes.map(item => {
                                return <Route key={item.path} path={item.path}>
                                    <div className='content-wrapper'>
                                        {<item.route />}
                                    </div>
                                </Route>
                            })
                            }
                        </Switch>
                    </Content>
                </Layout>
            </Layout>
            <br />
        </div >
    )
}
