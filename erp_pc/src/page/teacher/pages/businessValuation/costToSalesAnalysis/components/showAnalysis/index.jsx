/* 
  这里是封装的内容模块
*/
import React, {useEffect, useState} from 'react'
import {Cascader, Button, Space, message, Divider} from 'antd'
import {useHistory, useParams} from 'react-router'
import {Chart} from '@antv/g2'
import httpUtil from 'utils/request'
import Change from './util'
import './index.css'

// 收集状态
let stateObj = {}

// 定义一个图表
let chart

// 选项栏
const options = [
  {
    value: 1,
    label: '第一年'
  },
  {
    value: 2,
    label: '第二年'
  },
  {
    value: 3,
    label: '第三年'
  },
  {
    value: 4,
    label: '第四年'
  },
  {
    value: 5,
    label: '第五年'
  },
  {
    value: 6,
    label: '第六年'
  }
]

export const OwnerEquity = props => {
  // 将状态有关的东西存入stateObj
  stateObj = {...props.state}

  const params = useParams()
  const history = useHistory()

  // 设置状态
  const [year, setYear] = useState([1])
  const [hasNewTableData, setHasNewTableData] = useState(true)
  const [timer, setTimer] = useState(null)

  const renderChartData = data => {
    chart.data(data)
    chart.render()
  }

  // 发送请求获取数据
  const getChartData = (year, isShowMessage = 1) => {
    const formdata = new FormData()
    // 从url的参数中获取到小组名
    formdata.append('groupName', params.groupName)
    formdata.append('year', year[0])
    httpUtil.getCostStructure(formdata).then(res => {
      const newData = new Array()
      res.data.forEach(item => {
        // ***********************
        item.member = item.member.userName
        // ***********************
        for (let propName in item) {
          const zh_propName = Change(propName)
          let dataItem = null
          if (zh_propName) {
            dataItem = {
              member: item.member,
              type: Change(propName),
              value: item[propName]
            }
            newData.push(dataItem)
          }
        }
      })
      // 通过isShowMessage 来实现，数据首次加载时不出现第几年数据获取成功的提示
      isShowMessage && message.success(`第${year[0]}年数据获取成功！`)
      // 请求成功说明有新的chart数据
      setHasNewTableData(true)
      // 渲染数据到图表上
      renderChartData(newData)
      console.log(newData)
    })
  }

  // 给获取图表数据添加防抖功能
  const debounceGetChartData = (...args) => {
    timer && clearTimeout(timer)
    setTimer(
      setTimeout(function () {
        getChartData(...args)
      }, 1000)
    )
  }

  // 查看所有者权益表
  const showOwnerEquity = () => {
    if (hasNewTableData === false) {
      getChartData(year)
    } else if (hasNewTableData === true) {
      message.warning('请选择其他年！')
    }
  }

  // 展示上一年的所有者权益表
  const showLastYear = () => {
    const newyear = [...year]
    newyear[0]--
    if (newyear[0] === 0) {
      newyear[0] = 6
    }
    // 发送请求
    debounceGetChartData(newyear)
    // 保存最新的year
    setYear(newyear)
  }

  // 展示下一年的所有者权益表
  const showNextYear = () => {
    const newyear = [...year]
    newyear[0]++
    if (newyear[0] === 7) {
      newyear[0] = 1
    }
    // 发送请求
    debounceGetChartData(newyear)
    // 保存最新的year
    setYear(newyear)
  }

  // 下拉框变化时
  const onChange = value => {
    // 如果下拉框进行选择，就说明没有新的chart数据了
    setHasNewTableData(false)
    setYear(value)
  }

  useEffect(() => {
    // 渲染图表
    chart = new Chart({
      container: 'container',
      autoFit: true,
      height: 450
    })
    chart.scale('value', {nice: true})
    chart.legend({
      position: 'top'
    })
    chart
      .interval()
      .position('member*value')
      .color('type')
      .adjust([
        {
          type: 'dodge',
          marginRatio: 0
        }
      ])

    // 发送请求
    getChartData(year, 0)

    return () => {
      // 返回到小组列表时，清除已经获取到的状态
    }
  }, [])

  return (
    <>
      <p className='hxy-return'>
        <Button
          type='link'
          onClick={() => {
            history.goBack()
          }}
          className='search-box'
          style={{margin: 0, paddingLeft: 0}}>
          {'<'} 返回
        </Button>
        <span>
          <strong>{params.groupName} 小组</strong>
        </span>
      </p>
      <Space size='middle'>
        <Cascader
          options={options}
          onChange={onChange}
          value={year}
          placeholder='年'
        />
        <Button type='primary' onClick={showOwnerEquity}>
          查看
        </Button>
        <Button type='primary' onClick={showLastYear}>
          上一年
        </Button>
        <Button type='primary' onClick={showNextYear}>
          下一年
        </Button>
      </Space>
      {/* <Divider
        style={{
          backgroundColor: "rgb(225 224 224 / 85%)",
          marginTop: 15,
          marginBottom: 0
        }}
      /> */}
      <div id='container-wrapper'>
        <div id='container' />
      </div>
    </>
  )
}
