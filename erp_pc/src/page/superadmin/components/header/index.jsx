import React, { Fragment, useEffect, useState } from 'react'
import { connect } from 'react-redux'
import { Redirect, Link, withRouter } from 'react-router-dom'


import { Modal } from 'antd'
import './index.scss'
import { Button, Popover, message, Form, Input } from 'antd'

import { ReloadOutlined } from '@ant-design/icons'
import { UsergroupDeleteOutlined } from '@ant-design/icons'
import { PoweroffOutlined } from '@ant-design/icons'
import logoUrl from 'assets/images/startGame_logo.jpg'
import headUrl from 'assets/images/pic_meb2.jpg'
import { actionCreators } from '../../store/index'

import httpUtil from 'utils/request'



function Header(props) {

    let [visible, setVisible] = useState(false)

    let layout = {
        labelCol: { span: 5 },
        wrapperCol: { span: 16 }
    }
    let tailLayout = {
        wrapperCol: { offset: 8, span: 16 }
    }

    useEffect(() => {
        props.getAdmin()
    }, [])

    // 退出登录
    const onUserLogout = () => {
        // localStorage.clear()
        httpUtil
            .logout()
            .then(res => {
                if (!res.status)
                    localStorage.clear()
                const listData = res.data
                const key = 'updatable'
                message.loading({ content: 'Loading...', key })
                setTimeout(() => {
                    message.info({
                        content: listData + '!',
                        key,
                        duration: 2
                    }

                    )

                }, 200)
            }, <Redirect to='/login' />)
            .catch((err) => console.log(err))
    }

    // 页面刷新
    const reload = () => {
        window.location.reload()
    }

    // 新建管理员
    const setAdmin = () => {
        setVisible(true)
    }

    // modal取消
    const handleCancel = () => {
        setVisible(false)
    }

    const onFinish = (values) => {
        for (let i = 0; i < props.admin.admins.length; i++) {
            if (props.admin.admins[i].adminId == values.userId) {
                message.warn('用户名已存在')
                return
            }
        }
        httpUtil.addAdmin({
            adminId: values.userId,
            adminName: values.userName,
            adminPwd: values.password,
        })
            .then(res => {
                if (res.code == 10000) {
                    props.getAdmin()
                    message.success('新建管理员用户成功')
                    setVisible(false)
                }
                else message.error('新建管理员用户失败')
            })
    }


    return (
        <Fragment>
            <div className="superadmin-header-wrapper">
                <img src={logoUrl} />
                <div className="content-wrapper">
                    <div className="button-wrapper">
                        <Popover placement="topLeft" content={'新建管理员'} trigger="hover">
                            <Button type="primary" onClick={setAdmin} icon={<UsergroupDeleteOutlined />} size="large" />
                        </Popover>
                        <Popover placement="topLeft" content={'点击刷新'} trigger="hover">
                            <Button type="primary" onClick={reload} icon={<ReloadOutlined />} size="large" />
                        </Popover>
                        <Popover placement="topLeft" content={'退出登录'} trigger="hover">
                            <Link to="/login">
                                <Button
                                    onClick={onUserLogout}
                                    type="primary"
                                    icon={<PoweroffOutlined />}
                                    size="large"
                                />
                            </Link>
                        </Popover>
                    </div>

                    <Modal destroyOnClose title='新建管理员用户' visible={visible} footer={null} onCancel={handleCancel}>
                        <Form onFinish={onFinish} {...layout}>

                            <Form.Item label='用户ID' name='userId' rules={[{ required: true, message: '请输入用户ID' }]}>
                                <Input></Input>
                            </Form.Item>
                            <Form.Item label='姓名' name='userName' rules={[{ required: true, message: '请输入姓名' }]}>
                                <Input></Input>
                            </Form.Item>
                            <Form.Item label='密码' name='password' rules={[{ required: true, message: '请输入密码' }]}>
                                <Input></Input>
                            </Form.Item>
                            <Form.Item {...tailLayout}>
                                <Button type="primary" htmlType='submit'>
                                    创建
                                </Button>
                                <Button style={{ marginLeft: '20px' }} type="primary" onClick={handleCancel}>
                                    取消
                                </Button>
                            </Form.Item>
                        </Form>
                    </Modal>

                    <div className="user-wrapper">
                        <img src={headUrl}></img>
                        <div className="message-wrapper">
                            <div className="message">您好，superadmin</div>
                        </div>
                    </div>
                </div>
            </div>
        </Fragment >
    )
}


const mapStateToProps = state => ({
    admin: state.admin
})
const mapDispatchToProps = {
    getAdmin: actionCreators.getAdmin
}
export default connect(mapStateToProps, mapDispatchToProps)(withRouter(Header))
