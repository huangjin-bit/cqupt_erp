

import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import PubSub from 'pubsub-js'
import localStorage from "localStorage"
import { Steps, Divider, Select, Button, Input, Modal, Card, message, Table } from 'antd'
import ERPWebSocket from 'utils/websocket'
import httpUtil from 'utils/request'
import { actionCreators } from '../../store/index'
import './index.scss'
import img from '../../../../assets/images/pic_meb2.jpg'
import img2 from '../../../../assets/images/pic_meb2_ready.jpg'
import right from '../../../../assets/images/pic_right.jpg'

const { Step } = Steps

function Advertisement(props) {
  console.log(props.gameOperate.member,"props");
  let [success, setSuccess] = React.useState(false)
  //进度条控制
  let [status, setStatus] = React.useState(0)
  //比赛用户所处小组
  let [userGroupName, setGroupName] = React.useState("")
  //已开拓的市场
  let [markets, setMarkets] = React.useState([])
  //所选市场的订单
  let [products, setProducts] = React.useState([])
  //Select的内容
  let [marketValue, setValue] = React.useState('本地市场')
  //已投放的广告列表
  let [doneList, setDoneList] = React.useState([])
  //浮窗控制
  let [visible, setVisible] = React.useState(false)
  //所有订单信息
  let [allOrders, setAllOrders] = React.useState([])
  //用户可选择的订单
  let [chooseOrders, setChooseOrders] = React.useState([])
  //结束选单浮窗显示
  let [isModalVisible, setIsModalVisible] = React.useState(false)
  // 结束选单表格数据
  let [chooseData, setChooseData] = React.useState([])

  let [selectMarket, setSelectMarket] = React.useState('全部')
  let [selectProduct, setSelectProduct] = React.useState('全部')

  // 封装获取结束弹窗请求
  const getModalMessage = () => {
    httpUtil.getOrderDetail().then(res => {
      if (res.status === 0) {
        const arr = []
        // 根据市场和产品名字合并选项
        const memoKey = new Set()
        for (let i = 0; i < res.data.length; i++) {
          const {marketName, productName} = res.data[i]
          const key = `${marketName}${productName}`

          if(!memoKey.has(key)) {
            arr.push({
              chooseID: res.data[i].chooseID,
              marketName: res.data[i].marketName,
              productName: res.data[i].productName
            })
            memoKey.add(key)
          }
        }
        setChooseData(arr)

      } else message.error('数据请求失败')
    })
  }

  //数据默认展示

  let userUnique = localStorage.getItem('userUnique')

  React.useEffect(() => {
  console.log(props,"111")
    // 控制流程条状态
    httpUtil.getCurrentProgress().then((res) => {
      setStatus(status = res.data)
      console.log(res);
      //状态1默认数据请求
      if (res.data === 1) {
        // 获取可选择的市场列表
        httpUtil.getMarketList().then((res) => {
          console.log(res.data[0].status,"111");
          if (res.status === 0) setMarkets(res.data)
          // else if(res.status === 0&&res.data[0].status==null) {message.error("你还未投放任何产品！");console.log(1113);}
          else message.error('请求数据失败1')
        })
        // 获取市场列表中的产品
        httpUtil.getProductByMarket({ marketName: marketValue }).then((res) => {
          if (res.status === 0) setProducts(products = res.data)
          else message.error('请求数据失败2')
        })
      }

      //状态2默认数据请求
      else if (res.data === 2) {
        // 获取已经投放的订单
        httpUtil.getAlreadyAd().then((res) => {
          if (res.status === 0) setDoneList(res.data)
          else message.error('请求数据失败3')
        })
      }

      //状态4默认数据请求
      else if (res.data === 4) {
        // 获取所有订单信息
        httpUtil.getAll().then(res => {
          if (res.status === 0) setAllOrders(allOrders = res.data)
          else message.error('请求数据失败4')
        })
        // 获取可选择订单信息
        httpUtil.getMy().then(res => {
          if (res.status === 0) setChooseOrders(res.data)
          else message.error('请求数据失败5')
        })
        // 获取结束市场弹窗信息
        getModalMessage()
      }
    })

    //小组成员请求
    props.getMembers()

    

    // websocket重连
    httpUtil.info().then(res => {
      setGroupName(userGroupName = res.data.groupName)
      ERPWebSocket.updateInstance(userGroupName)
      setSuccess(success = true)
    })


    //websocket接收消息反应
    PubSub.subscribe('7', async () => {
      await httpUtil.getMy().then(res => {
        setChooseOrders(res.data)
      })
      await setStatus(status = 4)
      httpUtil.getAll().then(res => {
        setAllOrders(allOrders = res.data)
      })

      getModalMessage()
    })
    //websocket接收消息 
    PubSub.subscribe('8', () => {
      httpUtil.getMy().then(res => {
        if (res.status === 0) setChooseOrders(res.data)
        else message.error('请求数据失败')

      })
    })

    return () => {
      PubSub.clearAllSubscriptions()
    }

  }, [])

  // 投广告费阶段Select内容获取传递
  function handleChange(value) {
    httpUtil.getProductByMarket({ marketName: value }).then((res) => {
      if (res.status === 0) setProducts(products = res.data)
      else message.error('请求数据失败')
    })
    setValue(marketValue = value)

  }

  // 根据市场场名称请求广告投放列表
  const searchList = (marketName) => {
    return () => {
      httpUtil.getProductByMarket({ marketName }).then((res) => {
        if (res.status === 0) setProducts(products = res.data)
        else message.error('请求数据失败')
      })
    }
  }

  //修改金额，提交订单
  const moneyChange = (money, id) => {
    products.forEach(item => {
      if (item.adId === id) {
        item.money = money
        setProducts(products)
      }
    })
  }

  // 投放广告
  const toMoney = (adId) => {
    return () => {
      let moneysIndex = []
      products.map(item => moneysIndex.push(item.adId))
      let index = moneysIndex.indexOf(adId)
      let money = products[index].money
      if (money >= 0 && money != ' ' && money != '') {
        if (money % 1 === 0 || money === 0) {
          httpUtil.advertise({ adId, money }).then(res => {
            console.log(res,"666")
            if (res.status === 0) message.success('广告投放成功')
            else message.error('广告投放失败')
          })
        }
        else message.error('广告费只允许正整数')
      }
      else message.error('广告费只允许正整数')
    }
  }

  //第一页下一步
  const firstNext = () => {
    setStatus(status = status + 1)
    httpUtil.getAlreadyAd().then((res) => {
      if (res.status === 0) setDoneList(res.data)
      else message.error('请求数据失败')
    })
  }
  //第二页上一步
  const goFont = () => {
    setStatus(status = status - 1)
    if (markets.length === 0) {
      httpUtil.getMarketList().then((res) => {
        if (res.status === 0) setMarkets(res.data)
        else message.error('请求数据失败')
      })
      httpUtil.getProductByMarket({ marketName: marketValue }).then((res) => {
        if (res.status === 0) setProducts(products = res.data)
        else message.error('请求数据失败')
      })
    }
  }

  //确认广告下一步
  const goWait = () => {
    if(doneList.length==0){console.log(123)
     message.warning("您还未投入任何广告，继续进行的话可能没有订单")
    }
    Modal.confirm({
      title: '是否提交广告费，确认后将不能修改',
      okText: '确认',
      cancelText: '取消',
      onOk() {
        httpUtil.confirm().then((res) => {
          if (res.status === 0) {
            httpUtil.getCurrentProgress().then((response) => {
              if (response.data === 3) {
                props.getMembers()
                setStatus(status = status + 1)
              } else {
                setStatus(status = status + 2)
                // status==4 数据请求
                httpUtil.getAll().then(res => {
                  if (res.status === 0) setAllOrders(allOrders = res.data)
                  else message.error('请求数据失败')
                })
                httpUtil.getMy().then(res => {
                  if (res.status === 0) setChooseOrders(res.data)
                  else message.error('请求数据失败')
                })
                getModalMessage()
              }
            })
          }
          else message.error('你投放的钱太多了同学！')
        })
      },
      onCancel() {
      },
    });
  }



  // 控制所有订单信息的显示
  const controlOrder = () => {
    return () => {
      if (visible === false) {
        setVisible(visible = true)
      }
      else setVisible(visible = false)
    }
  };

  // 选择订单阶段市场select框改变
  function selectChange(value) {
    setSelectMarket(selectMarket = value)
  }

  // 选择订单阶段产品select框改变
  function productChange(value) {
    setSelectProduct(selectProduct = value)
  }

  // 用户选择订单
  const chooseOrder = (orderId) => {
    Modal.confirm({
      title: '是否选择订单，确认后将不能修改',
      okText: '确认',
      cancelText: '取消',
      onOk() {
        httpUtil.chooseOrder({ orderId }).then(res => {
          if (res.status === 0) {
            if (res.data === null) {
              message.success('选择订单成功')
              httpUtil.getMy().then(res => {
                setChooseOrders(res.data)
              })
            }
            else if (res.data === 1) message.error('等待当前用户选择该产品的订单')
            else if (res.data === 2) message.error('未轮到该用户选择该产品的订单')
            else if (res.data === 3) message.error('ISO未研发成功，选单失败')
            else if (res.data === 4) message.error('该市场没有开拓完成')
            else if (res.data === 5) message.error('订单状态异常，不能选择')

          } else message.error(res.message)
        })
      },
      onCancel() {
      },
    });
  }

  // 结束浮窗取消显示
  const modalCancel = () => {
    setIsModalVisible(false)
  }

  // 结束浮窗的显示
  const modalOpen = () => {
    setIsModalVisible(true)
  }

  // 结束单个产品
  const finishProduct = value => {
    Modal.confirm({
      title: '是否结束该产品订单？',
      okText: '确认',
      cancelText: '取消',
      onOk() {
        httpUtil.endSelectOrder(value).then(res => {
          if (res.status === 0) {
            let arr = chooseData
            arr.splice(arr.indexOf(value), 1)
            setChooseData([...arr])
            message.success('结束产品订单成功')
            httpUtil.getMy().then(res => {
              if (res.status === 0) setChooseOrders(res.data)
              else message.error('请求数据失败')
            })
          } else message.error('结束产品订单订单失败')
        })
      },
      onCancel() {
      },
    });
  }

  // 结束投放广告
  const finishOrder = () => {
    Modal.confirm({
      title: '是否结束所有订单？（Ps：结束订单后就这季度的订单同时结束）',
      okText: '确认',
      cancelText: '取消',
      onOk() {
        httpUtil.finishAll().then(res => {
          if (res.status === 0) {
            setStatus(status = 5)
            setIsModalVisible(false)
            // ERPWebSocket.destoryInstance()
          } else message.error('结束选择订单失败')
        })
      },
      onCancel() {
      },
    });
  }

  //广告投放结束，跳转查看订单页面
  const checkedOrders = () => {
    props.history.push('/gameoperate/checkOrder')
  }


  // 结束订单浮窗列表
  const columns = [
    {
      title: '编号',
      dataIndex: 'chooseID',
      key: 'chooseID',
      align: 'center'
    },
    {
      title: '市场',
      dataIndex: 'marketName',
      key: 'marketName',
      align: 'center'
    },
    {
      title: '产品',
      dataIndex: 'productName',
      key: 'productName',
      align: 'center'
    },
    {
      title: '操作',
      key: 'tags',
      dataIndex: 'tags',
      align: 'center',
      render: (_, choose) => {
        return (
          <span>
            <Button className='table-btn' type='primary' onClick={() => finishProduct(choose)}>结束选单</Button>
          </span>)
      }
    }
  ]

  return <div className="userprepare-wrapper">
    <div className='ad-left'>
      <Steps
        type="default"
        className="site-navigation-steps"
        current={status - 1}
      >
        <Step
          title="1. 投放广告"
        />
        <Step
          title="2. 广告投放确认"
        />
        <Step
          title="3. 等待大厅"
        />
        <Step
          title="4. 订单选取"
        />
        <Step
          title="5. 结束选订单"
        />
      </Steps>

      {success === true ? status === 1 ?
        <div className='first-step'>
          <Divider orientation="left">广告投放 &gt;&gt;</Divider>
          <div className='left-top'>
            <span className='select-span'>请选择市场</span>
            <Select className='select' size='middle' defaultValue="本地市场" onChange={handleChange}>
              {markets.map((item) => {
                return <Select.Option value={item.marketName} key={item.marketName}>{item.marketName}</Select.Option>
              })}
            </Select>
            <Button className='search' type="primary" onClick={searchList(marketValue)}>查询</Button>
            <Button className='next' type='primary' onClick={firstNext}>已投放完毕，下一步&gt;</Button><br />
            <table>
              <thead>
                <tr style={{ backgroundColor: 'white' }}>
                  <td>产品</td>
                  <td>投放广告（单位:万）</td>
                  <td>操作</td>
                </tr>
              </thead>
              <tbody>
                {
                  products.map((item) => {
                    return <tr key={item.adId}>
                      <td>{item.productName}</td>
                      <td><Input style={{ width: '60%' }} onChange={e => moneyChange(e.target.value, item.adId)} /></td>
                      <td><Button type='primary' onClick={toMoney(item.adId)}>提交</Button></td>
                    </tr>
                  })
                }
              </tbody>
            </table>
          </div>
        </div>

        : status === 2 ?

          <div className='second-step'>
            <Divider orientation="left">广告投放确认  &gt;&gt;</Divider>
            <div className='left-top'>
              <Button type='primary' onClick={goFont}>&lt; 返回修改广告费</Button>
              <Button style={{ float: 'right' }} type='primary' onClick={goWait}>确认订单，下一步 &gt;</Button><br />
              <table>
                <thead>
                  <tr style={{ backgroundColor: 'white' }}>
                    <td>市场</td>
                    <td>产品</td>
                    <td>投放广告（单位:万）</td>
                  </tr>
                </thead>
                <tbody>
                  {
                    doneList.map((item) => {
                      if (item.money !== "0")
                        return <tr key={item.adId}>
                          <td>{item.marketName}</td>
                          <td>{item.productName}</td>
                          <td>{item.money}</td>
                        </tr>
                    })

                  }
                </tbody>
              </table>
            </div>
          </div>

          : status === 3 ?

            <div className='third-step'>
              <Divider orientation="left">等待大厅&gt;&gt;</Divider>
              {props.gameOperate.member.length===0?'':props.gameOperate.member.map((item) => {
                return <div className='wait-item' key={item.userName}>
                  {item.curProcess >= 3 ? <img src={img2}></img> : <img src={img}></img>}
                  <span>{item.userName}</span>
                </div>
              })}
            </div>

            : status === 4 ?

              <div className='forth-step'>
                <Divider orientation="left">订单选取  &gt;&gt;</Divider>
                <div>
                  <span className='select-span'>请选择市场:</span> <Select className='select' size='middle' defaultValue="全部" onChange={selectChange}>
                    <Select.Option value='全部'>全部</Select.Option>
                    <Select.Option value='本地市场'>本地市场</Select.Option>
                    <Select.Option value='区域市场'>区域市场</Select.Option>
                    <Select.Option value='国内市场'>国内市场</Select.Option>
                    <Select.Option value='亚洲市场'>亚洲市场</Select.Option>
                    <Select.Option value='国际市场'>国际市场</Select.Option>
                  </Select>
                  <span className='select-span'>请选择产品:</span><Select className='select' size='middle' defaultValue="全部" onChange={productChange}>
                    <Select.Option value='全部'>全部</Select.Option>
                    <Select.Option value='P1'>P1</Select.Option>
                    <Select.Option value='P2'>P2</Select.Option>
                    <Select.Option value='P3'>P3</Select.Option>
                    <Select.Option value='P4'>P4</Select.Option>
                  </Select>
                  <Button className='forthBtn' type="primary" onClick={controlOrder()}>查看所有订单信息</Button>
                  <Button className='forthBtn' type="primary" onClick={modalOpen}>结束选单</Button>

                  <div>
                    {chooseOrders.map(item => {
                      if (item.marketName === selectMarket || selectMarket === '全部') {
                        if (item.productName === selectProduct || selectProduct === '全部')
                          return <Card className={item.userUnique === userUnique ? 'adCard' : 'adCard noAdCard'} key={item.orderID}>
                            <p className='adCardSpan'>订单编号:  {item.orderID}  {item.userUnique === userUnique ? <Button type='primary' className='pBtn' onClick={() => chooseOrder(item.orderID)}>选择订单</Button> : ''}</p>
                            <div className='adCardDiv'>
                              <div>
                                <span>市场:  {item.marketName}</span><br />
                                <span>产品:  {item.productName}</span><br />
                                <span>数量:  {item.pNumber}</span><br />
                                <span>单价:  {item.price}</span><br />

                              </div>
                              <div>
                                <span>总计金额:  {item.price * item.pNumber}</span><br />
                                <span>交货日期:  {item.needTime}</span><br />
                                <span>货款到账日期:  {item.moneyTime}</span><br />
                                <span>罚金率:  {item.penalPercent}</span><br />
                              </div>
                            </div>
                            <span className='specialRem'>特别说明:  {item.specialRem ? item.specialRem : '无'}</span><br />
                            {item.userUnique === userUnique ? '' : <p className='pBottom'>该订单正在被&nbsp;{item.userName}&nbsp;选择</p>}
                          </Card>
                      }
                    })}
                  </div>

                  {visible === true ? <table>
                    <thead>
                      <tr style={{ backgroundColor: 'white' }}>
                        <td>市场</td>
                        <td>订单编号</td>
                        <td>产品名称</td>
                        <td>数量</td>
                        <td>单价</td>
                        <td>总金额</td>
                        <td>交货时间</td>
                        <td>货款账期</td>
                        <td>罚金率</td>
                        <td>特殊说明</td>
                      </tr>
                    </thead>
                    <tbody>
                      {
                        allOrders.map((item) => {
                          return <tr key={item.orderID}>
                            <td>{item.marketName}</td>
                            <td>{item.orderID}</td>
                            <td>{item.productName}</td>
                            <td>{item.pnumber}</td>
                            <td>{item.price}</td>
                            <td>{item.pnumber * item.price}</td>
                            <td>{item.needTime}</td>
                            <td>{item.moneyTime}</td>
                            <td>{item.penalPercent}</td>
                            <td>{item.specialRem ? item.specialRem : '无'}</td>
                          </tr>
                        })
                      }
                      <tr>
                        <td colSpan='10'>
                          <Button type="primary" onClick={controlOrder()}>返回订单会</Button>
                        </td>
                      </tr>
                    </tbody>
                  </table> : ''}

                </div>
              </div>

              : status === 5 ?

                <div className='fifth-step'>
                  <div>
                    <img src={right} alt='正确'></img>
                    <p>你已成功选取订单！</p>
                    <p>查看订单信息，请进入<a onClick={checkedOrders}> 销售管理》查看订单！</a></p>
                  </div>
                </div>

                : '' : ''}

    </div>



    {/* 结束订单浮窗 */}
    <Modal visible={isModalVisible} footer={null} onCancel={modalCancel}>
      <Table size='small' columns={columns} dataSource={chooseData} pagination={false} ></Table>
      <p style={{ textAlign: 'center', marginTop: '10px' }}>
        <Button className='modal-table-btn' type='primary' onClick={finishOrder}>结束所有订单</Button>
      </p>
    </Modal>

  </div >
}
const mapStateToProps = state => ({
  userprepare: state.userprepare,
  common: state.common,
  gameOperate: state.gameOperate
})
const mapDispatchToProps = {
  getMembers: actionCreators.getMembers,
}
export default connect(mapStateToProps, mapDispatchToProps)(withRouter(Advertisement))
