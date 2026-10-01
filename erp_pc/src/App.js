import React, { useEffect } from 'react'
import { HashRouter as Router, Route, Switch, Redirect } from 'react-router-dom'
import { actionCreators as CommonActionCreators } from 'store/common/index.js'
import Unauth from 'unauth'
import routes from 'routes'
import AuthRouter from 'components/authRouter/index.js'
import NotFound from 'components/NotFound'
import { message } from 'antd'
// 配置提示
message.config({
  top: 50,
  maxCount: 1,
});

function App() {
  return (
    <Router>
      <Switch>
        {routes.map(item => {
          return <AuthRouter key={item.path} {...item}></AuthRouter>
        })}
        <Route exact path='/'>
          {/* 重定向 */}
          <Redirect to='/login' />
        </Route>
        <Route exact path='*'>
          <NotFound />
        </Route>
      </Switch>
    </Router>
  )
}
export default App
