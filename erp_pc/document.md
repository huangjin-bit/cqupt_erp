## ERP 简介

该项目是学院专业课的 ERP 教学系统，主要的功能是模拟企业的经营活动。
该项目使用 React + React-Router 完成页面的开发。 数据层使用 Redux，前后端交互使用 axios，并使用 redux-thunk 管理副作用。
React 组件几乎全部使用 hook 开发，使用 useCallback 和 useMemo 来避免重复的渲染。
路由权限控制使用 React 动态路由的思想，使用 HOC 包裹 Router 组件，判断用户的权限，如果没有权限，就不渲染路由。
使用 websocket 进行服务端推送，场景主要有：小型聊天室、用户加入房间的时候要向其他成员广播房间人员的变化，选择订单的时候要向所有人广播订单的变化。
websocket 建立之后就不会断开，我们使用 type 来区别 websocket 消息的类型。
因为所有页面共享一个 websocket 链接，所以我自定义了一个 websocket 类，使用单例模式。

项目难点为，数据比较复杂，而且更新比较频繁，多个页面会共用数据，websocket 经常会更新数据。
我的处理是，model 和 view 层完全分离，把所有的数据全部放在 redux 中，react 组件只负责展示数据和 dispatch action，react 组件本身不存放数据。
只要有数据更新，所有的订阅了 redux 页面都能即使更新 view，管理数据很方便。

## 前端页面逻辑

### login

登录页面

### register

注册页面

### userprepare

用户准备页面

### useroperate

游戏开始之后的用户操作页面

## 前端 Redux 数据结构

```js
{
  // 主要用于存放公共的数据，包括用户的身份信息
  commom: {
   userMessage: {
    userId: '',
    password: '',
   },
   userStatus: 1,
   role: ''
  }

  // 主要用于存放用户准备页面的信息，包括分组信息，分组成员信息
  userprepare: {
   groupMessage: {},
   groupMemberList: []
  }
}
```

## 路由管理
