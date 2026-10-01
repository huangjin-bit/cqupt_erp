import React, { useCallback } from 'react'
import { Link } from 'react-router-dom'
import { withRouter } from 'react-router'
import { connect } from 'react-redux'
import { actionCreators } from 'store/common/index.js'

import './index.scss'
import { Form, Input, Button, message, Radio   } from 'antd'
import CopyRight from '../../components/copyRight'
import LogoHeader from '../../components/logoHeader'

function Register(props) {
  const registerSubmit = useCallback(
    e => {
      // 判断密码和确认密码是否一致
      if (e.password !== e.confirmPassword) {
        message.error('确认密码不一致', 1)
        return
      }
      // 删除多余信息
      delete e.confirmPassword
      e.state = 1
      e.gender = e.gender || '男'
      props.postRegister(e, props.history)
    },
    [props]
  )

  return (
    <div className="register-page-wrapper">
      <LogoHeader title="欢迎注册"></LogoHeader>
      <div className="middle">
        <div className="form-wrapper">
          <Form labelCol={{ span: 7, offset: 0 }} className="form" onFinish={registerSubmit}>
            <Form.Item
              label="用&ensp;户&ensp;名"
              name="userId"
              rules={[{ required: true, message: '请输入用户名' }]}>
              <Input />
            </Form.Item>
            <Form.Item
              label="真实姓名"
              name="userName"
              rules={[{ required: true, message: '请输入真实姓名' }]}>
              <Input />
            </Form.Item>
            <Form.Item
              label="学&emsp;&emsp;号"
              name="sid"
              rules={[{ required: true, message: '请输学号' }]}>
              <Input />
            </Form.Item>
            <Form.Item
              label="性&emsp;&emsp;别"
              name="gender">
              <Radio.Group  defaultValue={'男'}>
                <Radio value={'男'}>男</Radio>
                <Radio value={'女'}>女</Radio> 
              </Radio.Group>
            </Form.Item>
            <Form.Item label="专&emsp;&emsp;业" name="major">
              <Input />
            </Form.Item>
            <Form.Item label="班&emsp;&emsp;级" name="sclass">
              <Input />
            </Form.Item>
            <Form.Item label="电子邮件" name="email">
              <Input />
            </Form.Item>
            <Form.Item label="联系方式" name="tel">
              <Input />
            </Form.Item>
            <Form.Item
              label="密&emsp;&emsp;码"
              name="password"
              rules={[{ required: true, message: '请输入密码' }]}>
              <Input.Password size="large" />
            </Form.Item>
            <Form.Item
              label="确认密码"
              name="confirmPassword"
              rules={[{ required: true, message: '请输入确认密码' }]}>
              <Input.Password size="large" />
            </Form.Item>
            <Form.Item className="btn-wrapper">
              <Button className="login-btn" type="primary" htmlType="submit">
                <Link to="/">返回</Link>
              </Button>
              <Button className="login-btn" type="primary" htmlType="submit">
                注册
              </Button>
            </Form.Item>
          </Form>
        </div>
      </div>
      <div className="footer">
        <CopyRight></CopyRight>
      </div>
    </div>
  )
}

const mapDispatchToProps = {
  postRegister: actionCreators.postRegister,
}
export default connect(null, mapDispatchToProps)(withRouter(Register))
