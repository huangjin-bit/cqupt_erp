import React from 'react'
import ReactDOM from 'react-dom'
import store from 'store/index'
import App from './App.js'
import { Provider } from 'react-redux'

import 'common/styles/reset.css'
import 'antd/dist/antd.css'

ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
)
