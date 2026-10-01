import { combineReducers } from 'redux'
import { reducer as commonReducer } from './common'
import { reducer as userprepareReducer } from '../page/userprepare/store'
import { reducer as teacherReducer } from '../page/teacher/store'
import { reducer as gameOperate } from '../page/gameoperate/store'
import { reducer as admin } from '../page/superadmin/store'
export default combineReducers({
  common: commonReducer,
  userprepare: userprepareReducer,
  teacher: teacherReducer,
  gameOperate: gameOperate,
  admin: admin
})
