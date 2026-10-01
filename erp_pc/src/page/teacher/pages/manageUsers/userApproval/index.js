import React, { Component, Fragment } from 'react'
import { Form, Input, Button, Table, Row, Col, Pagination, message, Space } from 'antd'
import { CloseCircleOutlined, CheckCircleOutlined } from '@ant-design/icons'
// import { useEffect, useState } from 'react'
import { withRouter } from 'react-router-dom'
import { connect } from 'react-redux'
import { actionCreators } from '../../../store/index'
// import axios from 'axios'
import { SearchOutlined } from '@ant-design/icons'
import styles from './index.module.scss'
import httpUtil from 'utils/request'

class UserApproval extends Component {
  constructor(props) {
    super(props)
    this.state = {
      //搜索关键词
      keyword: '',
      status: 1,
      pageNumber: 1,
      pageSize: 10,
      total: '',
      //totalOne加载总数时的标志位，设置为1，平时为空''
      totalOne: 1,
      //复选框数据
      selectedRowKeys: [],
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
        },
        {
          title: '操作',
          dataIndex: 'action',
          key: 'action',
          width: 270,
          render: (text, record, index) => {
            return (
              <div className={styles['inline-button']}>
                <Button
                  type='primary'
                  icon={<CheckCircleOutlined />}
                  onClick={() => this.agreeUser(record.userId)}>
                  同意
                </Button>
                <Button
                  danger
                  icon={<CloseCircleOutlined />}
                  onClick={() => this.refusedApproval(record.userId)}>
                  拒绝
                </Button>
              </div>
            )
          }
        }
      ],
      //表的数据
      data: [],
      listData: []
    }
  }
  //搜索
  onFinish = value => {
    const { keyword } = value
    if (keyword === undefined || keyword === '') {
      message.info('请输入学号查找')
      return false
    }
    console.log(keyword)
    const { data: dataArr } = this.state
    //var userQuery = new zpinyin(dataArr,['name','workCode']);
    const reg = new RegExp(`${keyword}`, 'g')
    let itemArr = []
    dataArr.forEach((data, index) => {
      for (var item in data) {
        if (data[item].search(reg) !== -1) {
          itemArr.push(data)
          break
        }
      }
      /*  if (item.userName.search(reg) !== -1) {
        if (temp !== item) {
          listData.push(item)
        }
      } */
    })
    console.log(itemArr)
    if (itemArr.length === 0) {
      message.info(`内容: ${keyword}，没有找到`)
      this.setState({
        listData: itemArr,
        total: 1
      })
    } else {
      message.info(`内容: ${keyword}，找到了${itemArr.length}条相关数据`)
      this.setState({
        listData: itemArr,
        total: 1
      })
    }
    //var formdata = new FormData();
    //可以通过append()方法来追加数据
    //formdata.append('status', this.state.status);
    //formdata.append('keyword', value.keyword);
    // formdata.append('limit', this.state.pageSize)
    // formdata.append('startPage', this.state.pageNumber)
    // let _this = this

    /* httpUtil
      .search(formdata)
      .then(res => {
        const listData = res.data;
        if (listData.length === 0) {
          message.info(' ' + value.keyword + '，没有找到 ！');
        } else {
          message.info(value.keyword + '，找到' + listData.length + '条相关数据');
          this.setState({
            data: listData,
            total: 1,
          });
        }
      })
      .catch(err => console.log(err));  */

    // httpUtil
    //   .search({ keyword, status, startPage, limit })
    //   .then((res) => {
    //     const listData = res.data
    //     if (listData.length === 0) {
    //       message.info(' ' + value.keyword + '，没有找到 ！')
    //     } else {
    //       message.info(
    //         value.keyword + '，找到' + listData.length + '条相关数据'
    //       )
    //       this.setState({
    //         data: listData,
    //         total: 1,
    //       })
    //     }
    //   })
    //   .catch((err) => console.log(err))

    // axios
    //   .post('/erpn/teacher/search', formdata)
    //   .then(function (response) {
    //     const listData = response.data.data
    //     console.log(response)
    //     // if (listData) {
    //     //   _this.setState({
    //     //     data: listData,
    //     //   })
    //     // }
    //   })
    //   .catch(function (error) {
    //     console.log(error)
    //   })
  }
  //输入框变化
  serachAll = input => {
    const keyword = input.target.value
    if (keyword === undefined || keyword === '') {
      this.setState({ listData: this.state.data })
    }
    const { data: dataArr } = this.state
    const reg = new RegExp(`${keyword}`, 'g')
    let itemArr = []
    dataArr.forEach((data, index) => {
      for (var item in data) {
        if (data[item] !== null) {
          if (data[item].search(reg) !== -1) {
            itemArr.push(data)
            break
          }
        }
      }
      //if (item.userName.search(reg) !== -1) {
      //if (temp !== item) {
      //listData.push(item)
      //}
      //}
    })
    if (itemArr.length === 0) {
      this.setState({
        listData: itemArr,
        total: 1
      })
    } else {
      this.setState({
        listData: itemArr,
        total: 1
      })
    }
  }
  //复选框
  onCheckebox = selectedRowKeys => {
    this.setState({ selectedRowKeys })
  }
  onChangeCurrnePage = value => {
    this.setState(
      {
        pageNumber: value || 1
      },
      () => {
        console.log(this.state.pageNumber)

        this.loadData()
      }
    )
  }
  /** 下拉页码 */
  onChangeSizePage = (value, page) => {
    this.setState(
      {
        pageNumber: 1,
        pageSize: page
      },
      () => {
        this.loadData()
      }
    )
  }
  /** 点击加载全部 */
  onChangeSizePages = () => {
    const { pageSize, total, totalOne } = this.state
    if (total < pageSize) {
      message.info('已经到底啦！')
    } else {
      this.setState(
        {
          pageNumber: 1,
          pageSize: total,
          total: totalOne
        },

        () => {
          this.loadData()
        }
      )
      const key = 'updatable'
      message.loading({ content: 'Loading...', key })
      setTimeout(() => {
        message.info({
          content: '已加载全部！',
          key,
          duration: 2
        })
      }, 500)
    }
  }

  //同意审批用户
  agreeUser = userId => {
    let param
    // httpUtil.getTeacherList(userId).then((res) => {
    // const adminId = res.data;
    // param = adminId[0].adminId;

    param = 'wangyong' //获取教师信息接口有问题，写的假数据，todo
    httpUtil
      .passRegisterUser({ userId, adminId: param })
      .then(res => {
        const key = 'updatable'
        message.loading({ content: 'Loading...', key })
        setTimeout(() => {
          message.success({
            content: userId + '，审批成功',
            key,
            duration: 2
          })
        }, 300)

        this.loadData()
      })
      .catch(err => console.log(err))
    // })

    // this.props.passRegisterUser(userId)
  }
  //批量审批用户
  agreeUsers = () => {
    const userIds = this.state.selectedRowKeys.join()
    if (userIds) {
      //   this.props.passBatchRegisterUsers(userIds)

      // httpUtil.getTeacherList(userIds).then(res => {
      //   const adminId = res.data;
      //   const param = adminId[0].adminId;

      const param = 'wangyong' //获取教师信息接口有问题，写的假数据，用户审批以及用户列表也有todo;

      httpUtil
        .passBatchRegisterUsers({ userIds, adminId: param })
        .then(res => {
          const key = 'updatable'
          message.loading({ content: 'Loading...', key })
          setTimeout(() => {
            message.success({
              content: '批量审批成功',
              key,
              duration: 2
            })
          }, 300)
          this.loadData()
        })
        .catch(err => console.log(err))
      // });
    } else {
      const key = 'updatable'
      setTimeout(() => {
        message.warning({
          content: '请选择需要审批的用户',
          key,
          duration: 2
        })
      }, 200)
      return false
    }
  }
  //拒绝审批
  refusedApproval = (userId) => {
    // httpUtil.getTeacherList(userIds).then(res => {
    //   const adminId = res.data;
    //   const param = adminId[0].adminId;
    const param = 'wangyong' //获取教师信息接口有问题，写的假数据，todo
    httpUtil
      .refuseRegisterUsers({ userId, adminId: param })
      .then(res => {
        const key = 'updatable'
        message.loading({ content: 'Loading...', key })
        setTimeout(() => {
          message.warning({
            content: '已拒绝' + userId,
            key,
            duration: 2
          })
        }, 300)
        this.loadData()
      })
      .catch(err => console.log(err))
    // });
  }
  //批量拒绝审批
  refusedApprovals = () => {
    const userIds = this.state.selectedRowKeys.join()
    // httpUtil.getTeacherList(userIds).then(res => {
    //   const adminId = res.data;
    //   const param = adminId[0].adminId;
    const param = 'wangyong' //获取教师信息接口有问题，写的假数据，todo

    httpUtil
      .refuseBatchRegisterUsers({ userIds, adminId: param })
      .then(res => {
        const key = 'updatable'
        message.loading({ content: 'Loading...', key })
        setTimeout(() => {
          message.warning({
            content: '已批量拒绝',
            key,
            duration: 2
          })
        }, 500)
        this.loadData()
      })
      .catch(err => console.log(err))
    // });
  }

  //使用body传参方式
  //   //批量拒绝审批
  //   refusedApprovals = () => {
  //     const userIds = this.state.selectedRowKeys.join()
  //     httpUtil.getTeacherList(userIds).then((res) => {
  //       const adminId = res.data
  //       const param = adminId[0].adminId
  //       const requestData = {
  //         userIds: userIds,
  //         adminId: param,
  //       }
  //       httpUtil
  //         .refuseBatchRegisterUsers(requestData)
  //         .then((res) => {
  //           console.log(res)
  //           const key = 'updatable'
  //           message.loading({ content: 'Loading...', key })
  //           setTimeout(() => {
  //             message.warning({
  //               content: '已批量拒绝',
  //               key,
  //               duration: 2,
  //             })
  //           }, 200)
  //           this.loadData()
  //         })
  //         .catch((err) => console.log(err))
  //     })
  //   }

  loadData = () => {
    httpUtil.findAllRegister()
      .then(res => {
        console.log(res);
        this.setState({
          listData: res.data,
          data: res.data
        })
      })
  }

  componentDidMount() {
    this.loadData()
  }

  render() {
    const { columns, data } = this.state
    const rowSelection = {
      onChange: this.onCheckebox
    }
    return (
      <Fragment>
        <div className='search-box'>
          <Form layout='inline' onFinish={this.onFinish.bind(this)}>
            <Form.Item name='keyword'>
              <Input
                placeholder='请输入学号'
                ref={ref => {
                  this.searchUserRef = ref
                }}
                onChange={this.serachAll}
              />
            </Form.Item>
            <Form.Item shouldUpdate={true}>
              <Button
                type='primary'
                htmlType='submit'
                icon={<SearchOutlined />}>
                搜索
              </Button>
            </Form.Item>
          </Form>
        </div>
        <div className='content-box'>
          <Table
            pagination={false}
            className={styles['table-wrap']}
            rowSelection={rowSelection}
            size='small'
            rowKey='userId'
            columns={columns}
            dataSource={data}
            bordered>
          </Table>
          <Row className={styles["footer-btns"]}>
            <Col span={16}>
              <Button
                type='primary'
                icon={<CheckCircleOutlined />}
                className={styles["pull-left"]}
                onClick={() => this.agreeUsers()}
              >
                批量审批
              </Button>
              <Button
                danger
                onClick={() => this.refusedApprovals()}
                icon={<CloseCircleOutlined />}>
                批量拒绝
              </Button>
            </Col>
            <Col span={8}>
              <Pagination
                className={styles['pull-right']}
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
          <Button type='dashed' block onClick={this.onChangeSizePages}>
            点击加载全部
          </Button>
        </div>
      </Fragment>
    )
  }
}
const mapStateToProps = state => ({
  common: state.common
})

const mapDispatchToProps = {
  passRegisterUser: actionCreators.passRegisterUser,
  passBatchRegisterUsers: actionCreators.passBatchRegisterUsers
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(withRouter(UserApproval))
