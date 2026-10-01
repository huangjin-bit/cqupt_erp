import React from 'react'
import { actionCreators } from '../../store/index.js'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'

import './index.scss'
import { Form, Input, Button, InputNumber, Select } from 'antd'
import httpUtil from 'utils/request/index'
import { getUTF8ByteLength } from 'utils/index.js'

class createGame extends React.Component {
  state = {
    teacherList: [],
  }

  componentDidMount() {
    httpUtil.getTeacherList().then(res => {
      this.setState({ teacherList: res.data })
    })
  }

  createGameSubmit = e => {
    this.props.postCreateGroup(this.props.common.userMessage.userId, e, this.props.history)
  }
  render() {
    return (
      <div className="create-game-wrapper">
        <Form labelCol={{ span: 7, offset: 0 }} className="form" onFinish={this.createGameSubmit}>
          <Form.Item
            label="比赛名称"
            rules={[
              { required: true, message: '比赛名称' },
              {
                validator(_,v, cb) {
                  if(getUTF8ByteLength(v) > 20) {
                    return cb('比赛名称超过最大长度限制')
                  }
                  return cb()
                }
              }
            ]}
            name="groupName">
            <Input />
          </Form.Item>
          <Form.Item
            label="企业数量"
            rules={[{ required: true, message: '企业数量' }]}
            name="maxEnterpriseNumber">
            <InputNumber min={1} max={50} precision={0} size="middle" />
          </Form.Item>
          <Form.Item
            label="选择老师"
            rules={[{ required: true, message: '选择老师' }]}
            name="teacherId">
            <Select>
              {this.state.teacherList.map(item => (
                <Select.Option value={item.adminId} key={item.adminId}>
                  {item.adminName}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item>
            <Button className="login-btn" type="primary" htmlType="submit">
              创建比赛
            </Button>
          </Form.Item>
        </Form>
      </div>
    )
  }
}

const mapDispatchToProps = {
  postCreateGroup: actionCreators.postCreateGroup,
}
const mapStateToProps = state => ({
  common: state.common,
})
export default connect(mapStateToProps, mapDispatchToProps)(withRouter(createGame))
