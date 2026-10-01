import React, { Component, Fragment } from 'react'
import {
  Upload,
  Form,
  Input,
  Button,
  Table,
  message,
  Popconfirm,
  Select,
  Modal
} from 'antd'
import {
  UploadOutlined,
  CloseCircleOutlined,
  RedoOutlined
} from '@ant-design/icons'
import styles from './index.module.scss'
import { withRouter } from 'react-router-dom'
import { connect } from 'react-redux'
import { actionCreators } from '../../../store/index'
import { SearchOutlined } from '@ant-design/icons'
import httpUtil from 'utils/request'
import { downloadImportResultCsv } from 'utils/importResultCsv'

const { Option } = Select;

class UserList extends Component {
  constructor(props) {
    super(props)

    this.state = {
      //复选框数据
      selectedRowKeys: [],
      //搜索关键词
      keyword: '',
      // 分类搜索选项
      option: "userId",
      //
      status: 0,
      pageNumber: 1,
      pageSize: 9,
      //   limit: 10,
      total: '',
      //totalOne加载总数时的标志位，设置为1，
      totalOne: 1,
      csvfile: [],
      csvfileList: 11,
      adminId: [],
      data: [],
      listData: [],
      timer: '',
      resetModalVisible: false,
      resetConfirmText: '',
      //表头
      columns: [
        {
          title: '用户ID',
          dataIndex: 'userId',
          key: 'userId'
        },
        {
          title: '用户名',
          dataIndex: 'userName',
          key: 'userName'
        },
        {
          title: '密码',
          dataIndex: 'password',
          key: 'password'
        },
        {
          title: '专业',
          dataIndex: 'major',
          key: 'major'
        },
        {
          title: '班级',
          dataIndex: 'sclass',
          key: 'sclass'
        },
        {
          title: '学号',
          dataIndex: 'sid',
          key: 'sid'
        },
        {
          title: '邮箱',
          dataIndex: 'email',
          key: 'email'
        },
        {
          title: '电话',
          dataIndex: 'tel',
          key: 'tel'
        },
        {
          title: '操作',
          dataIndex: 'action',
          key: 'action',
          width: 270,
          render: (text, record, index) => {
            return (
              <div className={styles['inline-button']}>
                {/* <Button type="primary">编辑</Button> */}
                <Popconfirm
                  title="确认删除该用户吗？"
                  onConfirm={this.confirmDelete.bind(this, record.userId)}
                  onCancel={this.cancelDelete}
                  okText="确认"
                  cancelText="取消"
                >
                  <Button
                    danger
                    icon={<CloseCircleOutlined />}
                  >
                    删除
                  </Button>
                </Popconfirm>
                <Button
                  icon={<RedoOutlined />}
                  onClick={() => this.onResetPassword(record.userId)}>
                  重置密码
                </Button>
              </div>
            )
          }
        }
      ]
    }
  }

  confirmDelete = (userId) => {
    this.onDelete(userId)
  }

  cancelDelete = (e) => {
    console.log(e);
    message.error('Click on No');
  }

  confirmDeleteSome = () => {
    this.onDeleteSome()
  }

  cancelDeleteSome = (e) => {
    console.log(e);
    message.error('Click on No');
  }

  selectHandleChange = (value) => {
    console.log(`selected ${value}`);
  }

  //暂时只能删除没有开始比赛的用户
  onDelete = userId => {
    httpUtil
      .deletestu({ userId })
      .then(res => {
        if (res.message === '成功') {
          message.success(userId + ' 删除成功')
          this.loadData()
        } else {
          message.error(res.data + userId + ' 正在比赛')
        }
      })
      .catch(err => console.log(err))
  }

  //批量删除暂时只能删除没有开始比赛的用户
  onDeleteSome = () => {
    const userIds = this.state.selectedRowKeys.join()
    if (userIds) {
      httpUtil
        .deletestus({ userIds })
        .then(res => {
          if (res.message === '成功') {
            message.success('批量删除成功')
            this.setState({selectedRowKeys: []})
            this.loadData()
          } else {
            message.error('有用户正在比赛，批量删除失败！')
          }
        })
        .catch(err => console.log(err))
    } else {
      const key = 'updatable'
      setTimeout(() => {
        message.warning({
          content: '请选择需要删除的用户',
          key,
          duration: 2
        })
      }, 200)
      return false
    }
  }

  //重置密码
  onResetPassword = userId => {
    var formdata = new FormData()
    //可以通过append()方法来追加数据
    formdata.append('status', this.state.status)
    formdata.append('userId', userId)
    httpUtil
      .resetPassword(formdata)
      .then(res => {
        message.success('用户' + userId + '，重置密码为123456')
        this.loadData()
      })
      .catch(err => console.log(err))
  }

