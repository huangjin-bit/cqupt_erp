import React, { Component, Fragment } from 'react'
import { connect } from 'react-redux'
import { Redirect, Link, withRouter } from 'react-router-dom'
import './index.scss'
import { Button, Popover, message } from 'antd'
import { ReloadOutlined } from '@ant-design/icons'
import { UsergroupDeleteOutlined } from '@ant-design/icons'
import { FormOutlined } from '@ant-design/icons'
import { CopyOutlined } from '@ant-design/icons'
import { PoweroffOutlined } from '@ant-design/icons'
import { DownloadOutlined } from '@ant-design/icons'
import { QuestionCircleOutlined } from '@ant-design/icons'
import logoUrl from 'assets/images/startGame_logo.jpg'
import headUrl from 'assets/images/pic_meb2.jpg'
import httpUtil from 'utils/request'


const manualFileName = 'ERP模拟运营用户手册2.pdf'
const manualUrl = (process.env.PUBLIC_URL || '.') + '/docs/' + encodeURIComponent(manualFileName)

class Header extends Component {
  constructor(props) {
    super(props)
  }
  state = { userName: 'xxx', years: 'x', currentPeriod: 'x', groupName: 'x', tax: 'x', cash: 'x', factoryCount: 'x' }
  //   GameMessage
  componentDidMount() {
    this.fetchInfo()
    window.addEventListener('info', this.fetchInfo)
  }

  fetchInfo = (() => {
    httpUtil.info().then(res => {
      this.setState(res.data)
      localStorage.setItem('userUnique', res.data.userUnique)
      localStorage.setItem(
        'currentTime',
        `第${res.data.years}年第${res.data.currentPeriod}周期`
      )
    })
  }).bind(this)

  componentWillUnmount() {
    window.removeEventListener('info', this.fetchInfo)
    localStorage.clear()
  }

  // 退出登录
  onUserLogout = () => {
    // localStorage.clear()
    httpUtil
      .logout()
      .then(res => {
        if (!res.status)
          localStorage.clear()


        const listData = res.data
        const key = 'updatable'
        message.loading({ content: 'Loading...', key })
        setTimeout(() => {
          message.info({
            content: listData + '!',
            key,
            duration: 2
          }

          )

        }, 200)
      }, <Redirect to='/login' />)
      .catch((err) => console.log(err))
  }

  // 页面刷新
  reload = () => {
    window.location.reload()
  }

  // 下载实验报告
  downloadReport = () => {
    console.log(window.location.host)
    httpUtil.downloadDoc().then(res => {
      console.log(res,".............")
    //   if (!res.status) {
    //     // 地址
    //     const eleLink = document.createElement('a');
    //     eleLink.style.display = 'none';
    //     eleLink.target = "_blank"

    //     // 动态地址
    //     eleLink.href=`${SERVER}/erpn/${res.data.path}`
    //     // eleLink.href = `http://172.22.7.210:8080/erpn/` + res.data.path;
    //     document.body.appendChild(eleLink);
    //     eleLink.click();
    //     document.body.removeChild(eleLink);
    //   }
    //   else if (res.status && res.data != null) message.info(res.data.message)
    }
    )
  }


  render() {
    return (
      <Fragment>
        <div className="game-operate-header-wrapper">
          <img src={logoUrl} />
          <div className="content-wrapper">
            <div className="button-wrapper">
              <Popover placement="topLeft" content={'点击刷新'} trigger="hover">
                <Button type="primary" onClick={this.reload} icon={<ReloadOutlined />} size="large" />
              </Popover>
              <Popover placement="topLeft" content={'职务设置'} trigger="hover">
                <Link to='/gameoperate/memberset'>
                  <Button type="primary" icon={<UsergroupDeleteOutlined />} size="large" />
                </Link>
              </Popover>

              <Popover placement="topLeft" content={'预览实验报告'} trigger="hover">
                <Link to='/gameoperate/viewreport'>
                  <Button type="primary" icon={<CopyOutlined />} size="large" />
                </Link>
              </Popover>
              <Popover placement="topLeft" content={'下载实验报告'} trigger="hover">
                <Button type="primary" onClick={this.downloadReport} icon={<DownloadOutlined />} size="large" />
              </Popover>
              <Popover placement="topLeft" content={'编写心得体会'} trigger="hover">
                <Link to='/gameoperate/fillreport'>
                  <Button type="primary" icon={<FormOutlined />} size="large" />
                </Link>
              </Popover>
              <Popover placement="topLeft" content={'下载比赛规则'} trigger="hover">
                <a href={manualUrl} download={manualFileName}>
                  <Button type="primary" icon={<QuestionCircleOutlined />} size="large" />
                </a>
              </Popover>

              <Popover placement="topLeft" content={'退出登录'} trigger="hover">
                <Link to="/login">
                  <Button
                    onClick={this.onUserLogout}
                    type="primary"
                    icon={<PoweroffOutlined />}
                    size="large"
                  />
                </Link>
              </Popover>
            </div>
            <div className="user-wrapper">
              <Popover
                placement="topLeft"
                content={
                  <div>
                    <p>分组：{`${this.state.groupName}`}</p>
                    <p>现金：{`${this.state.cash}`}万元</p>
                    <p>税金：{`${Math.floor(this.state.tax * 100) / 100}`}万元</p>
                    <p>厂房：{`${this.state.factoryCount}`}个</p>
                  </div>
                }
                title="比赛信息"
                trigger="hover">
                <img src={headUrl}></img>
              </Popover>
              <div className="message-wrapper">
                <div className="message">您好， {`${this.state.userName}`}</div>
                <div className="message">第{`${this.state.years}`}年，第{`${this.state.currentPeriod}`}期</div>
              </div>
            </div>
          </div>
        </div>
      </Fragment>
    )
  }
}

const mapStateToProps = state => ({
  common: state.common
})
export default connect(mapStateToProps, null)(withRouter(Header))

