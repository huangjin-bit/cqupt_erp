/* 
  这里是封装的内容模块
*/
import React, { useEffect, useState } from 'react'
import { Table, Button, Modal } from 'antd';
import httpUtil from 'utils/request'
import { showModal, SetGrade } from '../index'


// 定义一个变量存储老师id
let teacherId = ''

let stateObj = {}


export const ContentBox = (props) => {

  // 设置状态
  // const [isShowModal, setIsShowModal] = useState(false)

  // 获取老师的名字
  useEffect(() => {
    httpUtil.getCurTeacherInfo()
      .then(res => {
        teacherId = res.data?.adminName ? res.data.adminName : ''
      })
  }, [])

  // 将状态有关的东西存入stateObj
  stateObj = { ...props.state }
  // 当数据获取到时，取消加载状态
  if (stateObj.showData?.length) {
    stateObj.setDataListLoading(false)
  }
  // 如果数据两秒后还没获取到就显示无数据
  setTimeout(() => {
    if (stateObj.dataListLoading === true) {
      stateObj.setDataListLoading(false)
    }
  }, 3000)

  const columns = [
    {
      title: '比赛名称',
      dataIndex: 'groupName',
      key: 'groupName',
    },
    // {
    //   title: '比赛创建人',
    //   dataIndex: 'groupCreatorId',
    //   key: 'groupCreatorId',
    //   render: (text, record) => {
    //     return <span>{text ? text : `${teacherId}老师`}</span>
    //   }
    // },
    {
      title: '企业数',
      dataIndex: 'userNumbers',
      key: 'userNumbers',
    },
    // {
    //   title: '比赛时长(年)',
    //   key: 'years',
    //   dataIndex: 'years',
    // },
    {
      title: '时间',
      dataIndex: 'currentPeriod',
      key: 'currentPeriod',
      render: (text, record) => {
        const timeInfo = (record) => {
          const { currentPeriod, periodsOfOneYear } = record
          const year = Math.floor(currentPeriod / periodsOfOneYear)
          const period = currentPeriod - periodsOfOneYear * year
          return (
            period === 0
              ? <span>第{year}年 第4期</span>
              : <span>第{year + 1}年 第{period}期</span>
          )
        }
        return timeInfo(record)
      }
    },
    {
      title: '最高分',
      dataIndex: 'maxScore',
      key: 'maxScore',
    },
    {
      title: '最低分',
      dataIndex: 'minScore', // 也有可能最低分字段是passed
      key: 'minScore',
    },
    {
      title: '操作',
      key: 'operation',
      render: (text, record) => {
        return (
          <>
            <Button type="primary" onClick={showModal?.bind(this, text)}>设置成绩</Button>
          </>
        )
      },
    },
  ];

  return (
    <>
      <Table
        columns={columns}
        dataSource={stateObj.showData}
        pagination={{
          total: stateObj.showData?.length,
          pageSizeOptions: [6, 10, 20, 30],
          defaultPageSize: stateObj.pageControl.pageSize,
          current: stateObj.pageControl.page,
          onChange: (page, pageSize) => {
            stateObj.setPageControl({ page, pageSize })
          }
        }}
        loading={stateObj.dataListLoading}
      />
      <SetGrade state={{ ...stateObj }} />
    </>

  )

}