  //搜索
  onFinish = value => {
    console.log(value);
    const { keyword, option } = value
    if (keyword === undefined || keyword === '') {
      message.info('请输入内容查找')
      return false
    }
    const { data } = this.state
    const reg = new RegExp(`${keyword}`, 'g')
    let listData = []
    data.forEach((data, index) => {
      if (data[option] && data[option].search(reg) !== -1) {
        console.log(data);
        listData.push(data)
      }
    })
    if (listData.length === 0) {
      message.info(`内容: ${keyword}，没有找到`)
      this.setState({
        listData: listData,
        total: listData.data
      })
    } else {
      message.info(`内容: ${keyword}，找到了${listData.length}条相关数据`)
      this.setState({
        listData: listData,
        total: listData.data
      })
    }
  }
  componentDidMount() {
    this.loadData()
  }
  //搜索
  serachAll = input => {
    const keyword = input.target.value

    if (keyword === undefined || keyword === '') {
      this.setState({ listData: this.state.data })
    }
    const { data } = this.state
    const reg = new RegExp(`${keyword}`, 'g')
    let listData = []
    data.forEach((data, index) => {
      for (let item in data) {
        if (data[item] !== null) {
          if (data[item].search(reg) !== -1 && item !== "password") {
            listData.push(data)
            break
          }
        }
      }
    })
    if (listData.length === 0) {
      this.setState({
        listData: listData,
        total: listData.data
      })
    } else {
      this.setState({
        listData: listData,
        total: listData.data
      })
    }
  }

  //获取列表数据
  loadData = () => {
    httpUtil
      .findAllApprovedUser()
      .then(res => {
        const data = res.data
        this.setState({
          data: data,
          listData: data,
          total: data.length
        })
      })
  }

  //复选框
  onCheckebox = selectedRowKeys => {
    this.setState({ selectedRowKeys })
  }

  downloadManual = () => {
    httpUtil
      .downloadHelp()
      .then(res => res.blob())
      .then(res => {
        const blob = new Blob([res], {type: 'application/msword'})
        const link = document.createElement('a')
        if (link.download !== undefined) {
          const url = URL.createObjectURL(blob)
          link.setAttribute('href', url)
          link.setAttribute('download', '说明书')
          link.style.visibility = 'hidden'
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
        }
      })
      .catch(err => {})
  }

  downloadTemplate = () => {
    httpUtil
      .downloadTemplate().then(res => res.blob())
      .then(res => {
        const blob = new Blob([res], {type: 'text/csv;charset=utf-8;'})
        const link = document.createElement('a')
        if (link.download !== undefined) {
          const url = URL.createObjectURL(blob)
          link.setAttribute('href', url)
          link.setAttribute('download', '模板')
          link.style.visibility = 'hidden'
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
        }
      })
      .catch(err => {})
  }


  // 打开系统重置确认框
  openResetSystemModal = () => {
    this.setState({ resetModalVisible: true, resetConfirmText: '' })
  }

  // 取消系统重置
  cancelResetSystem = () => {
    this.setState({ resetModalVisible: false, resetConfirmText: '' })
  }

  // 执行系统重置：必须输入确认文字后才调用后端接口
  confirmResetSystem = () => {
    const confirmText = this.state.resetConfirmText.trim()
    if (confirmText !== '确认重置') {
      message.warning('请输入确认文字：确认重置')
      return
    }

    httpUtil.resetSystemData({ confirmText })
      .then(res => {
        if (res.message === '失败') {
          message.error(res.data || '系统重置失败')
          return
        }
        const rows = res.data?.deletedRows || {}
        const total = Object.values(rows).reduce((sum, count) => sum + Number(count || 0), 0)
        message.success(`系统重置完成，共清理 ${total} 条业务数据`)
        this.setState({ resetModalVisible: false, resetConfirmText: '' })
        this.loadData()
      })
      .catch(err => {
        console.error('[UserList] 系统重置失败', err)
        message.error('系统重置失败，请检查服务器日志')
      })
  }
  // 上传csv文件  // 上传 CSV，并下载逐行导入结果。
  uploadTemplate(teacherId, file) {
    const formdata = new FormData()
    formdata.append('csvfile', file)
    return httpUtil.importstu(teacherId, formdata).then(res => {
      if (res.message === '失败') {
        message.error(`${file.name} 文件导入失败：${res.data || '请检查文件格式'}`)
        return
      }

      const results = Array.isArray(res.data) ? res.data : []
      const successCount = results.filter(row => row.status === '成功').length
      const failedCount = results.length - successCount
      downloadImportResultCsv(
        results,
        `用户批量导入结果_${new Date().toISOString().replace(/[:.]/g, '-')}.csv`
      )
      message.info(`${file.name} 导入完成：成功 ${successCount} 条，失败 ${failedCount} 条。结果 CSV 已下载。`)
      this.loadData()
    }).catch(err => {
      console.error('[UserApproval] CSV 导入失败', err)
      message.error('批量导入失败，请检查网络或服务器日志')
    })
  }

