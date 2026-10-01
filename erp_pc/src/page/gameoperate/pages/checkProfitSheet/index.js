import React from 'react'

import httpUtil from 'utils/request/index'
import './index.scss'
import { Select, Button } from 'antd'

function CheckProfitSheet(props) {
  let [allData, setAllData] = React.useState([])
  let [year, setYear] = React.useState(1)
  let [period, setPeriod] = React.useState(1)

  const getMessage = () => {
    httpUtil.profitInfo({ year, period }).then(res => {
      if (res.status === 0) {
        let arr = []
        let Arr = []
        res.data.profits.forEach(item => {
          Arr.push([item.content, item.rows, item.cols])
        })
        for (let i = 0; i < 14; i++) {
          arr.push(Arr.filter((value, index) => value[1] === i + 1))
        }
        arr.forEach(item => {
          for (let i = 0; i < item.length; i++) {
            for (let j = 0; j < item.length; j++) {
              let temp
              if (item[i].rows < item[j].rows) {
                temp = item[i]
                item[i] = item[j]
                item[j] = temp
              }
            }
          }
        })
        setAllData(arr)
      }
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
    console.log(allData);
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
      <table>
        <tbody>
          {allData === [] ? "" :
            allData.map(item => {
              return <tr key={item[0][0]} className='profit-tr' style={{ backgroundColor: 'white' }}>
                <td>{item[0][0]}</td>
                <td>{item[1][0]}</td>
              </tr>
            })
          }
        </tbody>
      </table>
    </div>
  </div>
}

export default CheckProfitSheet
