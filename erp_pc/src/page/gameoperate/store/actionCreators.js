import { actionTypes } from './index'
import httpUtil from 'utils/request/index'


//
export const groupMemberList = (data) => ({
    type: actionTypes.GROUP_MEMBER,
    groupMember: data
})

// 获取小组成员
export const getMembers = () => {
    return (dispatch) => {
        httpUtil.getMembersAdInfo()
            .then((res) => {
                dispatch(groupMemberList(res.data.data))
            })
    }
}


// //获取未研发的产品
// const undevelopProducts = (data) => ({
//   type: actionTypes.GET_UNDEVELOP_PRODUCTS,
//   data
// })


// export const getUndevelopProducts = () => {
//   return dispatch => {
//     httpUtil.getUndevelopsProduct.then(res => {
//       if (res.status === 0) {
//         dispatch(
//           undevelopProducts(data)
//         )
//       } else {
//         // 获取失败
//         Modal.warning({
//           title: '提示',
//           content: res.message,
//         })
//       }
//     })
//   }
// }