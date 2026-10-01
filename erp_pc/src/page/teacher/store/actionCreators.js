import {actionTypes} from './index'

import httpUtil from 'utils/request'
import {message} from 'antd'

export const changeFindAllApprovedUser = data => ({
  type: actionTypes.CHANGE_USER_LIST,
  userList: data
})
export const changeFindAllRegister = data => ({
  type: actionTypes.CHANGE_USER_APPROVAL,
  userApproval: data
})

// 获取审批用户（用户列表）
export const findAllApprovedUser = () => {
  return dispatch => {
    httpUtil.findAllApprovedUser().then(res => {
      dispatch(changeFindAllApprovedUser(res.data))
    })
  }
}
// 获取待审批用户
export const findAllRegister = () => {
  return dispatch => {
    httpUtil.findAllRegister().then(res => {
      dispatch(changeFindAllRegister(res.data))
    })
  }
}

//批量审批用户passBatchRegisterUsers
export const passBatchRegisterUsers = userIds => {
  return dispatch => {
    httpUtil.getTeacherList(userIds).then(res => {
      //   const userIds = this.state.selectedRowKeys.join()
      const adminId = res.data
      const param = adminId[0].adminId
      httpUtil
        .passBatchRegisterUsers({userIds, adminId: param})
        .then(res => {
          console.log(res)
          const key = 'updatable'
          message.loading({content: 'Loading...', key})
          setTimeout(() => {
            message.success({
              content: '批量审批成功',
              key,
              duration: 2
            })
          }, 200)
        })
        .catch(err => console.log(err))
    })
  }
}

// 同意审批用户
export const passRegisterUser = userId => {
  return dispatch => {
    let param
    httpUtil.getTeacherList(userId).then(res => {
      const adminId = res.data
      param = adminId[0].adminId
      httpUtil
        .passRegisterUser({userId, adminId: param})
        .then(res => {
          console.log(res)
          const key = 'updatable'
          message.loading({content: 'Loading...', key})
          setTimeout(() => {
            message.success({
              content: userId + '，审批成功',
              key,
              duration: 2
            })
          }, 200)
          //   dispatch(changeFindAllRegister(res.data.userApproval))
          //   this.props.loadData()
        })
        .catch(err => console.log(err))
    })
  }
}
