import React from 'react'

import './index.scss'
import { Select, Button, message } from 'antd'
import httpUtil from 'utils/request/index'

function CheckJournalEntry(props) {

  let [allData, setAllData] = React.useState(false)
  let [beginYear, setBeginYear] = React.useState(1)
  let [endYear, setEndYear] = React.useState(1)
  let [beginPeriod, setBeginPeriod] = React.useState(1)
  let [endPeriod, setEndPeriod] = React.useState(1)

  const getMessage = type => {
    let pageIndex
    if (type === 0) pageIndex = 1
    if (type === 1) pageIndex = allData.curPage + 1
    if (type === 2) pageIndex = allData.curPage - 1
    httpUtil.accountInfo({ pageIndex, beginYear, beginPeriod, endYear, endPeriod, pageSize: 6 }).then(res => {
      if (res.status === 0) {
        console.log(res)
        setAllData(res.data)
      }
    })
  }

  React.useEffect(() => {
    getMessage(0)
  }, [])

  const selectChange = (value, type) => {
    if (type === 1) setBeginYear(value)
    if (type === 2) setBeginPeriod(value)
    if (type === 3) setEndYear(value)
    if (type === 4) setEndPeriod(value)
  }

  const search = () => {
    getMessage(0)
  }

  const goNext = () => {
    return () => {
      if (allData.pages === 0) alert('已经是最后一页!')
      else if (allData.curPage === allData.pages) alert('已经是最后一页!')
      else {
        getMessage(1)
      }
    }
  }

  const goForward = () => {
    return () => {
      if (allData.pages === 0) alert('已经是第一页!')
      else if (allData.curPage === 1) alert('已经是第一页!')
      else {
        getMessage(2)
      }
    }
  }

  return <div className="userprepare-wrapper">
    <div className='entry-top'>
      <span className='select-span'>起始时间:</span>
      <Select className='select' size='middle' defaultValue="1" onChange={(value) => selectChange(value, 1)}>
        <Select.Option value='1'>1</Select.Option>
        <Select.Option value='2'>2</Select.Option>
        <Select.Option value='3'>3</Select.Option>
        <Select.Option value='4'>4</Select.Option>
        <Select.Option value='5'>5</Select.Option>
        <Select.Option value='6'>6</Select.Option>
      </Select>
      <span className='select-span'>年</span>
      <Select className='select' size='middle' defaultValue="1" onChange={(value) => selectChange(value, 2)}>
        <Select.Option value='1'>1</Select.Option>
        <Select.Option value='2'>2</Select.Option>
        <Select.Option value='3'>3</Select.Option>
        <Select.Option value='4'>4</Select.Option>
      </Select>
      <span className='select-span'>期</span>
      <span className='select-span' style={{ marginLeft: '30px' }}>结束时间:</span>
      < Select className='select' size='middle' defaultValue="1" onChange={(value) => selectChange(value, 3)}>
        <Select.Option value='1'>1</Select.Option>
        <Select.Option value='2'>2</Select.Option>
        <Select.Option value='3'>3</Select.Option>
        <Select.Option value='4'>4</Select.Option>
        <Select.Option value='5'>5</Select.Option>
        <Select.Option value='6'>6</Select.Option>
      </Select>
      <span className='select-span'>年</span>
      <Select className='select' size='middle' defaultValue="1" onChange={(value) => selectChange(value, 4)}>
        <Select.Option value='1'>1</Select.Option>
        <Select.Option value='2'>2</Select.Option>
        <Select.Option value='3'>3</Select.Option>
        <Select.Option value='4'>4</Select.Option>
      </Select>
      <span className='select-span'>期</span>
      <Button className='select-btn' type='primary' onClick={search}>查询</Button>
    </div>
    <div className='entry-middle'>
      <table>
        <thead>
          <tr style={{ backgroundColor: 'whitesmoke', color: 'rgb(136, 133, 133)' }}>
            <td>凭条</td>
            <td>发生时间</td>
            <td>交易概要</td>
            <td>科目</td>
            <td>类型</td>
            <td>金额</td>
          </tr>
        </thead>
        <tbody>
          {allData === false ? "" :
            allData.accountDetails.map(_item => {
              return < >
                <tr style={{ backgroundColor: 'white' }} >
                  <td rowSpan='2'>{_item.accountId}</td>
                  <td rowSpan='2'>{_item.happenTime}</td>
                  <td rowSpan='2'>{_item.accountIdDescription}</td>
                  <td>{_item.accountDetailList.length != 0 ? _item.accountDetailList[0].item : ''}</td>
                  <td>{_item.accountDetailList.length != 0 ? _item.accountDetailList[0].itemType : ''}</td>
                  <td>{_item.accountDetailList.length != 0 ? _item.accountDetailList[0].money : ''}</td>
                </tr>
                <tr>
                  <td>{_item.accountDetailList.length != 0 ? _item.accountDetailList[1].item : ''}</td>
                  <td>{_item.accountDetailList.length != 0 ? _item.accountDetailList[1].itemType : ''}</td>
                  <td>{_item.accountDetailList.length != 0 ? _item.accountDetailList[1].money : ''}</td>
                </tr>
              </>
            })
          }
        </tbody>
      </table>
      <div className='entry-buttom'>
        <p className='order-p'>当前是第<span>  {allData ? allData.curPage : 0}  </span>页,共有<span>  {allData ? allData.pages : 0}  </span>页</p><br />
        <span className='span-buttom'><Button type='primary' onClick={goForward()}>上一页</Button>  <Button type='primary' onClick={goNext()}>下一页</Button></span>
      </div>
    </div>
  </div >
}

export default CheckJournalEntry
