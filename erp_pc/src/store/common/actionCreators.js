import { actionTypes } from './index'
import httpUtil from 'utils/request/index'
import { message, Modal } from 'antd'
import {
  changeGroupMessage,
  changeGroupList,
  changeGroupMemberList,
} from 'page/userprepare/store/actionCreators'
import ERPWebSocket from 'utils/websocket/index'

export const changeUserMessage = data => ({
  type: actionTypes.CHANGE_USER_MESSAGE,
  userData: data,
})

// 用户退出登录
export const logout = () => ({
  type: actionTypes.LOGOUT,
})

// 发送退出登录的请求
export const getLogout = () => {
  return dispatch => {
    httpUtil.logout().then(res => {
      if (res.status === 0) {
        dispatch(logout())
        ERPWebSocket.destoryInstance()
      }
    })
  }
}

export const changeTeacherMessage = data => ({
  type: actionTypes.CHANGE_TEACHER_MESSAGE,
  userData: data,
})
// 教师退出登录
export const teacherLogout = () => ({
  type: actionTypes.TEACHER_LOGOUT,
})

// 发送退出登录的请求
export const getTeacherLogout = () => {
  return dispatch => {
    httpUtil.teacherLogout().then(res => {
      dispatch(teacherLogout())
    })
  }
}

// 提交注册, 根据返回的 status 进行业务处理
// 0 注册成功
// 7 用户名已经注册
export const postRegister = (params, history) => {
  return dispatch => {
    httpUtil.postRegister(params).then(res => {
      if (res.status === 0) {
        message.success(res.message, 1)
        dispatch(
          changeUserMessage({
            userStatus: 1,
            role: 'student',
            userMessage: res.data.userMessage,
          })
        )
        // 跳转页面
        history.push('/userprepare')
      } else {
        // 注册失败
        Modal.warning({
          title: '提示',
          content: res.message,
        })
      }
    })
  }
}

// 提交登录, 根据返回的 status 进行业务处理
// -1  未注册用户
//  0  验证码错误
//  2  密码错误

//  1  已经注册但未审核的用户
//  3  已审核用户，但是未加入分组
//  7  已经加入一个分组，并且 是 游戏创建者
//  8  已经加入分组，并且是分组成员

//  4  已经破产
//  5  正在比赛

//学生登录
export const postLogin = (params, history) => {

  return dispatch => {
    console.log(params)
    httpUtil.postLogin(params).then(res => {
      switch (res.status) {
        case -1:
          Modal.warning({
            title: '提示',
            content: '该用户尚未注册，只有注册用户才能使用本系统，请进行相应的注册!',
          })
          break
        case 0:
          Modal.warning({
            title: '提示',
            content: res.message,
          })
          break
        case 2:
          Modal.warning({
            title: '提示',
            content: '密码错误，请重新输入',
          })
          break

        case 1:
        case 3:
        case 7:
        case 8:
          dispatch(
            changeUserMessage({
              userMessage: res.data.userMessage,
              userStatus: res.status,
              role: 'student',
            })
          )
          if (res.data.groupList) {
            dispatch(changeGroupList(res.data.groupList))
          }
          if (res.data.groupMessage) {
            dispatch(changeGroupMessage(res.data.groupMessage))
          }
          if (res.data.groupMemberList) {
            dispatch(changeGroupMemberList(res.data.groupMemberList))
          }
          // 建立 websocket 链接
          ERPWebSocket.createInstance(res.data?.groupMessage?.groupName || 'NoGroup')
          history.push('/userprepare')
          break
        case 4:
          dispatch(
            changeUserMessage({
              userMessage: res.data.userMessage,
              userStatus: res.status,
              role: 'student',
            })
          )
          history.push(`/gameover`)
          break
        case 5:
        case 6:
          dispatch(
            changeUserMessage({
              userMessage: res.data.userMessage,
              userStatus: res.status,
              role: 'student',
            })
          )
          // TODO
          history.push('/gameoperate/main')
          break
        default:
      }
    })
  }
}

//教师登录
export const postTeacherLogin = (params, history) => {
  return dispatch => {
    httpUtil.postTeacherLogin(params).then(res => {
      switch (res.status) {
        case -1:
          Modal.warning({
            title: '提示',
            content: '该用户尚未注册，只有注册用户才能使用本系统，请进行相应的注册!',
          })
          break
        case 0:
          Modal.warning({
            title: '提示',
            content: res.message,
          })
          break
        case 2:
          Modal.warning({
            title: '提示',
            content: '密码错误，请重新输入',
          })
          break
        case 1:
        case 3:
        case 7:
        case 8:
        case 4:
        case 5:
        case 6:
        case 200:
          dispatch(
            changeUserMessage({
              userStatus: 1,
              role: 'teacher',
            })
          )
          history.push('/teacher/userList')
          break
        default:
      }
    })
  }
}

export const adminLogin = () => {
  return dispatch => {
    dispatch(
      changeUserMessage({
        userStatus: 1,
        role: 'admin'
      })
    )
  }
}

// 修改用户信息
export const postModifyUser = params => {
  return dispatch => {
    httpUtil.postModifyUser(params).then(res => {
      if (res.status === 0) {
        message.success('修改成功', 1)
        dispatch(
          changeUserMessage({
            userMessage: res.data.userMessage,
          })
        )
      } else {
        // TODO
        // 等一个有缘人把代码补上。from rcw :)
      }
    })
  }
}

// 获取用户信息
// 逻辑和 login 一样
export const getBasic = history => {
  return dispatch => {
    httpUtil.getBasic().then(res => {
      if (res.status) {
        switch (res.status) {
          case 1:
          case 3:
          case 7:
          case 8:
            dispatch(
              changeUserMessage({
                userMessage: res.data.userMessage,
                userStatus: res.status,
                role: 'student',
              })
            )
            if (res.data.groupList) {
              dispatch(changeGroupList(res.data.groupList))
            }
            if (res.data.groupMessage) {
              dispatch(changeGroupMessage(res.data.groupMessage))
            }
            if (res.data.groupMemberList) {
              dispatch(changeGroupMemberList(res.data.groupMemberList))
            }
            // 建立 websocket 链接
            ERPWebSocket.createInstance(res.data?.groupMessage?.groupName || 'NoGroup')
            history.push('/userprepare')
            break
          case 4:
            dispatch(
              changeUserMessage({
                userMessage: res.data.userMessage,
                userStatus: res.status,
                role: 'student',
              })
            )
            history.push(`/gameover`)
            break
          case 5:
          case 6:
            dispatch(
              changeUserMessage({
                userMessage: res.data.userMessage,
                userStatus: res.status,
                role: 'student',
              })
            )
            // TODO
            history.push('/gameoperate')
            break
          default:
        }
      } else if (res.status === 1) {
        // TODO
        // 等一个有缘人把代码补上。from rcw :)
      }
    })
  }
}
