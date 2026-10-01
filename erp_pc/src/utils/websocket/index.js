
// // 单例模式，所有的页面共享一个 websocket 链接
// // 所以只需要一个 websocket 链接即可
// import PubSub from 'pubsub-js'

// import * as userpreapreActionCreators from 'page/userprepare/store/actionCreators'
// import * as gameoperateActionCrrators from 'page/gameoperate/store/actionCreators'
// import { actionCreators as CommonActionCreators } from 'store/common/index.js'

// import httpUtil from 'utils/request'
// import store from 'store/index'
// import { message } from 'antd'



// const WebSocketUrl = 'ws://172.22.7.210:8080/erpn/commonWebsocket?groupName='

// export default class ERPWebSocket {
//   // 关闭 websocket 链接
//   static destoryInstance() {
//     this.ws.close()
//     this.ws = null
//   }

//   // 更新 websockt 链接
//   static updateInstance(param) {
//     if (this.ws) {
//       this.ws.close()
//       this.createInstance(param)
//     } else {
//       this.createInstance(param)
//     }
//   }

//   // 创建 websocket 链接
//   static async createInstance(param) {
//     if (this.ws) {
//       return
//     }
//     this.ws = new WebSocket(WebSocketUrl + param)
//     this.description = 'erpwebsocket'
//     this.param = param
//     store.dispatch(userpreapreActionCreators.getGroupList())
//     store.dispatch(userpreapreActionCreators.getGroupMemberList(this.param))

//     this.ws.onclose = e => {
//       message.info('websocket onclose')
//       console.log('onclose')
//     }

//     this.ws.onerror = e => {
//       message.info('websocket onerror')
//       console.log('onerror')
//     }
//     // 根据服务器推送的消息更新信息

//     // let wsTimer = setInterval(() => {
//     //   // console.log(this.ws)
//     //   if (!this.ws) {
//     //     httpUtil.info().then(res => {
//     //       if (!res.status) {
//     //         console.log('open')
//     //         clearInterval(wsTimer)
//     //         this.createInstance(res.data.groupName)
//     //       }

//     //     })
//     //   }
//     // }, 6000)

//     this.ws.onmessage = (e) => {
//       console.log(e.data)
//       const { type, data, message } = JSON.parse(e.data)
//       if (type === 1) {
//         store.dispatch(userpreapreActionCreators.getGroupList())
//       }

//       if (type === 2)
//         store.dispatch(userpreapreActionCreators.getGroupMemberList(this.param))

//       if (type === 3) {
//         store.dispatch(userpreapreActionCreators.changeGroupMemberList([]))
//         store.dispatch(userpreapreActionCreators.changeGroupMessage({}))
//         store.dispatch(userpreapreActionCreators.getGroupList(this.param))
//         store.dispatch(
//           CommonActionCreators.changeUserMessage({
//             userStatus: 3
//           })
//         )

//         // 更新 websocket 链接
//         ERPWebSocket.updateInstance('NoGroup')
//       }

//       // 房主踢人
//       // 如果是被踢的成员，退出房间
//       // 如果是其他成员，只需要更新成员列表即可
//       if (type === 4) {
//         if (message === store.getState().common.userMessage.userId) {
//           store.dispatch(userpreapreActionCreators.changeGroupMemberList([]))
//           store.dispatch(userpreapreActionCreators.changeGroupMessage({}))
//           store.dispatch(userpreapreActionCreators.getGroupList(this.param))
//           store.dispatch(
//             CommonActionCreators.changeUserMessage({
//               userStatus: 3
//             })
//           )
//         } else {
//           store.dispatch(
//             userpreapreActionCreators.getGroupMemberList(this.param)
//           )
//         }
//       }

//       if (type === 5) {
//         if (
//           data.groupCreatorId !== store.getState().common.userMessage.userId
//         ) {
//           httpUtil.modifyStatus().then(() => {
//             store.dispatch(userpreapreActionCreators.changeGroupMessage(data))
//             store.dispatch(
//               CommonActionCreators.changeUserMessage({ userStatus: 5 })
//             )
//           })
//         }
//       }
//       if (type === 6) {

//         httpUtil.getMembersAdInfo()
//           .then((res) => {
//             let sum = 0
//             res.data.data.forEach(item => {
//               sum = sum + item.curProcess * 1
//             });
//             console.log(sum)
//             if (sum === res.data.data.length * 4) {
//               PubSub.publish('7', 'finish')
//             } else {
//               store.dispatch(gameoperateActionCrrators.groupMemberList(res.data))
//             }
//           })
//       }
//       if (type === 7) {
//         PubSub.publish('7', 'finish')
//       }
//       if (type === 8) {
//         PubSub.publish('8', 'choose')
//       }


