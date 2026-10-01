import React from 'react'

import httpUtil from 'utils/request/index'
import './index.scss'
import { Select, Button, message } from 'antd'

function DiscountAccountsReceivable(props) {

  let [allData, setAllData] = React.useState([])

  const getMessage = () => {
    httpUtil.willreceiveInfo().then(res => {
      console.log(res);
      if (res.status === 0) setAllData(res.data)
    })
  }

  React.useEffect(() => {
    getMessage()
  }, [])

  const recieve = (willReceiveId) => {
    return () => {
      httpUtil.willreceiveDelete({ willReceiveId }).then(res => {
        if (res.status === 0) {
          message.success('贴现成功')
          getMessage()
        } else message.error('贴现失败')
      })
    }
  }

  return <div className="userprepare-wrapper">
    <span className='account-span'>说明：贴现比率为{allData.rate}，即如果立即贴现，应收账款的75%将转化为现金，其余将转化为财务费用。</span>
    <table>
      <thead>
        <tr>
          <td>应收账款编号</td>
          <td>金额</td>
          <td>入账时间</td>
          <td>到款时间</td>
          <td>事由说明</td>
          <td>处理</td>
        </tr>
      </thead>
      <tbody>
        {!allData.willReceives ? "" :
          allData.willReceives.map(item => {
            return <tr key={item.willReceiveId} style={{ backgroundColor: 'white' }}>
              <td>{item.willReceiveId}</td>
              <td>{item.money}</td>
              <td>{item.beginTime}</td>
              <td>{item.endTime}</td>
              <td>{item.willReceiveDescription}</td>
              <td><Button type='primary' className='account-btn' onClick={recieve(item.willReceiveId)} >贴现</Button></td>
            </tr>
          })
        }
      </tbody>
    </table>
  </div>
}

export default DiscountAccountsReceivable
