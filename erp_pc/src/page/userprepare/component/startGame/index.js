import React, { useCallback, useState } from 'react'
import { Redirect, withRouter } from 'react-router'
import { connect } from 'react-redux'

import { actionCreators } from '../../store/index.js'

import './index.scss'
import pic_no from 'assets/images/pic_wait.jpg'
import pic_meb6 from 'assets/images/pic_meb6.jpg'
import { Popover, Button, Popconfirm } from 'antd'
import { CloseSquareOutlined } from '@ant-design/icons'

import httpUtil from 'utils/request/index'

function GroupMemberList(props) {
  console.log(props,"props");
  const { groupMemberList, userNumbers, isCreator } = props
  let waitList = new Array(userNumbers - groupMemberList.length)
  waitList.fill('1')

  const tickGroupMember = useCallback(userId => {
    httpUtil.tickGroupMember({ userId, groupName: props.groupName }).then(res => { })
  }, [])

  return (
    <>
      {groupMemberList.length !== 0
        ? groupMemberList.map((item, index) => {
          if (index === 0) {
            return (
              <div className="group-creater" key={item.userId}>
                <Popover
                  content={
                    <div>
                      <p>学号： {item.sid}</p>
                      <p>邮箱： {item.email}</p>
                      <p>联系方式： {item.tel}</p>
                      <p>真实姓名 {item.userName}</p>
                    </div>
                  }
                  title={'用户名：' + item.userId}
                  trigger="hover">
                  <img alt="example" src={pic_meb6} />
                </Popover>
                <div className="member-info">
                  <p>学号：{item.sid || item.userId}</p>
                  <p>姓名：{item.userName || '未填写'}</p>
                </div>
              </div>
            )
          } else {
            return (
              <div className="group-member" key={item.userId}>
                {isCreator ? (
                  <Popconfirm
                    placement="bottom"
                    title={'确认删除该名成员？'}
                    onConfirm={tickGroupMember.bind(this, item.userId)}
                    okText="确认"
                    cancelText="取消">
                    <CloseSquareOutlined className="deleteBtn" />
                  </Popconfirm>
                ) : null}
                <div>
                  <Popover
                    content={
                      <div>
                        <p>学号： {item.sid}</p>
                        <p>邮箱： {item.email}</p>
                        <p>联系方式： {item.tel}</p>
                        <p>真实姓名 {item.userName}</p>
                      </div>
                    }
                    title={'用户名：' + item.userId}
                    trigger="hover">
                    <img alt="example" src={pic_meb6} />
                  </Popover>
                  <div className="member-info">
                  <p>学号：{item.sid || item.userId}</p>
                  <p>姓名：{item.userName || '未填写'}</p>
                </div>
                </div>
              </div>
            )
          }
        })
        : null}
      {waitList.map((item, index) => (
        <img alt="example" key={index} src={pic_no} />
      ))}
    </>
  )
}

function StartGame(props) {
  const { groupName, groupCreatorId, years, periodsOfOneYear, userNumbers } =
    props.userprepare.groupMessage || {}

  const quitGroup = useCallback(() => {
    props.quitGroup(
      {
        groupName: groupName,
        userId: props.common.userMessage.userId,
      },
      props.history
    )
  }, [props, groupName])

  const deleteGroup = useCallback(() => {
    props.deleteGroup(
      {
        groupName: groupName,
      },
      props.history
    )
  }, [props, groupName])

  const startGame = useCallback(
    params => {
      httpUtil.startGame(params).then(res => {
        props.history.push('/gameoperate/main')
      })
    },
    [props]
  )

  const isCreator =
    props?.common?.userMessage?.userId === props?.userprepare?.groupMessage?.groupCreatorId
  return (
    <div className="start-game-wrapper">
      <header>
        <div>组名：{groupName}</div>
        <div>创建人：{groupCreatorId}</div>
        <div> 比赛持续年数：{years}</div>
        <div>当前周期数：{periodsOfOneYear}</div>
      </header>

      <div className="content">
        <GroupMemberList
          groupName={props.userprepare.groupMessage.groupName}
          groupMemberList={props.userprepare.groupMemberList}
          userNumbers={userNumbers ? userNumbers : 0}
          isCreator={isCreator}></GroupMemberList>
      </div>

      <div className="button-wrapper">
        {isCreator ? (
          <>
            <Button type="primary" onClick={props.startGame.bind(null, props.history)}>
              开始比赛
            </Button>
            <Button type="primary" onClick={deleteGroup}>
              解散分组
            </Button>
          </>
        ) : (
          <Button className="quit" type="primary" onClick={quitGroup}>
            退出分组
          </Button>
        )}
      </div>
      {props.common.userStatus === 5 ? <Redirect to="/gameoperate/main"></Redirect> : null}
    </div>
  )
}

const mapStateToProps = state => ({
  userprepare: state.userprepare,
  common: state.common,
})
const mapDispatchToProps = {
  getGroupMemberList: actionCreators.getGroupMemberList,
  quitGroup: actionCreators.quitGroup,
  deleteGroup: actionCreators.deleteGroup,
  startGame: actionCreators.startGame,
}

export default connect(mapStateToProps, mapDispatchToProps)(withRouter(StartGame))

