import React, { useState, useEffect } from 'react'
import { Button, Table, message, Modal } from 'antd'
import { ExclamationCircleOutlined } from '@ant-design/icons';
import { Link, useParams, useHistory } from "react-router-dom"
import httpUtil from "../../../../../../../utils/request/index"
const { confirm } = Modal;

// 封装一个高阶函数来给每一个进度操作添加一个确认功能
const withOperation = (fn, title) => {
  return () => {
    confirm({
      title: title,
      icon: <ExclamationCircleOutlined />,
      onOk() {
        fn()
      },
      onCancel() {
      },
      cancelText: "取消",
      okText: "确认"
    });
  }
}


export const ConfirmScore = (props) => {

  const stateObj = { ...props.state }
  const { tea, stu } = props
  const params = useParams()
  const history = useHistory()

  const confirmAllScore = () => {
    const members = stateObj.contributionData
    let count = 0
    members.forEach(member => {
      console.log(stateObj.contributionData);
      if (member.confirm === false) {
        count++
        const formData = new FormData()
        formData.append("userUnique", member.userUnique)
        httpUtil.confirmScore(formData)
          .then(res => {
            if (res.status === 0) {
              message.success(res.message)
              updataPCData(member.userUnique)
            } else {
              message.error(res.message)
            }
          })
      }
    })
    if (count === 0) {
      message.warning("贡献度已全部确认！")
    }
  }

  // 发生请求来更新数据
  const updataPCData = (userUnique) => {
    // 发送请求
    const formData = new FormData()
    formData.append("/userUnique", userUnique)
    httpUtil.listContribution(formData).then((res) => {
      // 将数据传输
      stateObj.setContributionData(res.data)
    })
  }

  const columns = [
    // {
    //   title: '比赛名称',
    //   dataIndex: 'groupName',
    //   key: 'groupName',
    // },
    {
      title: '职务',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '学号',
      dataIndex: 'studentNo',
      key: 'studentNo',
    },
    {
      title: '姓名',
      key: 'studentName',
      dataIndex: 'studentName',
    },
    {
      // 降序
      title: '贡献排名',
      dataIndex: 'contribution',
      key: 'contribution',
      defaultSortOrder: 'ascend',
      sorter: (a, b) => a.contribution - b.contribution 
    },
    {
      title: '状态',
      dataIndex: 'confirmScore',
      key: 'confirmScore',
      render: (text, record) => {
        if (record) {
          return (
            record.confirm
              ?
              <span style={{ color: "rgb(82,196,26)" }}>已确认</span>
              :
              <span style={{ color: "rgb(250,173,20)" }}>未确认</span>
          )
        }
      },
    }
  ];

  // 返回到小组列表时，清除已经获取到的状态
  useEffect(() => {
    const formData = new FormData()
    formData.append("/userUnique", params.userUnique)
    httpUtil.listContribution(formData).then((res) => {
      // 将数据传输
      stateObj.setContributionData(res.data)
    }).finally(() => {
      stateObj.setContributionLoading(false)
    })
    return () => {
      stateObj.setContributionData([])
      stateObj.setContributionLoading(true)
    }
  }, [])

  return (
    <div>
      <p className="hxy-return">
        <Button type="link" onClick={() => { history.goBack() }} className='search-box' style={{ margin: 0, paddingLeft: 0 }} >
          {'<'} 返回
        </Button>
        <span>
          <strong>{stateObj.contributionData[0]?.groupName}--{stateObj.contributionData[0]?.studentName}的企业</strong>
        </span>
      </p>
      <Table
        columns={columns}
        dataSource={stateObj.contributionData}
        pagination={{
          pageSizeOptions: [6, 10, 20, 30],
          defaultPageSize: 6,
          total: stateObj.contributionData?.length,
        }}
        loading={stateObj.contributionLoading}
      />
      <Button
        type="primary"
        onClick={withOperation(confirmAllScore, "是否一键确认？")}
        style={{
          position: "absolute",
          marginTop: -48,
        }}
      >
        一键确认
      </Button>
    </div>
  )
}