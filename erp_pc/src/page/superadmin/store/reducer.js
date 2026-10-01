import { actionTypes } from './index'

const defaultState = {
  admins: []
}

export default (state = defaultState, action) => {
  switch (action.type) {
    case actionTypes.CHANGE_ADMIN:
      return Object.assign({}, state, { admins: action.adminList })
    default:
      return state
  }
}
