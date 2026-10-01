import React from 'react'
import { Button, message } from 'antd'

import httpUtil from 'utils/request/index'
import './index.scss'

function DeliveryWithOrder(props) {

  const [orders, setOrders] = React.useState([])

  React.useEffect(() => {
    httpUtil.checkOrders().then(res => {
      console.log(res)
      setOrders(res.data)
    })
  }, [])

  const delivery = (orderId) => {
    return () => {
      httpUtil.deliveryOrder({ orderId }).then(res => {
        if (res.data.status === 1) {
          httpUtil.checkOrders().then(res => {
            setOrders(res.data)
          })
          message.success('交货成功')
        }
        else if (res.data.status === 2) message.error(`交货失败，请首先处理延误时间最长的订单`)
        else message.error(`交货失败，没有足够库存的商品`)
      })
    }
  }

  return <div className="userprepare-wrapper">
    <h3 className='delivery-h3'>（温馨提示： 如果有到期交货的产品，必须先交货到期的订单，就是到期了的订单必须先交货。）</h3>
    <table className='delivery-table'>
      <thead>
        <tr className='delivery-tr' style={{ backgroundColor: 'white' }}>
          <td>订单编号</td>
          <td>市场</td>
          <td>交货日期</td>
          <td>产品</td>
          <td>数量</td>
          <td>单价</td>
          <td>总金额</td>
          <td>应收帐期</td>
          <td>罚金率</td>
          <td>操作</td>
        </tr>
      </thead>
      <tbody>
        {
          orders.length != 0 ?
            orders.map((item) => {
              return <tr className='delivery-table' key={item.orderID}>
                <td>{item.orderID}</td>
                <td>{item.marketName}</td>
                <td>{item.needTime}</td>
                <td>{item.productName}</td>
                <td>{item.pnumber}</td>
                <td>{item.price}</td>
                <td>{item.pnumber * item.price}</td>
                <td>{item.moneyTime}</td>
                <td>{item.penalPercent}</td>
                <td>{item.status === 0 ? <Button className='delivery-button' type='primary' onClick={delivery(item.orderID)}>交货</Button> : '已交货'}</td>
              </tr>
            }) : ""
        }
      </tbody>
    </table>
  </div>
}

export default DeliveryWithOrder
