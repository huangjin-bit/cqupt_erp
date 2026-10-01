import React, { useCallback } from 'react'

import './index.scss'
import { Form, Input, Button, message } from 'antd'
import { connect } from 'react-redux'
import { Spin } from 'antd'
import { actionCreators } from 'store/common/index.js'

function ModifyForm(props) {
  let modifyUser = useCallback(
    e => {
      if (e.password !== e.confirmPassword) {
        message.error('确认密码不一致', 1)
        return
      }
      // 删除多余的信息
      delete e.confirmPassword
      props.postModifyUser(e)
    },
    [props]
  )

  const registerMessage = props.common.userMessage

  return (
    <>
      {registerMessage ? (
        <div className="modify-form-wrapper">
          <Form
            initialValues={registerMessage}
            labelCol={{ span: 7, offset: 0 }}
            className="form"
            onFinish={modifyUser}>
            <Form.Item label="用&ensp;户&ensp;名" name="userId">
              <Input value="123" disabled={true} />
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
              rules={[{ required: true, message: '请输入学号' }]}>
              <Input />
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
                确认修改
              </Button>
            </Form.Item>
          </Form>
        </div>
      ) : (
        <div className="card-wrapper">
          <Spin size="large" />
        </div>
      )}
    </>
  )
}

const mapStateToProps = state => ({
  common: state.common,
})
const mapDispatchToProps = {
  postModifyUser: actionCreators.postModifyUser,
}
export default connect(mapStateToProps, mapDispatchToProps)(ModifyForm)
