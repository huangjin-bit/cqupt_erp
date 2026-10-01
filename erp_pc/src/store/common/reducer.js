import { actionTypes } from './index.js'

const defaultState = {
  userMessage: {},
  userStatus: -1,
  role: 'unauthorized',
}
export default (state = defaultState, action) => {
  switch (action.type) {
    case actionTypes.CHANGE_USER_MESSAGE:
      return Object.assign({}, state, action.userData)
    case actionTypes.LOGOUT:
      return {
        userMessage: {},
        userStatus: -1,
        role: 'unauthorized',
      }
    case actionTypes.CHANGE_STUDENT_STATUS:
      return Object.assign({}, state, { userStatus: action.userStatus })

    case actionTypes.TEACHER_LOGOUT:
      return {
        // 此处一个bug，退出设置unauthorized，在登录之后role是unauthorized，退出之后role才是teacher
        // 但是刷新之后role还是'unauthorized'
        // role: 'unauthorized',
        userStatus: 1,

        role: 'teacher',
      }
    case actionTypes.TEACHER_LOGIN_SUCCESS:
      return {
        userStatus: 1,
        role: 'teacher',
      }
    // case actionTypes.CHANGE_TEACHER_MESSAGE:
    //   return {
    //     userStatus: 1,
    //     role: 'teacher',
    //   }

    default:
      return state
  }
}
