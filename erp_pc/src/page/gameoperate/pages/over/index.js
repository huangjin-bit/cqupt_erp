import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import { Result, Button, message } from 'antd'
import { SmileOutlined } from '@ant-design/icons'
import httpUtil from 'utils/request'
import { getBasic } from '../../../../store/common/actionCreators'

import './index.scss'


function Over(props) {
  const overGame = () => {
    httpUtil.overGame()
      .then(res => {
        console.log(res);
        if (!res.status) {
          //成功
          alert("结束经营成功")
          props.getBasic()
          console.log("成功")
        }
      })
  }

  const restartGame = () => {
    httpUtil.restartGame().then(res => {
      console.log(res)
      if (!res.status) {
        //成功
        alert("重新开始成功，请退出重新登录")
        console.log("成功")
      }
    })
  }
  return (
    <div className="userprepare-wrapper">
      <Button type="primary" danger onClick={overGame}>
        结束经营的按钮
      </Button>
      <br />
      <br />
      <Button type="primary" danger onClick={restartGame}>
        重新开始的按钮
      </Button>
    </div>
  )
}

const mapStateToProps = state => ({
  common: state.common,
})
const mapDispatchToProps = {
  getBasic: getBasic,
}

export default connect(mapStateToProps, mapDispatchToProps)(withRouter(Over))
