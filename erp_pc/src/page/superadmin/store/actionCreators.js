import { actionTypes } from './index'
import httpUtil from 'utils/request/index'


//
export const changeAdminList = (data) => ({
    type: actionTypes.CHANGE_ADMIN,
    adminList: data,
})

export const getAdmin = () => {
    return (dispatch) => {
        httpUtil.getAdminList().then(res => {
            if (res.code == 10000) {
                let arr = []
                res.data.map(item => {
                    let obj = {
                        adminId: item.adminId,
                        adminName: item.adminName
                    }
                    arr.push(obj)
                })
                dispatch(changeAdminList(arr))
            }
        })
    }
}