import {actionTypes} from './index'
import {actionCreators as CommonActionCreators} from 'store/common/index.js'
import * as userpreapreActionCreators from 'page/userprepare/store/actionCreators'

import httpUtil from 'utils/request/index'
import {actionCreators} from 'store/common/index'
import {message} from 'antd'
import ERPWebSocket from 'utils/websocket/index'

// import HttpUtil from 'utils/request/http'

export const changeGroupMessage = data => ({
  type: actionTypes.CHANGE_GROUP_MESSAGE,
  groupMessage: data
})

export const changeGroupMemberList = data => ({
  type: actionTypes.CHANGE_GROUP_MEMBER_LIST,
  groupMemberList: data
})

export const changeGroupList = data => ({
  type: actionTypes.CHANGE_GROUP_LIST,
  groupList: data
})

// 获取分组列表
export const getGroupList = () => {
  return dispatch => {
    httpUtil.getGroupList().then(res => {
      dispatch(changeGroupList(res.data))
    })
  }
}

// 获取分组成员列表
export const getGroupMemberList = params => {
  return dispatch => {
    httpUtil.getGroupMemberList(params).then(res => {
      console.log(res, 123123)
      dispatch(changeGroupMemberList(res.data))
    })
  }
}

// 未加入分组的用户加入分组
export const joinGroup = params => {
  return dispatch => {
    httpUtil.joinGroup(params).then(res => {
      if (res.status === 0) {
        dispatch(changeGroupMemberList(res.data.groupMemberList))
        dispatch(changeGroupMessage(res.data.groupMessage))
        dispatch(actionCreators.changeUserMessage({userStatus: 8}))

        // 更新 websocket 链接
        ERPWebSocket.updateInstance(res.data.groupMessage.groupName)
      } else {
      }
    })
  }
}

// 创建分组
export const postCreateGroup = (userId, params, history) => {
  return dispatch => {
    httpUtil.postCreateGroup(userId, params).then(res => {
      if (res.status === 0) {
        message.success('创建分组成功')
        dispatch(changeGroupMemberList(res.data.groupMemberList))
        dispatch(changeGroupMessage(res.data.groupMessage))
        dispatch(actionCreators.changeUserMessage({userStatus: 7}))
        // 更新 websocket 链接
        ERPWebSocket.updateInstance(res.data.groupMessage.groupName)
      } else {
        message.error(res.data)
      }
    })
  }
}

// 退出分组
export const quitGroup = (params, history) => {
  return dispatch => {
    httpUtil.quitGroup(params).then(res => {
      if (res.status === 0) {
        dispatch(changeGroupMemberList([]))
        dispatch(changeGroupMessage({}))
        dispatch(actionCreators.changeUserMessage({userStatus: 3}))
        // 更新 websocket 链接
        ERPWebSocket.updateInstance('NoGroup')
        history.push('/userprepare')
      } else {
      }
    })
  }
}

// 解散分组
export const deleteGroup = (params, history) => {
  return (dispatch, getState) => {
    let state = getState()
    let obj = {
      groupName: state.userprepare.groupMessage.groupName,
      userId: state.common.userMessage.userId
    }
    console.log(dispatch, state)
    httpUtil.deleteGroup(params).then(res => {
      if (res.status === 0) {
        // httpUtil.quitGroup(obj).then((result) => {
        //   if (result.status === 0) {
        //     dispatch(changeGroupMemberList([]))
        //     dispatch(changeGroupMessage({}))
        //     dispatch(actionCreators.changeUserMessage({ userStatus: 3 }))
        //     // 更新 websocket 链接
        //     ERPWebSocket.updateInstance('NoGroup')
        //     history.push('/userprepare')
        //   } else {
        //   }
        // })
        dispatch(changeGroupMemberList([]))
        dispatch(changeGroupMessage({}))
        dispatch(actionCreators.changeUserMessage({userStatus: 3}))
        // 更新 websocket 链接
        ERPWebSocket.updateInstance('NoGroup')
        history.push('/userprepare')
      } else {
      }
    })
  }
}

// 开始比赛
export const startGame = history => {
  return dispatch => {
    history.push('/loading')
    httpUtil.startGame().then(res => {
      if (res.status === 0) {
        httpUtil.modifyStatus()
        dispatch(changeGroupMessage(res.data))
        dispatch(actionCreators.changeUserMessage({userStatus: 5}))
        ERPWebSocket.destoryInstance()
        history.push('/gameoperate/main')
      } else if (res.data === `游戏已开始`) {
        ERPWebSocket.destoryInstance()
        history.push(`/gameoperate/main`)
      } else {
      }
    })
  }
}
