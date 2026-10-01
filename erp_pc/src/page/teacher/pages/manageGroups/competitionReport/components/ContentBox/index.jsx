/* 
  这里是封装的内容模块
*/
import React, {useEffect, useState} from 'react'
import {Table, Button, Space, message} from 'antd'
import httpUtil from 'utils/request'
import {PreviewExperimentReport} from '../index'
import {SERVER} from 'config'

// 定义一个变量存储老师id
let teacherId = ''

let stateObj = {}

export const ContentBox = props => {
  // 设置实验报告模块状态
  const [visible, setVisible] = useState(false)
  const [groupName, setGroupName] = useState('')
  const [data, setData] = React.useState({})
  const [isHaveData, setIsHaveData] = React.useState(false)

  // 预览比赛报告
  const previewCompetitionReport = text => {
    setGroupName(text.groupName)
    setVisible(true)
  }

  // 下载比赛报告
  const downloadCompetitionReport = text => {
    // 发送请求
    httpUtil
      .getDownloadReportPath(text.groupName)
      .then(res => {
        console.log(res);
        console.log(typeof res);
        
        return res.blob()
      })
      .then(blob => {
        console.log(blob);
        
        const link = document.createElement('a')
        if (link.download !== undefined) {
          const url = URL.createObjectURL(blob)
          link.setAttribute('href', url)
          link.setAttribute('download', `${text.groupName}.doc`)
          link.style.visibility = 'hidden'
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
        }
      })
      .catch(() => {
        message.error('下载文件失败')
      })
  }

  // 获取老师的名字
  useEffect(() => {
    httpUtil.getCurTeacherInfo().then(res => {
      teacherId = res.data?.adminName ? res.data.adminName : ''
    })
  }, [])

  // 将状态有关的东西存入stateObj
  stateObj = {...props.state}
  // 当数据获取到时，取消加载状态
  if (stateObj.showData?.length) {
    stateObj.setDataListLoading(false)
  }

  // // 如果数据两秒后还没获取到就显示无数据
  // setTimeout(() => {
  //   if (stateObj.dataListLoading === true) {
  //     stateObj.setDataListLoading(false)
  //   }
  // }, 3000)

  const columns = [
    {
      title: '比赛名称',
      dataIndex: 'groupName',
      key: 'groupName'
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
      key: 'userNumbers'
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
        const timeInfo = record => {
          const {currentPeriod, periodsOfOneYear} = record
          const year = Math.floor(currentPeriod / periodsOfOneYear)
          const period = currentPeriod - periodsOfOneYear * year
          return period === 0 ? (
            <span>第{year}年 第4期</span>
          ) : (
            <span>
              第{year + 1}年 第{period}期
            </span>
          )
        }
        return timeInfo(record)
      }
    },
    {
      title: '操作',
      key: 'operation',
      render: (text, record) => {
        return (
          <Space>
            <Button
              type='primary'
              onClick={previewCompetitionReport?.bind(this, text)}>
              预览比赛报告
            </Button>
            <Button
              type='primary'
              onClick={downloadCompetitionReport?.bind(this, text)}>
              下载比赛报告
            </Button>
          </Space>
        )
      }
    }
  ]

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
            stateObj.setPageControl({page, pageSize})
          }
        }}
        loading={stateObj.dataListLoading}
      />
      {visible ? (
        <PreviewExperimentReport
          state={{
            visible,
            setVisible,
            groupName,
            data,
            setData,
            isHaveData,
            setIsHaveData
          }}
        />
      ) : (
        ''
      )}
    </>
  )
}
