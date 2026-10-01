/* 
  这里是封装的进度管理模块
*/
import React, { useState, useEffect } from 'react'
import { Button, Form, Input, Checkbox, Modal, message } from 'antd'
import httpUtil from "../../../../../../../utils/request/index"
export let showModal
let stateObj = null
export function SetGrade(props) {
  stateObj = { ...props.state }

  const [visible, setVisible] = useState(false);
  const [group, setGroup] = useState(null)
  const [form] = Form.useForm();

  showModal = (group) => {
    setVisible(true);
    setGroup(group)
  };
  const handleCancel = () => {
    setVisible(false);
  };
  // 发生请求来更新数据
  const updataPCData = async () => {
    const { curListTag, searchContent, setShowData } = stateObj
    // 发送请求渲染页面最新数据
    const formdata = new FormData()
    formdata.append("status", curListTag)
    formdata.append("vagueName", searchContent)
    console.log(searchContent);
    const res = await httpUtil.search(formdata)
    const retData = res.data
    // 将最新数据更新展示出来
    setShowData(retData)
  }
  const onFinish = (values) => {
    // 发送请求
    const formData = new FormData()
    formData.append("groupName", group.groupName)
    formData.append("highest", values.highest)
    formData.append("lowest", values.lowest)
    httpUtil.setGradeRange(formData).then((res) => {
      // 将数据传输
      if (res.status === 0) {
        message.success(res.data)
        updataPCData()
      } else {
        message.error(res.data)
      }
      // 重置输入框中的数据
      form.resetFields();
      setVisible(false);
    })
  };
  return (
    <div>
      <Modal
        title={`比赛名称：${group?.groupName}`}
        visible={visible}
        onCancel={handleCancel}
        footer={null}
      >
        <Form
          name="normal_login"
          className="login-form"
          onFinish={onFinish}
          form={form} 
        >
          <Form.Item
            label="最高分"
            name="highest"
            rules={[
              {
                required: true,
                message: '请输入最高分！',
              },
            ]}
          >
            <Input
            />
          </Form.Item>
          <Form.Item
            label="最低分"
            name="lowest"
            rules={[
              {
                required: true,
                message: '请输入最低分！',
              },
            ]}
          >
            <Input
            />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              className="login-form-button"
              style={{ display: "block", margin: "0 0 0 auto" }}
            >
              确认
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}