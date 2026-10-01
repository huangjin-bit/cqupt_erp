import React from 'react'

import { Button } from 'antd'
import httpUtil from 'utils/request'
import '../checkPurchaseOrders/index.scss'

function CheckPurchaseOrders(props) {

  let [allMessage, setAllMessage] = React.useState({})
  let [index, setIndex] = React.useState(1)
  let [success, setSuccess] = React.useState(false)

  React.useEffect(() => {
    httpUtil.unreachMaterial({ index: 1, size: 8 }).then(res => {
      setAllMessage(res.data)
      setSuccess(true)
    })
  }, [])

  const goForward = () => {
    return () => {
      if (allMessage.pages === 0) alert('已经是第一页!')
      else if (allMessage.curPage === 1) alert('已经是第一页!')
      else {
        httpUtil.unreachMaterial({ index: allMessage.curPage - 1, size: 8 }).then(res => {
          setAllMessage(res.data)
        })
      }
    }
  }

  const goNext = () => {
    return () => {
      if (allMessage.pages === 0) alert('已经是最后一页!')
      else if (allMessage.curPage === allMessage.pages) alert('已经是最后一页!')
      else {
        httpUtil.unreachMaterial({ index: allMessage.curPage + 1, size: 8 }).then(res => {
          setAllMessage(res.data)
          setIndex(index = index + 1)
        })
      }
    }
  }

  return <div className="userprepare-wrapper">
    {success === true ?
      <table>
        <thead>
          <tr style={{ backgroundColor: 'white' }}>
            <td>订单编号</td>
            <td>原材料名字</td>
            <td>订购数量</td>
            <td>所在仓库</td>
            <td>采购时间</td>
            <td>到货时间</td>
          </tr>
        </thead>
        <tbody>
          {allMessage.materialName === [] ?
            ""
            :
            allMessage.materialUnReach.map((item) => {
              return <tr key={item.orderId}>
                <td>{item.orderId}</td>
                <td>{item.materialName}</td>
                <td>{item.mnumber}</td>
                <td>{item.wareHouseName}</td>
                <td>{item.happenTime}</td>
                <td>{item.endTime}</td>
              </tr>

            })
          }
        </tbody>
      </table>
      : ''
    }
    <div className='div-buttom'>
      <p className='order-p'>当前是第<span>  {allMessage.curPage}  </span>页,共有<span>  {allMessage.pages}  </span>页</p><br />
      <span className='span-buttom'><Button type='primary' onClick={goForward()}>上一页</Button>  <Button type='primary' onClick={goNext()}>下一页</Button></span>
    </div>
  </div>
}

export default CheckPurchaseOrders
