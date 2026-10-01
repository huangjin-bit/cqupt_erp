import {actionTypes} from './index'

const defaultState = {
  userList: [],
  userApproval: []
}

export default (state = defaultState, action) => {
  switch (action.type) {
    case actionTypes.CHANGE_USER_LIST:
      return Object.assign({}, state, {userList: action.userList})

    case actionTypes.CHANGE_USER_APPROVAL:
      return Object.assign({}, state, {userApproval: action.userApproval}) //Object.assign  是es6写法，不改变数据
    default:
      return state
  }
}
