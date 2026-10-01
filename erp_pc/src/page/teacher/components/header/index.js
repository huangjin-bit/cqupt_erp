import React, { Component, Fragment } from 'react'
import { connect } from 'react-redux'
import { Route, Redirect, Link, withRouter } from 'react-router-dom'
import { actionCreators as CommomActionCreators } from 'store/common/index.js'
import { Modal, Button, message, Popover } from 'antd'
import styles from './index.module.scss'
import {
  ReloadOutlined,
  UsergroupDeleteOutlined,
  FormOutlined,
  CopyOutlined,
  PoweroffOutlined,
  DownloadOutlined,
} from '@ant-design/icons'

import logoUrl from 'assets/images/startGame_logo.jpg'
import headUrl from 'assets/images/pic_meb2.jpg'
import httpUtil from 'utils/request'

class Header extends Component {
  constructor(props) {
    console.log(props.common);
    super(props)
    console.log(props);
    this.state = {
      visible: false,
      teacherData: '',
      teacherName: '',
      teacherId: '',
    }
  }

  componentDidMount() {
    httpUtil.getCurTeacherInfo().then(res => {
      const teacherData = res.data
      const teacherName = teacherData ? teacherData.adminName : 'xxx'
      const teacherId = teacherData?.adminId
      this.setState({
        teacherData,
        teacherName,
        teacherId,
      })
    })

    console.log(sessionStorage.valueOf())
    console.log(sessionStorage.getItem('teacherPo'))

    console.log(window.sessionStorage.getItem('teacherPo'))

    // 遍历session
    // for (var i = 0; i < sessionStorage.length; i++) {
    //   var key = storage.key(i)
    //   var value = storage.getItem(key)
    //   console.log(key + '=' + value)
    // }
  }

  logout = e => {
    this.setState({
      visible: true,
    })
  }

  handleOk = e => {
    this.setState({
      visible: false,
    })
    this.props.getTeacherLogout()
    this.props.history.replace('/login')
  }

  handleCancel = e => {
    this.setState({
      visible: false,
    })
  }

  // 页面刷新
    reload = () => {
        window.location.reload()
    }

  //   //退出登录
  //   onTeacherLogout = () => {
  //     httpUtil
  //       .teacherLogout()
  //       .then((res) => {
  //         const listData = res.data
  //         // message.info(this.state.teacherId + '老师,' + listData + '!')
  //         const key = 'updatable'
  //         message.loading({ content: 'Loading...', key })
  //         setTimeout(() => {
  //           message.info({
  //             content: this.state.teacherId + '老师,' + listData + '!',
  //             key,
  //             duration: 2,
  //           })
  //         }, 200)
  //       })
  //       .catch((err) => console.log(err))
  //   }

  render() {
    const { teacherName, teacherId } = this.state

    return (
      <Fragment>
        <div className={styles["game-operate-header-wrapper"]}>
          <img src={logoUrl} />
          <div className={styles["content-wrapper"]}>
            <div className={styles["button-wrapper"]}>
              <Popover placement="topLeft" content={'点击刷新'} trigger="hover">
                <Button type="primary" icon={<ReloadOutlined />} size="large" onClick={this.reload} />
              </Popover>
              {/* <Popover placement="topLeft" content={'职务设置'} trigger="hover">
              <Button
                type="primary"
                icon={<UsergroupDeleteOutlined />}
                size="large"
              />
            </Popover>

            <Popover
              placement="topLeft"
              content={'预览实验报告'}
              trigger="hover"
            >
              <Button type="primary" icon={<CopyOutlined />} size="large" />
            </Popover>
            <Popover
              placement="topLeft"
              content={'下载实验报告'}
              trigger="hover"
            >
              <Button type="primary" icon={<DownloadOutlined />} size="large" />
            </Popover>
            <Popover
              placement="topLeft"
              content={'编写心得体会'}
              trigger="hover"
            >
              <Button type="primary" icon={<FormOutlined />} size="large" />
            </Popover> */}

              <Popover placement="topLeft" content={'点击退出登录'} trigger="hover">
                {/* <Link to="/login">
                  <Button
                    // onClick={this.onTeacherLogout}
                    type="primary"
                    icon={<PoweroffOutlined />}
                    size="large"
                  />
                </Link> */}
                <Modal
                  title="警告"
                  visible={this.state.visible}
                  onOk={this.handleOk}
                  onCancel={this.handleCancel}>
                  <p>您确定要退出登录吗？</p>
                </Modal>
                <Button
                  onClick={this.logout}
                  type="primary"
                  icon={<PoweroffOutlined />}
                  size="large"
                />
              </Popover>
            </div>
            <div className={styles["user-wrapper"]}>
              <Popover
                placement="topLeft"
                content={
                  <div>
                    <p>教师ID：{`${teacherName}`}</p>
                    <p>教师用户名：{`${teacherId}`}</p>
                  </div>
                }
                title="教师信息"
                trigger="hover">
                <img src={headUrl}></img>
              </Popover>
              <div className={styles["message-wrapper"]}>
                <div className={styles["message"]}>您好，{`${teacherName}`} 老师</div>
              </div>
            </div>
          </div>
        </div>
      </Fragment>
    )
  }
}
const mapDispatchToProps = {
  getTeacherLogout: CommomActionCreators.getTeacherLogout,
}
const mapStateToProps = state => ({
  common: state.common,
})
export default connect(mapStateToProps, mapDispatchToProps)(withRouter(Header))
