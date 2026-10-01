import React, { Suspense } from 'react'
import { Route, Redirect } from 'react-router-dom'
import { connect } from 'react-redux'
import { Spin } from 'antd'
import './index.css'

/**
 * 鉴权路由组件
 * 如果身份验证不通过，则跳转到登录页
 */
function AuthRouter(props) {
  if (props.auth.includes(props.common.role)) {
  const { auth, path } = props
  const ChildComponent = props.router
  return (
    <Route auth={auth} path={path}>
      <Suspense fallback={<Spin tip="加载中..." className="childComponent-wrapper-spin" />}>
        <ChildComponent></ChildComponent>
      </Suspense>
    </Route>
  )

  }
  else {
    return <Redirect to="/login" />
  }
}

const mapStateToProps = state => ({
  common: state.common,
})
export default connect(mapStateToProps, null)(AuthRouter)