import React from 'react'
import localStorage from "localStorage"
import { withRouter } from 'react-router-dom'
import { actionCreators as CommomActionCreators } from 'store/common/index.js'
import { connect } from 'react-redux'

import './index.scss'
import { Modal } from 'antd'
class Logout extends React.Component {

  componentWillUnmount() {
    localStorage.removeItem('userUnique')
  }

  state = { visible: false }
  logout = e => {
    this.setState({
      visible: true,
    })
  }

  handleOk = e => {
    this.setState({
      visible: false,
    })
    localStorage.clear()
    this.props.getLogout()
    this.props.history.push('/login')
  }

  handleCancel = e => {
    this.setState({
      visible: false,
    })
  }

  render() {
    return (
      <div className="logout-wrapper">
        <Modal
          title="警告"
          visible={this.state.visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}>
          <p>您确定要退出登录吗？</p>
        </Modal>
        <div onClick={this.logout} className="content">
          {this.props.children}
        </div>
      </div>
    )
  }
}

const mapDispatchToProps = {
  getLogout: CommomActionCreators.getLogout,
}
export default connect(null, mapDispatchToProps)(withRouter(Logout))