//     }
//   }
// }




// 单例模式，所有的页面共享一个 websocket 链接
// 所以只需要一个 websocket 链接即可
import PubSub from 'pubsub-js'

import * as userpreapreActionCreators from 'page/userprepare/store/actionCreators'
import * as gameoperateActionCreators from 'page/gameoperate/store/actionCreators'
import { actionCreators as CommonActionCreators } from 'store/common/index.js'
import { useHistory } from 'react-router-dom'

import httpUtil from 'utils/request'
import store from 'store/index'
import { WS } from 'config'


const WebSocketUrl = `${WS}/erpn/commonWebsocket?groupName=`

export default class ERPWebSocket {

  history = useHistory()
  // 关闭 websocket 链接
  static destoryInstance() {
    this.ws.close()
    this.ws = null
  }

  // 更新 websockt 链接
  static async updateInstance(param) {
    if (this.ws) {
      this.ws.close()
      this.createInstance(param)
    } else {
      this.createInstance(param)
    }

  }

  // 创建 websocket 链接
  static async createInstance(param) {
    this.ws = new WebSocket(WebSocketUrl + encodeURIComponent(param))
    this.description = 'erpwebsocket'
    this.param = param
    store.dispatch(userpreapreActionCreators.getGroupList())
    store.dispatch(userpreapreActionCreators.getGroupMemberList(this.param))

    // this.ws.onclose = e => {
    //   AntdMessage.info('websocket onclose')
    // }

    // this.ws.onerror = e => {
    //   AntdMessage.info('websocket onerror')
    // }
    // 根据服务器推送的消息更新信息

    this.ws.onmessage = (e) => {
      const { type, data, message } = JSON.parse(e.data)
      if (type === 1) {
        store.dispatch(userpreapreActionCreators.getGroupList())
      }

      if (type === 2)
        // 成员加入
        store.dispatch(userpreapreActionCreators.getGroupMemberList(this.param))

      if (type === 3) {
        // 解散分组
        httpUtil.modifyUserStatus().then(res => {
          if (!res.status) {
            store.dispatch(userpreapreActionCreators.changeGroupMemberList([]))
            store.dispatch(userpreapreActionCreators.changeGroupMessage({}))
            store.dispatch(userpreapreActionCreators.getGroupList(this.param))
            store.dispatch(
              CommonActionCreators.changeUserMessage({
                userStatus: 3
              })
            )
          }
        })

        ERPWebSocket.updateInstance('NoGroup')
        // this.history.push('/userprepare')

      }

      // 房主踢人
      // 如果是被踢的成员，退出房间
      // 如果是其他成员，只需要更新成员列表即可
      if (type === 4) {
        if (message === store.getState().common.userMessage.userId) {
          httpUtil.modifyUserStatus().then(res => {
            if (!res.status) {
              store.dispatch(userpreapreActionCreators.changeGroupMemberList([]))
              store.dispatch(userpreapreActionCreators.changeGroupMessage({}))
              store.dispatch(userpreapreActionCreators.getGroupList(this.param))
              store.dispatch(
                CommonActionCreators.changeUserMessage({
                  userStatus: 3
                })
              )
              ERPWebSocket.updateInstance('NoGroup')
            }
          })


        } else {
          store.dispatch(
            userpreapreActionCreators.getGroupMemberList(this.param)
          )
        }
      }

      if (type === 5) {
        if (
          data.groupCreatorId !== store.getState().common.userMessage.userId
        ) {
          httpUtil.modifyStatus().then(() => {
            store.dispatch(userpreapreActionCreators.changeGroupMessage(data))
            store.dispatch(
              CommonActionCreators.changeUserMessage({ userStatus: 5 })
            )
          })
        }
      }
      if (type === 6) {
        httpUtil.getMembersAdInfo()
          .then((res) => {
            let sum = 0
            res.data.data.forEach(item => {
              sum = sum + item.curProcess * 1
            });
            console.log(sum)
            if (sum === res.data.data.length * 4) {
              PubSub.publish('7', 'finish')
            } else {
              store.dispatch(gameoperateActionCreators.groupMemberList(res.data.data))
            }
          })
      }
      if (type === 7) {
        // 所有组员完成广告投放
        PubSub.publish('7', 'finish')
      }
      if (type === 8) {
        PubSub.publish('8', 'choose')
      }
    }
  }
}