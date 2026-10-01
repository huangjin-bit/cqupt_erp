import React, { useState, useEffect } from 'react'
import { message, Table, Button, Input, PageHeader, Modal } from 'antd'
import httpUtil from 'utils/request/index'
import { nanoid } from 'nanoid'
import './index.scss'
import { indexOf, values } from '@antv/util'

export default function ReportList() {
    // 所有数据
    let [allData, setAllData] = useState([])

    // 查找的数据
    let [data, setData] = useState([])

    let [delFilesData, setDelFilesData] = useState([])

    useEffect(() => {
        httpUtil.getAllFiles().then(res => {
            console.log(res)
            if (res.code == 10000) {
                setAllData(res.data)
                setData(res.data)

            }
        })
    }, [])



    // 单个删除文件
    const delWord = (value) => {
        httpUtil.delFile({ fileName: value.name }).then(res => {
            if (res.code == 10000) {
                let arr = allData
                arr.splice(arr.indexOf(value), 1)
                let arr1 = data
                arr1.splice(arr1.indexOf(value), 1)
                setAllData([...arr])
                setData([...arr1])
                message.success('删除成功')
            }
            else {
                message.error('删除失败')
            }
        })

    }


    // 根据word文件名模糊查找
    const searchChange = (e) => {
        let str = ['', ...e.target.value, ''].join('.*'); //转化成正则格式的字符串
        let reg = new RegExp(str) //正则
        let arr = []
        for (let i = 0; i < allData.length; i++) {
            if (reg.test(allData[i].name)) {
                arr.push(allData[i])
            }
        }
        setData(arr)
    }

    // 批量选择
    const rowSelection = {
        onChange: (_, selectedRows) => {
            console.log(selectedRows);
            setDelFilesData(selectedRows)
        },
    };

    // 批量删除
    const delFiles = () => {
        console.log(delFilesData)
        httpUtil.delFiles({ fileInfos: delFilesData }).then(res => {
            if (res.code == 10000) {
                let arr = allData
                let arr1 = data
                delFilesData.forEach(item => {
                    arr.splice(arr.indexOf(item), 1)
                    arr1.splice(arr1.indexOf(item), 1)
                })
                setAllData([...arr])
                setData([...arr1])
                message.success('删除成功')
            }
            else {
                message.error('删除失败')
            }
        })
    }

    // 清空文件
    const delAllFiles = () => {
        httpUtil.delAllFiles().then(res => {
            if (res.code == 10000) {
                setAllData([...[]])
                setData([...[]])
                message.success('删除成功')
            }
            else {
                message.error('清空失败')
            }
        })
    }

    const delTag = (type, value = null) => {
        return () => {
            Modal.confirm({
                title: '是否确认删除？',
                okText: '确认',
                cancelText: '取消',
                onOk() {
                    if (type === 1) delWord(value)
                    if (type === 2) delFiles()
                    if (type === 3) delAllFiles()
                },
                ontouchcancel() { }
            })
        }
    }

    const columns = [
        {
            title: '文件名字',
            dataIndex: 'name',
            className: 'table-big',
            key: 'name',
        },
        {
            title: '文件大小/KB',
            dataIndex: 'fileSize',
            className: 'table-column',
            key: 'fileSize',
        },
        {
            title: '文件类型',
            dataIndex: 'fileType',
            className: 'table-column',
            key: 'fileType',
        },
        {
            title: '创建日期',
            dataIndex: 'createTime',
            className: 'table-column',
            key: 'createTime',
        },
        {
            title: '操作',
            key: 'tags',
            dataIndex: 'tags',
            className: 'table-column',
            render: (_, file) => {
                return (
                    <span>
                        <Button type='primary' onClick={delTag(1, file)}>删除</Button>
                    </span>)
            }
        }
    ];

    return (
        <>
            <PageHeader
                className='admin-header'
                subTitle='教师Word实验报告列表'
            />

            <div className='superadmin-wapper'>
                <hr className='admin-hr' />
                <div>
                    <span className='search-admin'>查找：<Input onChange={searchChange} className='search-input' type='text' placeholder='基于文件名查找' /></span>
                </div>
                <Table
                    rowSelection={{
                        type: 'checkbox',
                        ...rowSelection,
                    }}
                    size='small'
                    className='admin-table'
                    columns={columns}
                    dataSource={data}
                    rowKey='name'
                    pagination={false} />
                <Button danger onClick={delTag(2)}>批量删除</Button>
                <Button className='danger-btn' danger onClick={delTag(3)}>清空文件</Button>
            </div>

        </>
    )
}
