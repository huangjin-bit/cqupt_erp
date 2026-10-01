import React, { Component } from 'react'
import { message, Modal, Form, Input, Button, Checkbox } from 'antd'
import httpUtil from 'utils/request'
import { actionCreators } from '../../../../store/index'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import { getUTF8ByteLength } from 'utils'

class CreateModal extends Component {
  constructor(props) {
    super(props)

    this.state = {
      adminId: '',
      isModalVisible: false,
      layout: {
        labelCol: { span: 8 },
        wrapperCol: { span: 16 },
      },
      tailLayout: {
        wrapperCol: { offset: 8, span: 16 },
      },
    }
  }

  componentDidMount() {
    this.props.onRef(this)
    httpUtil
      .getCurTeacherInfo()
      .then(res => {
        const adminId = res.data.adminId
        this.setState({
          adminId,
        })
      })
      .catch(err => console.log(err))
  }

  onFormRef = ref => {
    this.child = ref
  }

  //弹窗
  visibleModal = status => {
    this.setState({
      isModalVisible: status,
    })
    if (status === true) {
      this.props.onRef(this)
    }
  }

  handleCancel = () => {
    this.visibleModal(false)
  }

  //提交表单
  onFinish = value => {
    const formdata = new FormData()
    formdata.append('groupName', value.groupName)
    formdata.append('userNumber', this.props.value.length)
    formdata.append('teacherId', this.state.adminId)
    formdata.append('userId', this.props.value.join())

    //创建比赛
    httpUtil.selectStuStart(formdata).then(res => {
      if (res.status === 0) {
        message.success(res.message)
        this.handleCancel()
      } else if (res.status === 1) {
        message.warning(res.message)
      } else if (res.status === 2) {
        message.warning(`玩家<${res.data.replace(/\[|\]/g, '')}>正在游戏`)
      } else if(res.status === 5) {
        // 登录过期
        message.warning(res.message)
      }
      //加入小组
    })
  }

  render() {
    return (
      <Modal
        title="新增用户"
        visible={this.state.isModalVisible}
        onOk={this.handleOk}
        onCancel={this.handleCancel}
        footer={null}>
        <Form
          // {...layout}
          name="basic"
          true
          initialValues={{ remember: true }}
          onFinish={this.onFinish}
          onFinishFailed={null}>
          <Form.Item label=" *  比赛人数" name="userNumber" >
            {this.props.value.length}
          </Form.Item>

          <Form.Item
            label="小组名称"
            name="groupName"
            rules={[
              { required: true, message: '请输入小组名称' },
              {
                validator(_,v, cb) {
                  if(getUTF8ByteLength(v) > 20) {
                    return cb('比赛名称超过最大长度限制')
                  }
                  return cb()
                }
              }]}>
            <Input />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              开始比赛
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    )
  }
}
const mapDispatchToProps = {
  postCreateGroup: actionCreators.postCreateGroup,
}
const mapStateToProps = state => ({
  common: state.common,
})
export default connect(mapStateToProps, mapDispatchToProps)(withRouter(CreateModal))
