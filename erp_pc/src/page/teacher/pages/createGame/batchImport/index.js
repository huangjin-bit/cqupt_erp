import React, { Component, Fragment } from 'react'
import { Upload, Form, Input, Button, Table, Row, Col, Pagination, message } from 'antd'
import {
  UploadOutlined,
  ToolOutlined,
  SearchOutlined,
} from '@ant-design/icons'
import CreateModal from '../filterUser/components/createModal'

import styles from "./index.module.scss"
import httpUtil from 'utils/request'
import { downloadImportResultCsv } from 'utils/importResultCsv'
class BatchImport extends Component {
  constructor(props) {
    super(props)

    this.state = {
      //复选框数据
      selectedRowKeys: [],
      prevSelectedRowKeys: [],
      //搜索关键词
      keyword: '',
      status: 0,
      pageNumber: 1,
      pageSize: 9,
      //   limit: 10,
      total: 0,
      //表头
      columns: [
        {
          title: '用户ID',
          dataIndex: 'userId',
          key: 'userId',
        },
        {
          title: '用户名',
          dataIndex: 'userName',
          key: 'userName',
        },
        {
          title: '密码',
          dataIndex: 'password',
          key: 'password',
        },
        {
          title: '专业',
          dataIndex: 'major',
          key: 'major',
        },
        {
          title: '班级',
          dataIndex: 'sclass',
          key: 'sclass',
        },
        {
          title: '学号',
          dataIndex: 'sid',
          key: 'sid',
        },
        {
          title: '邮箱',
          dataIndex: 'email',
          key: 'email',
        },
        {
          title: '电话',
          dataIndex: 'tel',
          key: 'tel',
        },
      ],
      //表的数据
      data: [],
      listData:[],
      isModalVisible: false
    }
  }

  getModalRef = ref => {
    this.child = ref
  }
  showModal = () => {
    if (this.state.selectedRowKeys.length !== 0) {
      this.child.visibleModal(true)
    } else {
      message.warning("请先选择用户")
    }
  }

  //暂时只能删除没有开始比赛的用户
  onDelete = userId => {
    httpUtil
      .deletestu({ userId })
      .then(res => {
        if (res.message === '成功') {
          message.success(userId + ' 删除成功')
        } else {
          message.error(res.data + userId + ' 正在比赛')
        }
      }, this.loadData())
      .catch(err => console.log(err))
  }

  //批量删除暂时只能删除没有开始比赛的用户
  onDeleteSome = () => {
    const userIds = this.state.selectedRowKeys.join()
    if (userIds) {
      // httpUtil
      //   .deletestus({ userIds })
      //   .then((res) => {
      //     if (res.message === '成功') {
      //       message.success('批量删除成功')
      //     } else {
      //       message.error('有用户正在比赛，批量删除失败！')
      //     }
      //   }, this.loadData())
      //   .catch((err) => console.log(err))
    } else {
      const key = 'updatable'
      setTimeout(() => {
        message.warning({
          content: '请选择需要创建比赛的用户',
          key,
          duration: 2,
        })
      }, 200)
      return false
    }
  }

  //搜索
  onFinish = value => {
    const { selectedRowKeys, prevSelectedRowKeys } = this.state
    if (value.keyword === undefined || value.keyword === '') {
      this.setState({ 
        listData: this.state.data, 
        selectedRowKeys: [...prevSelectedRowKeys, ...selectedRowKeys],
        prevSelectedRowKeys: []
      })
    } else {
      const newData = this.state.data.filter(item => item.sid === value.keyword)
      const prevSelected = new Set(this.state.selectedRowKeys)
      for (const row of newData) {
        if (prevSelected.has(row.userId)) {
          prevSelected.delete(row.userId)
        }
      }
      this.setState({
        prevSelectedRowKeys: [...prevSelected],
        selectedRowKeys: newData.map(item => item.userId),
        listData: newData
      })
    }
  }
  componentDidMount() {
    this.loadData()
  }

