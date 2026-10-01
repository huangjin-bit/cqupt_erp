import React from 'react'

import './index.scss'
import httpUtil from 'utils/request/index'
import { Select, Button, message } from 'antd'

function CheckBalanceSheet(props) {

  let [allData, setAllData] = React.useState([])
  let [year, setYear] = React.useState(1)
  let [period, setPeriod] = React.useState(1)

  const getMessage = () => {
    httpUtil.balanceInfo({ year, period }).then(res => {
      if (res.status === 0) {
        let arr = []
        let Arr = []
        res.data.balanceSheets.forEach(item => {
          Arr.push([item.content, item.rows, item.cols])
        })
        for (let i = 0; i < 15; i++) {
          arr.push(Arr.filter((value, index) => value[1] === i + 1))
        }
        arr.forEach(item => {
          for (let i = 0; i < item.length; i++) {
            for (let j = 0; j < item.length; j++) {
              let temp
              if (item[i].cols < item[j].cols) {
                temp = item[i]
                item[i] = item[j]
                item[j] = temp
              }
            }
          }
        })
        setAllData(arr)
      }else message.error('查询失败，并未推进到该周期')
    })
  }

  React.useEffect(() => {
    getMessage()
  }, [])

  const selectChange = (value, type) => {
    if (type === 1) setYear(value)
    if (type === 2) setPeriod(value)
  }

  const search = () => {
    getMessage()
  }

  return <div className="userprepare-wrapper">
    <div className='sheet-top'>
      <span className='select-span'>查询条件:</span>
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
      <Button className='select-btn' type='primary' onClick={search}>查询</Button>
    </div>
    <div className='sheet-bottom'>
      <table style={{ fontSize: '15px' }} border='1'>
        <tbody>
          {allData === [] ? "" :
            allData.map(item => {
              if (item[0][1] === 2 || item[0][1] === 9) {
                return <tr style={{ height: '30px', backgroundColor: 'white' }} className='sheet-tr'>
                  <td>{item[0][0]}</td>
                  <td></td>
                  <td></td>
                  <td>{item[1][0]}</td>
                  <td></td>
                  <td></td>
                </tr>
              }
              else if (item[0][1] === 15) {
                return <tr style={{ height: '30px', backgroundColor: 'white' }} className='sheet-tr'>
                  <td>{item[0][0]}</td>
                  <td>{item[1][0]}</td>
                  <td>{item[2][0]}</td>
                  <td></td>
                  <td></td>
                  <td></td>
                </tr>
              }
              else {
                return <tr style={{ height: '30px', backgroundColor: 'white' }} className='sheet-tr'>
                  <td>{item[0][0]}</td>
                  <td>{item[1][0]}</td>
                  <td>{item[2][0]}</td>
                  <td>{item[3][0]}</td>
                  <td>{item[4][0]}</td>
                  <td>{item[5][0]}</td>
                </tr>
              }
            })
          }
        </tbody>
      </table>
    </div>
  </div>
}

export default CheckBalanceSheet
