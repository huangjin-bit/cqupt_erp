import React, { useEffect, useState } from 'react'
import { Link, withRouter, useHistory } from 'react-router-dom'
import { connect } from 'react-redux'
import { actionCreators as CommonActionCreators } from 'store/common/index.js'

import './index.scss'
import { Form, Input, Button, Radio, message,Popover } from 'antd'
import { UserOutlined, KeyOutlined } from '@ant-design/icons'
import LogoHeader from '../../components/logoHeader'
import CopyRight from '../../components/copyRight'
import DesItem from './component/desItem/index.js'
import imgUrl from 'assets/images/universe.jpg'
import httpUtil from 'utils/request/index'
import downloadApp from 'assets/images/download-app.png';


function Login(props) {
  const history = useHistory()

  // 切换登录身份：学生: 1, 老师: 2
  const [roleType, changeRoleType] = useState(1)
  let [isAdmin, setIsAdmin] = useState(false)

  /**
   * 每次登录之前会判断用户是否已经登录，如果已经登录
   * 直接跳转对应的页面，不需要重复登录
   */
  useEffect(() => {
    props.getBasic(props.history)
  }, [])

  // TODO 登陆时切换教师和学生状态
  function changeRole(e) {
    changeRoleType(e.target.value)
  }

  function loginSubmit(e) {
    if (isAdmin) {
      httpUtil.adminLogin({ userId: e.userId, password: e.password, serverCode: '1' })
        .then(res => {
          console.log(res)
          if (res.code == 10000) {
            props.adminLogin()
            message.success('登录成功')
            history.push('/superadmin/manageUser')
          }
          else {
            message.error('用户名或密码错误')
          }
        })
      console.log(1, e.userId, e.password, e.serverCode)
      // 超级管理员登录请求接口进行跳转
      // 
    }
    else {
      if (roleType === 1) {
        props.postLogin(e, props.history)
      }
      if (roleType === 2) {
        console.log(props);
        props.postTeacherLogin(e, props.history)
      }
    }

  }

  // 切换超级管理员登录
  const goAdmin = () => {
    setIsAdmin(true)
  }

  // 切换用户登录
  const goUser = () => {
    setIsAdmin(false)
  }

  return (
    <div className="login-page-wrapper">
      <LogoHeader title={isAdmin ? '管理员登录' : ""} />
      <div className={isAdmin ? 'middle adminMiddle' : 'middle'}>
        <div className="form-wrapper">
          <div className="left">
            <Form className='left-form' onFinish={loginSubmit}>
              <Form.Item
                name="userId"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input
                  size="large"
                  placeholder="请输入用户名"
                  prefix={<UserOutlined />}
                />
              </Form.Item>
              <Form.Item
                name="password"
                rules={[{ required: true, message: '请输入密码' }]}
              >
                <Input.Password
                  size="large"
                  placeholder="请输入密码"
                  prefix={<KeyOutlined />}
                  autoComplete="on"
                />
              </Form.Item>
              {
                isAdmin ? '' :
                  <Form.Item label="登录身份选择" valuePropName="checked">
                    <Radio.Group
                      name="type"
                      defaultValue={1}
                      onChange={changeRole}
                      value={roleType}
                    >
                      <Radio value={1}>学生</Radio>
                      <Radio value={2}>教师</Radio>
                    </Radio.Group>
                  </Form.Item>
              }
              <Form.Item>
                <Button
                  className="login-btn"
                  type="primary"
                  htmlType="submit"
                  block
                >
                  登录
                </Button>
              </Form.Item>
            </Form>
            {
              isAdmin ?
                <p className='adminLogin'><a onClick={goUser}> {`<<`}  返回主界面</a></p>
                :
                <p className='adminLogin'><a onClick={goAdmin}>管理员登录  {`>>`}</a></p>
            }
          </div>
          {
            isAdmin ? '' :
              <div className="right">
                <div className="img-wrapper">
                  <img src={imgUrl} alt="universe"></img>
                </div>
                <p className="right_p1">还没有ERP虚拟系统账号？</p>
                <br></br>
                <p className="right_p2">
                  立即<Link to="/register">去注册!!</Link>
                </p>
                <Popover content={<img style={{width: '200px'}} src={downloadApp} alt='下载APP' />} title="">
                  <p className='download'>ERP移动端下载</p>
                </Popover>
              </div>
          }
        </div>
      </div>
      <div className="footer">
        <DesItem></DesItem>
        <CopyRight></CopyRight>
      </div>
    </div>
  )
}

const mapDispatchToProps = {
  postLogin: CommonActionCreators.postLogin,
  postTeacherLogin: CommonActionCreators.postTeacherLogin,
  getBasic: CommonActionCreators.getBasic,
  adminLogin: CommonActionCreators.adminLogin
}

export default connect(null, mapDispatchToProps)(withRouter(Login))
