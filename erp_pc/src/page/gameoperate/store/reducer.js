import { actionTypes } from './index'

const defaultState = {
  member: []
}


export default (state = defaultState, action) => {
  switch (action.type) {
    case actionTypes.GROUP_MEMBER:{
      return Object.assign({}, state, { member: action.groupMember})}
    default:
      return state
  }
}
