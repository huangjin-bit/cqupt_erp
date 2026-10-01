import React, {Component, Fragment} from 'react'
import {Form, Input, Button, Table, message} from 'antd'
import {SearchOutlined, ToolOutlined} from '@ant-design/icons'
import CreateModal from './components/createModal'
import styles from './index.module.scss'
import httpUtil from 'utils/request'
// 引入样式文件
class FilterUser extends Component {
  constructor(props) {
    super(props)

    this.state = {
      isModalVisible: false,

      //复选框数据
      selectedRowKeys: [],
      //保存搜索前已选的key值
      prevSelectedRowKeys: [],
      //搜索关键词
      keyword: '',
      //
      status: 0,
      //   limit: 10,
      total: '',
      //totalOne加载总数时的标志位，设置为1，
      totalOne: 1,
      csvfile: [],
      csvfileList: 11,
      adminId: [],
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
        }
      ],
      // 表的数据
      data: [],
      // 搜索表数据
      listData: [],
    }
  }

  //传统导入文件方法
  //   //csv文件导入
  //   onImportstu = () => {
  //     httpUtil.getTeacherList().then((res) => {
  //       const adminId = res.data[0].adminId
  //       var data = document.getElementById('file').files[0]
  //       console.log(data)
  //       var formdata = new FormData()
  //       //可以通过append()方法来追加数据
  //       formdata.append('csvfile', data)
  //       formdata.append('columns', this.state.csvfileList)
  //       formdata.append('adminId', adminId)
  //       httpUtil
  //         .importstu(formdata)
  //         .then((res) => {
  //           if (res.message === '失败') {
  //             message.warning('导入失败，请不要传入重复数据')
  //           }
  //           if (res.message === '成功') {
  //             message.success('导入成功')
  //           }
  //         }, this.loadData())
  //         .catch((err) => console.log(err))
  //     })
  //   }

  //获取弹窗组件实例
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

  componentDidMount() {
    this.loadData()
  }

  //获取列表数据
  loadData = () => {
    httpUtil.findAllApprovedUser().then(res => {
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

  //搜索
  serachAll = value => {
    const data = this.state.data
    const listData = []
    data.forEach((item, index) => {
      if (
        item.sid.search(value) == 0 ||
        item.userName.search(value) !== -1 ||
        item.userName.search(value.toLowerCase()) !== -1 ||
        item.userName.search(value.toUpperCase()) !== -1
      ) {
        listData.push(item)
      }
    })
    if (listData.length === 0) {
      message.info(`内容: ${value}，没有找到`)
      this.setState({
        listData: listData,
        total: listData.length
      })
    } else {
      if (value !== '') {
        message.info(`内容: ${value}，找到了${listData.length}条相关数据`)
      }
      this.setState({
        listData: listData,
        total: listData.length
      })
    }
  }

  handleKeywordChange(value) {
    if(value === '') {
      const {data, prevSelectedRowKeys, selectedRowKeys} = this.state
      // 当使用全部数据时，将prevSelectedRowKeys中的数据恢复到selectedRowKeys中
      const selected = [...prevSelectedRowKeys, ...selectedRowKeys]
      this.setState({
        listData: data,
        keyword: value,
        total: data.length,
        selectedRowKeys: selected,
        prevSelectedRowKeys: []
      })
    } else {
      this.setState({ keyword: value })
    }
  }

  handleDataChange(newData) {
    // 当通过搜索导致数据变化时，将当前所有已选的数据保存到 prevSelectedRowKeys中
    // 因为 prevSelectedRowKeys 中的数据不应包含当前搜索结果里的数据，所以将重复的数据从prevSelectedRowKeys中删除
    const prevSelected = new Set(this.state.selectedRowKeys)
    for (const row of newData) {
      if (prevSelected.has(row.userId)) {
        prevSelected.delete(row.userId)
      }
    }
    this.setState({prevSelectedRowKeys: [...prevSelected]})
  }

  // 搜索用户
  searchUser = (e) => {
    const value = this.state.keyword
    if (value === undefined || value === '') {
      message.info('请先输入用户名')
      return false
    }
    const data = this.state.data
    const listData = data.filter((item, index) => {
      if (item.userName.search(value) !== -1) {
        return item
      }
    })
    this.handleDataChange(listData)
    if (listData.length === 0) {
      message.info(`用户名: ${value}，没有找到 ！`)
      this.setState({
        listData: listData,
        total: listData.length,
      })
    } else {
      message.info(`用户名: ${value}，找到了${listData.length}条相关数据`)
      this.setState({
        listData: listData,
        total: listData.length,
      })
    }
  }
  // 搜索用户学号
  searchSid = (e) => {
    const value = this.state.keyword
    if (value === undefined || value === '') {
      message.info('请先输入学号')
      return false
    }
    const data = this.state.data
    const listData = data.filter((item, index) => {
      if (item.sid.search(value) == 0) {
        return item
      }
    })
    this.handleDataChange(listData)
    if (listData.length === 0) {
      message.info(`学号: ${value}，没有找到 ！`)
      this.setState({
        listData: listData,
        total: listData.length
      })
    } else {
      message.info(`学号: ${value}，找到了${listData.length}条相关数据`)
      this.setState({
        listData: listData,
        total: listData.length
      })
    }
  }

  render() {
    const {columns, listData, selectedRowKeys, keyword, prevSelectedRowKeys} =
      this.state
    const selected = [...selectedRowKeys, ...prevSelectedRowKeys]
    const rowSelection = {
      onChange: this.onCheckebox,
      selectedRowKeys: selected
    }

    return (
      <Fragment>
        <div className="search-box">
          <Form layout="inline">
            <Form.Item name="keyword">
              <Input
                placeholder="请输入内容"
                value={keyword}
                onChange={e => this.handleKeywordChange(e.target.value)}
              />
            </Form.Item>
            <Form.Item shouldUpdate={true}>
              <Button type="primary" onClick={this.searchUser} icon={<SearchOutlined />} >
                搜索用户
              </Button>
            </Form.Item>
            <Form.Item shouldUpdate={true}>
              <Button type="primary" onClick={this.searchSid} icon={<SearchOutlined />}>
                搜索学号
              </Button>
            </Form.Item>
          </Form>
        </div>
        <div className="content-box">
          <Fragment>
            <Table
              pagination={{
                total: this.state.total,
                pageSizeOptions: [11, 40, 70, 100],
                defaultPageSize: 11
              }}
              className={styles["table-wrap"]}
              rowSelection={rowSelection}
              size="small"
              rowKey="userId"
              columns={columns}
              dataSource={listData}
              bordered
            />
            <Button
              type="primary"
              onClick={() => this.showModal()}
              className={styles["pull-left"]}
              icon={<ToolOutlined />}>
              创建比赛
            </Button>
          </Fragment>
        </div>
        <CreateModal onRef={this.getModalRef} value={[...selectedRowKeys, ...prevSelectedRowKeys]} />
      </Fragment>
    )
  }
}

export default FilterUser