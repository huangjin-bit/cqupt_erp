import {actionTypes} from './index'

const defaultState = {
  groupMessage: {},
  groupMemberList: [],
  groupList: []
}

export default (state = defaultState, action) => {
  switch (action.type) {
    case actionTypes.CHANGE_GROUP_MESSAGE:
      return Object.assign({}, state, {groupMessage: action.groupMessage})
    case actionTypes.CHANGE_GROUP_MEMBER_LIST:
      console.log(
        Object.assign({}, state, {
          // 过滤掉所有为 null 的信息
          groupMemberList: action.groupMemberList.filter(
            memberMessage => memberMessage
          )
        }),
        'error'
      )
      return Object.assign({}, state, {
        // 过滤掉所有为 null 的信息
        groupMemberList: action.groupMemberList.filter(
          memberMessage => memberMessage
        )
      })
    case actionTypes.CHANGE_GROUP_LIST:
      return Object.assign({}, state, {groupList: action.groupList})
    default:
      return state
  }
}
