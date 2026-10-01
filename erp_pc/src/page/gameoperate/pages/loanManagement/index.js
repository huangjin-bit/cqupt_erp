import React from 'react'

import './index.scss'
import httpUtil from 'utils/request/index'
import { Select, Button, Modal, message  } from 'antd'


function LoanManagement(props) {

  let [allData, setAllData] = React.useState([])
  let [loanTypeName, setLoanTypeName] = React.useState('全部')
  let [status, setStatus] = React.useState(2)

  const getMessage = () => {
    if (status === 2) {
      if (loanTypeName === '全部') {
        httpUtil.loanOfuser().then(res => {
          if (res.status === 0) setAllData(res.data)
        })
      } else {
        httpUtil.loanOfuser({ loanTypeName }).then(res => {
          if (res.status === 0) setAllData(res.data)
        })
      }
    }
    else {
      if (loanTypeName === '全部') {
        httpUtil.loanOfuser({ status }).then(res => {
          if (res.status === 0) setAllData(res.data)
        })
      } else {
        httpUtil.loanOfuser({ loanTypeName, status }).then(res => {
          if (res.status === 0) setAllData(res.data)
        })
      }
    }
  }

  React.useEffect(() => {
    getMessage()
  }, [])

  const selectChange = (value, num) => {
    if (num === 1) setLoanTypeName(value)
    if (num === 2) {
      if (value === '未还贷款') setStatus(0)
      else if (value === '已还贷款') setStatus(1)
      else setStatus(2)
    }
  }

  const search = () => {
    getMessage()
  }

  const returnMoney = (loanId) => {
    return () => {
      Modal.confirm({
        title: '是否确认归还贷款（请对比现金,小心破产）',
        okText: '确认',
        cancelText: '取消',
        onOk() {
          
          httpUtil.returnLoan({ loanId }).then(res => {
            if (res.status === 0) {
              //* 向Header发送消息通知header组件重新请求Info数据
              window.dispatchEvent(new Event('info'))
              message.success(res.message)
              getMessage()
            } else {
              message.error(res.message)
            }
          })
        },
        ontouchcancel() { }
      })
    }

  }

  return <div className="userprepare-wrapper">
    <span className='select-span'>贷款类型:</span>
    <Select className='select' size='middle' defaultValue="全部" onChange={(value) => selectChange(value, 1)}>
      <Select.Option value='长期贷款'>长期贷款</Select.Option>
      <Select.Option value='短期贷款'>短期贷款</Select.Option>
      <Select.Option value='高利贷'>高利贷</Select.Option>
      <Select.Option value='全部'>全部</Select.Option>
    </Select>
    <span className='select-span'>贷款年限:</span>
    <Select className='select' size='middle' defaultValue="全部" onChange={(value) => selectChange(value, 2)}>
      <Select.Option value='未还贷款'>未还贷款</Select.Option>
      <Select.Option value='已还贷款'>已还贷款</Select.Option>
      <Select.Option value='全部'>全部</Select.Option>
    </Select>
    <Button className='select-btn' type='primary' onClick={search}>查询</Button>
    <table>
      <thead>
        <tr>
          <td>贷款编号</td>
          <td>贷款类型</td>
          <td>开始贷款时间</td>
          <td>最后还款时间</td>
          <td>贷款金额</td>
          <td>处理</td>
        </tr>
      </thead>
      <tbody>
        {
          !allData.length ? "" :
            allData.map(item => {
              return <tr key={item.loanId} style={{ backgroundColor: 'white' }}>
                <td>{item.loanId}</td>
                <td>{item.loanTypeName}</td>
                <td>{item.beginTime}</td>
                <td>{item.endTime}</td>
                <td>{item.money}</td>
                <td>{item.status === 0 ? <Button type='primary' onClick={returnMoney(item.loanId)}>归还贷款</Button> : '已归还'}</td>
              </tr>
            })
        }
      </tbody>
    </table>
  </div>
}

export default LoanManagement
