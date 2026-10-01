/* 
  这里是封装的进度管理模块
*/
import React, { useState, useEffect } from 'react'
import { Button, Table, message, Modal } from 'antd'
import { ExclamationCircleOutlined } from '@ant-design/icons';
import httpUtil from "../../../../../../../utils/request/index"
import { Link, useParams, useHistory } from "react-router-dom"
const { confirm } = Modal;

// 封装一个高阶函数来给每一个进度操作添加一个确认功能
const withOperation = (fn, record, title) => {
  return () => {
    confirm({
      title: title,
      icon: <ExclamationCircleOutlined />,
      onOk() {
        fn(record)
      },
      onCancel() {
      },
      cancelText: "取消",
      okText: "确认"
    });
  }
}


export function ProgressControl(props) {

  const stateObj = { ...props.state }
  const { tea, stu } = props
  const params = useParams()
  const history = useHistory()

  // 发生请求来更新数据
  const updataPCData = (groupName) => {
    // 发送请求
    const formData1 = new FormData()
    formData1.append("groupName", groupName)
    httpUtil.query(formData1).then((res) => {
      // 将数据传输
      stateObj.setProgressData(res.data)
    })
  }

  // 推进下一周期
  const forwardPeriod = (record) => {
    const formData = new FormData()
    formData.append("userUnique", record.userUnique)
    const loading = message.loading("请求中")
    httpUtil.forwardPeriod(formData)
      .then(res => {
        loading()
        if (res.status === 0) {
          if (res.data.forwardStatus === 1) {
            message.success(res.data.message)
          } else {
            message.warning(res.data.message)
          }
        } else {
          message.error(res.message)
        }
        updataPCData(record.groupName)
      })
  }

  // 结束经营
  const endPlayGame = (record) => {
    const formData = new FormData()
    formData.append("userUnique", record.userUnique)
    formData.append("groupName", record.groupName)
    httpUtil.endPlayGame(formData)
      .then(res => {
        if (res.status === 0) {
          message.success(res.data)
          updataPCData(record.groupName)
        } else {
          message.error(res.data || "结束经营失败")
        }
      })
  }

  // 结束广告投放
  const endAdvertising = (record) => {
    const formData = new FormData()
    formData.append("userUnique", record.userUnique)
    httpUtil.endAdvertising(formData)
      .then(res => {
        if (res.status === 0) {
          message.success(res.message)
          updataPCData(record.groupName)
        } else {
          message.error(res.message)
        }
      })
  }

  // 结束选择订单
  const endChooseOrder = (record) => {
    const formData = new FormData()
    formData.append("userUnique", record.userUnique)
    httpUtil.endChooseOrder(formData)
      .then(res => {
        if (res.status === 0) {
          message.success(res.message)
          updataPCData(record.groupName)
        } else {
          message.error(res.message)
        }
      })
  }

  const columns = [
    {
      title: '标识符',
      dataIndex: 'userName',
      key: 'userName',
    },
    {
      title: '时间',
      dataIndex: 'year',
      key: 'year',
      render: (text, record) => {
        const timeInfo = (record) => {
          const { currentPeriod, periodsOfOneYear } = record
          const year = Math.floor(currentPeriod / periodsOfOneYear)
          const period = currentPeriod - periodsOfOneYear * year
          if (record.status === 1) {
            return (
              <span>{period === 0 ? `第${year}年 第4期` : `第${year + 1}年 第${period}期 `}/ <a href onClick={withOperation(forwardPeriod, record, "是否要推进下一周期？")}>推进下一周期</a></span>
            )
          } else {
            return (period === 0
              ? <span>第{year}年 第4期</span>
              : <span>第{year + 1}年 第{period}期</span>)
          }
        }
        return timeInfo(record)
      }
    },
    {
      title: '游戏状态',
      dataIndex: 'enterpriseNumber',
      key: 'enterpriseNumber',
      render: (text, record) => {
        if (record.status === 0) {
          return <span>已破产</span>
        } else if (record.status === 1) {
          return <span>进行中 / <a href onClick={withOperation(endPlayGame, record, "是否要结束经营？")}>结束经营</a></span>
        } else if (record.status === -1) {
          return <span>比赛已结束</span>
        } else if (record.status === 2) {
          return <span>完成比赛</span>
        }
      }
    },
    {
      title: '广告状态',
      key: 'competitionDuration',
      dataIndex: 'competitionDuration',
      render: (text, record) => {
        if (record.finishAdFlag == 1) {
          return <span>广告已投放</span>
        } else {
          return <span>广告投放中 / <a href onClick={withOperation(endAdvertising, record, "是否要结束投放？")}>结束投放</a></span>
        }
      }
    },
    {
      title: '订单状态',
      dataIndex: 'currentPeriodsNumber',
      key: 'currentPeriodsNumber',
      render: (text, record) => {
        if (record.chooseOrderFlag == 0) {
          return <span>无法选单</span>
        } else {
          if (record.finishOrderFlag == 1) {
            return <span>已完成订单选择</span>
          } else {
            return <span>订单选择中 / <a href onClick={withOperation(endChooseOrder, record, "是否要结束选择？")}>结束选择</a></span>
          }
        }
      }
    }
  ];

  // 返回到小组列表时，清除已经获取到的状态
  useEffect(() => {
    // 发送请求
    const formData1 = new FormData()
    formData1.append("groupName", params.groupName)
    httpUtil.query(formData1).then((res) => {
      // 将数据传输
      stateObj.setProgressData(res.data)
    })
    return () => {
      stateObj.setProgressData([])
      stateObj.setProgressLoading(true)
    }
  }, [])

  // 当数据获取到时，取消加载状态
  if (stateObj.progressData?.length) {
    stateObj.setProgressLoading(false)
  }

  return (
    <div>
      <p className="hxy-return">
        <Button type="link" onClick={() => { history.goBack() }} className='search-box' style={{ margin: 0, paddingLeft: 0 }} >
          {'<'} 返回
        </Button>
        <span>
          <strong>{stateObj.progressData[0]?.groupName} 小组</strong>
        </span>
      </p>
      <Table
        columns={columns}
        dataSource={stateObj.progressData}
        pagination={{
          showSizeChanger: true,
          pageSizeOptions: [6, 10, 20, 30],
          defaultPageSize: 20,
          total: stateObj.progressData?.length,
        }}
        loading={stateObj.progressLoading}
      />
    </div>
  )
}