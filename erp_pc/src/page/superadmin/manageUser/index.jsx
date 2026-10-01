import React, { useState, useEffect } from 'react'
import { message, Table, Button, Input, PageHeader, Modal } from 'antd'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import { actionCreators } from '../store/index'

import httpUtil from 'utils/request/index';
import './index.scss'

function ManageUser(props) {

    // 所有数据
    // 模糊查询数据
    let [data, setData] = useState([])


    // 删除管理员
    const delAdmin = async (value) => {
        if (data.length == 0) setData(props.admin.admins)
        httpUtil.deleteAdmin({ adminId: value.adminId })
            .then(res => {
                if (res.code == 10000) {
                    props.getAdmin()
                    let newArr = data
                    newArr.splice(newArr.indexOf(value), 1)
                    setData([...newArr])
                    message.success('删除成功')

                }
                else message.error('删除失败')
            })

    }

    // 重置管理员密码
    const resetPassword = (value) => {
        httpUtil.setpwd({ adminId: value.adminId })
            .then(res => {
                console.log(res)
                if (res.code == 10000) message.success('密码重置成功')
                else message.error('密码重置失败')
            })
    }

    // 提示
    const toDo = (value, type) => {
        Modal.confirm({
            title: `是否确认${type}？`,
            okText: '确认',
            cancelText: '取消',
            onOk() {
                if (type == '删除管理员') delAdmin(value)
                else resetPassword(value)
            },
            ontouchcancel() { }
        })
    }

    // 根据管理员ID模糊查找
    const searchChange = (e) => {
        let str = ['', ...e.target.value, ''].join('.*'); //转化成正则格式的字符串
        let reg = new RegExp(str) //正则
        let arr = []
        for (let i = 0; i < props.admin.admins.length; i++) {
            if (reg.test(props.admin.admins[i].adminId)) {
                arr.push(props.admin.admins[i])
            }
        }
        setData(arr)
    }

    const columns = [
        {
            title: '管理员用户名ID',
            dataIndex: 'adminId',
            className: 'table-column',
            key: 'adminId',
        },
        {
            title: '姓名',
            dataIndex: 'adminName',
            className: 'table-column',
            key: 'adminName',
        },
        {
            title: '操作',
            key: 'tags',
            dataIndex: 'tags',
            className: 'table-column',
            render: (_, admin) => {
                return (
                    <span>
                        <Button className='table-btn' type='primary' onClick={() => toDo(admin, '删除管理员')}>删除</Button>
                        <Button className='table-btn' type='primary' onClick={() => toDo(admin, '重置管理员密码')}>重置密码</Button>
                    </span>)
            }
        }
    ];

    return (
        <>
            <PageHeader
                className='admin-header'
                subTitle='管理员用户列表'
            />

            <div className='superadmin-wapper'>
                <hr className='admin-hr' />
                <div>
                    <span className='search-admin'>查找：<Input onChange={searchChange} className='search-input' type='text' placeholder='基于管理员ID查找' /></span>
                </div>
                <Table className='admin-table' columns={columns} dataSource={data.length != 0 ? data : props.admin.admins} pagination={{ pageSize: 8 }}></Table>
            </div>
        </>
    )
}

const mapStateToProps = state => ({
    admin: state.admin
})
const mapDispatchToProps = {
    getAdmin: actionCreators.getAdmin
}
export default connect(mapStateToProps, mapDispatchToProps)(withRouter(ManageUser))
