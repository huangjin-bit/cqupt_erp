/* 
  这里是封装的进度管理模块
*/
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


export const ShowGroups = (props) => {

  const stateObj = { ...props.state }
  const { tea, stu } = props
  const params = useParams()
  const history = useHistory()

  const [willConfirmData, setWillConfirmData] = useState([])

  // // 发生请求来更新数据
  // const updataPCData = (userUnique) => {
  //   // 发送请求
  //   const formData1 = new FormData()
  //   formData1.append("/userUnique", userUnique)
  //   httpUtil.listContribution(formData1).then((res) => {
  //     const newItem = res.data[0]
  //     let Data = [...stateObj.groupsData]
  //     Data.forEach((user, i) => {
  //       if (user.userUnique === newItem.userUnique) {
  //         Data[i] = newItem
  //       }
  //     })
  //     // 将数据传输
  //     stateObj.setGroupsData(Data)
  //   })
  // }

  const confirmAll = () => {
    if (willConfirmData.length !== 0) {
      willConfirmData.forEach(group => {
        const formData = new FormData()
        formData.append("/userUnique", group.userUnique)
        httpUtil.listContribution(formData).then((res) => {
          // 将数据传输
          const members = res.data
          let count = 0
          members.forEach(member => {
            if (member.confirm === false) {
              count++
              const formData = new FormData()
              formData.append("userUnique", member.userUnique)
              httpUtil.confirmScore(formData)
                .then(res => {
                  if (res.status === 0) {
                    message.success(res.message)
                  } else {
                    message.error(res.message)
                  }
                })
            }
          })
          if (count === 0) {
            message.warning("贡献度已全部确认！")
          }
        })
      })
    } else {
      message.warning("请先选择小组！")
    }
  }

  const columns = [
    {
      title: '比赛名称',
      dataIndex: 'groupName',
      key: 'groupName',
    },
    {
      title: '企业负责人',
      key: 'userName',
      dataIndex: 'userName',
    },
    {
      title: '组内贡献情况',
      dataIndex: 'confirmScore',
      key: 'confirmScore',
      render: (text, record) => {
        return (
          <Link to={`/teacher/contributionConfirmation/showgroups/confirmscore/${record?.userUnique}`}>
            <Button type="primary">确认/查看贡献度</Button>
          </Link>
        )
      },
    }
  ];

  // 返回到小组列表时，清除已经获取到的状态
  useEffect(() => {
    // 发送请求
    const formData1 = new FormData()
    formData1.append("groupName", params.groupName)
    httpUtil.query(formData1).then((res) => {
      stateObj.setGroupsData(res.data)
    })
    return () => {
      stateObj.setGroupsData([])
      stateObj.setGroupDataLoading(true)
    }
  }, [])

  // 当数据获取到时，取消加载状态
  if (stateObj.groupsData?.length) {
    stateObj.setGroupDataLoading(false)
  }

  return (
    <div>
      <p className="hxy-return">
        <Button type="link" onClick={() => { history.goBack() }} className='search-box' style={{ margin: 0, paddingLeft: 0 }} >
          {'<'} 返回
        </Button>
      </p>
      <Table
        columns={columns}
        dataSource={stateObj.groupsData}
        rowSelection={{
          type: "checkbox",
          onChange: (selectedRowKeys, selectedRows) => {
            setWillConfirmData(selectedRows)
          },
        }}
        rowKey="userUnique"
        pagination={{
          showSizeChanger: true,
          pageSizeOptions: [6, 10, 20, 30],
          total: stateObj.groupsData?.length,
          defaultPageSize: stateObj.pageControl.showGroups.pageSize,
          current: stateObj.pageControl.showGroups.page,
          onChange: (page, pageSize) => {
            stateObj.setPageControl((pre) => ({
              ...pre,
              showGroups: { page, pageSize }
            }))
          }
        }}
        loading={stateObj.groupDataLoading}
      />
      <Button
        type="primary"
        style={{
          position: "absolute",
          marginTop: -50
        }}
        onClick={withOperation(confirmAll, "确定一键确认吗？")}
      >
        一键确认
      </Button>
    </div>
  )
}