  //获取列表数据
  loadData = () => {
    var formdata = new FormData()
    //可以通过append()方法来追加数据
    formdata.append('page', this.state.pageNumber)
    formdata.append('status', this.state.status)
    formdata.append('limit', this.state.pageSize)
  }

  //复选框
  onCheckebox = selectedRowKeys => {
    this.setState({ selectedRowKeys })
  }
  //当前页码
  onChangeCurrnePage = value => {
    console.log(this.state.pageNumber)
    console.log(value)

    this.setState(
      {
        pageNumber: value || 1,
      },
      () => {
        console.log(this.state.pageNumber)
        this.loadData()
      }
    )
  }
  /** 下拉页码 */
  onChangeSizePage = (current, page) => {
    console.log(current)
    this.setState(
      {
        pageNumber: 1,
        pageSize: page,
      },
      () => {
        this.loadData()
      }
    )
  }
  // 上传 CSV，展示可进入比赛的用户，并下载逐行导入结果。
  uploadTemplateForGame(teacherId, file) {
    const formdata = new FormData()
    formdata.append('csvfile', file)
    return httpUtil.importStuForGame(teacherId, formdata).then(res => {
      if (res.message === '失败') {
        message.error(`${file.name} 文件导入失败：${res.data || '请检查文件格式'}`)
        return
      }

      const results = res.data?.results || []
      const users = res.data?.users || []
      const successCount = results.filter(row => row.status === '成功').length
      const failedCount = results.length - successCount
      downloadImportResultCsv(
        results,
        `比赛用户导入结果_${new Date().toISOString().replace(/[:.]/g, '-')}.csv`
      )
      const selected = users.map(user => user.userId)
      this.setState({ data: users, listData: users, selectedRowKeys: selected })
      message.info(`${file.name} 导入完成：成功 ${successCount} 条，失败 ${failedCount} 条。结果 CSV 已下载。`)
    }).catch(err => {
      console.error('[BatchImport] CSV 导入失败', err)
      message.error('批量导入失败，请检查网络或服务器日志')
    })
  }
  // 下载创建比赛的csv模板
  downloadTemplate = () => {
    httpUtil
      .downloadTemplateForGame().then(res => res.blob())
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
      .catch(err => {
        message.error('下载失败')
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
          _this.uploadTemplateForGame(teacherId, info.file)
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
        <div className="search-box">
          <Form layout="inline" onFinish={this.onFinish.bind(this)}>
            <Form.Item name="keyword">
              <Input placeholder="请输入学号" />
            </Form.Item>
            <Form.Item shouldUpdate={true}>
              <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                搜索
              </Button>
            </Form.Item>
          </Form>
        </div>
        <div className="content-box">
          <Table
            pagination={false}
            className="table-wrap"
            rowSelection={rowSelection}
            size="small"
            rowKey="userId"
            columns={columns}
            dataSource={listData}
            bordered></Table>
          <Row className={styles["footer-btns"]}>
            <Col span={8}>
              <Button
                type='primary'
                onClick={() => this.showModal()}
                className={styles["pull-left"]}
                icon={<ToolOutlined />}>
                创建比赛
              </Button>
              <Button
                className={styles["pull-left"]}
                onClick={this.downloadTemplate}>
                下载模板
              </Button>
              <Upload {...props}>
                <Button icon={<UploadOutlined />}>批量导入</Button>
              </Upload>
            </Col>
            <Col span={16}>
              <Pagination
                className={styles["pull-right"]}
                onChange={this.onChangeCurrnePage}
                onShowSizeChange={this.onChangeSizePage}
                hideOnSinglePage={true}
                total={this.state.total}
                showSizeChanger={false}
                showQuickJumper
                defaultCurrent={1}
                showTotal={total => `共 ${total} 条`}
              />
            </Col>
          </Row>
        </div>
        <CreateModal onRef={this.getModalRef} value={selectedRowKeys} />
      </Fragment>
    )
  }
}

export default BatchImport

