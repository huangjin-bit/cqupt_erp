import React from 'react'
import { Select } from 'antd'

import './index.scss'
import httpUtil from 'utils/request/index'

function CheckOrder(props) {
  let [orders, setOrders] = React.useState([])
  let [select, setSelect] = React.useState(3)

  React.useEffect(() => {
    httpUtil.checkOrders().then(res => {
      setOrders(res.data)
    })
  }, [])

  const orderChange = (value) => {
    if (value === `未交货`) setSelect(select = 0)
    else if (value === `全部`) setSelect(select = 3)
    else setSelect(select = 1)
  }

  return <div className="userprepare-wrapper">
    <span className='div-span'>订单状态 : </span>
    <Select className='select' size='middle' defaultValue="全部" onChange={orderChange}>
      <Select.Option value='全部'>全部</Select.Option>
      <Select.Option value='已交货'>已交货</Select.Option>
      <Select.Option value='未交货'>未交货</Select.Option>
    </Select>
    <table>
      <thead>
        <tr style={{ backgroundColor: 'white' }}>
          <td>订单编号</td>
          <td>市场</td>
          <td>交货日期</td>
          <td>产品</td>
          <td>数量</td>
          <td>单价</td>
          <td>总金额</td>
          <td>应收帐期</td>
          <td>罚金率</td>
          <td>订货状态</td>
        </tr>
      </thead>
      <tbody>
        {
          orders.length !== 0 ?
            orders.map((item) => {
              if (select === 3 || select === item.status)
                return <tr key={item.orderID}>
                  <td>{item.orderID}</td>
                  <td>{item.marketName}</td>
                  <td>{item.needTime}</td>
                  <td>{item.productName}</td>
                  <td>{item.pnumber}</td>
                  <td>{item.price}</td>
                  <td>{item.pnumber * item.price}</td>
                  <td>{item.moneyTime}</td>
                  <td>{item.penalPercent}</td>
                  <td>{item.status === 0 ? `未交货` : `已交货`}</td>
                </tr>
            }) : ""
        }
      </tbody>
    </table>
  </div >
}

export default CheckOrder