  render() {
    const _this = this
    const props = {
      name: 'file',
      beforeUpload: () => false,
      onChange(info) {
        httpUtil.getCurTeacherInfo().then(res => {
          const teacherData = res.data
          const teacherId = teacherData?.adminId
          _this.uploadTemplate(teacherId, info.file)
        })
      }
    }
    const { columns, listData, selectedRowKeys } = this.state
    const rowSelection = {
      onChange: this.onCheckebox,
      selectedRowKeys
    }

    return (
      <Fragment>
        <div className='search-box'>
          <Form layout='inline' onFinish={this.onFinish.bind(this)}>
            <Form.Item name='keyword'>
              <Input
                placeholder='请输入内容'
                ref={ref => {
                  this.searchUserRef = ref
                }}
                onChange={this.serachAll}
              />
            </Form.Item>
            <Form.Item shouldUpdate={true} name="option" initialValue="userId">
              <Select defaultValue="userId" style={{ width: 120 }} onChange={this.selectHandleChange}>
                <Option value="userId">用户ID</Option>
                <Option value="userName">用户名</Option>
                <Option value="major">专业</Option>
                <Option value="sclass">班级</Option>
                <Option value="sid">学号</Option>
                <Option value="email">邮箱</Option>
                <Option value="tel">电话</Option>
              </Select>
            </Form.Item>
            <Form.Item shouldUpdate={true}>
              <Button
                type='primary'
                htmlType='submit'
                icon={<SearchOutlined />}>
                综合搜索
              </Button>
            </Form.Item>
          </Form>
        </div>
        <div className='content-box'>
          <Table
            pagination={{
              total: this.state.total,
              pageSizeOptions: [9, 40, 70, 100],
              defaultPageSize: 9
            }}
            className='table-wrap'
            rowSelection={rowSelection}
            size='small'
            rowKey='userId'
            columns={columns}
            dataSource={listData}
            bordered
          />
          <div className={[styles["footer-btns"]]}>
            <Popconfirm
              title="确认批量删除用户吗？"
              onConfirm={this.confirmDeleteSome.bind(this)}
              onCancel={this.cancelDelete}
              okText="确认"
              cancelText="取消"
            >
              <Button
                danger
                className={styles['pull-left']}
                icon={<CloseCircleOutlined />}>
                批量删除
              </Button>
            </Popconfirm>
            <Button
              danger
              className={styles['pull-left']}
              onClick={this.openResetSystemModal}>
              清空全部学生并重置系统
            </Button>            <Button className={styles['pull-left']} onClick={this.downloadManual}>下载说明书</Button>
            <Button className={styles['pull-left']} onClick={this.downloadTemplate}>下载模板</Button>
            <Upload {...props}>
              <Button icon={<UploadOutlined />}>批量导入用户</Button>
            </Upload>

          </div>
        </div>
          <Modal
            title="危险操作：清空全部学生并重置系统"
            visible={this.state.resetModalVisible}
            onOk={this.confirmResetSystem}
            onCancel={this.cancelResetSystem}
            okText="我已确认，立即重置"
            cancelText="取消"
            okButtonProps={{
              danger: true,
              disabled: this.state.resetConfirmText.trim() !== '确认重置'
            }}
          >
            <p>此操作会删除系统内所有学生，并清空全部比赛经营数据。</p>
            <p>将清理：所有学生、所有小组、财务、订单、广告、贷款、厂房和生产线数据。</p>
            <p>将保留：教师账号、管理员账号以及基础配置。</p>
            <p style={{ color: '#cf1322', fontWeight: 600 }}>注意：此操作不可恢复。</p>
            <Input
              placeholder='请输入“确认重置”'
              value={this.state.resetConfirmText}
              onChange={event => this.setState({ resetConfirmText: event.target.value })}
            />
          </Modal>      </Fragment>
    )
  }
}
const mapStateToProps = state => ({
  common: state.common
})

const mapDispatchToProps = {
  findAllApprovedUser: actionCreators.findAllApprovedUser
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(withRouter(UserList))


