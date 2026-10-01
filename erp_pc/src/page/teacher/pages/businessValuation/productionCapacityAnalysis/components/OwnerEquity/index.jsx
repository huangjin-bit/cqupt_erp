/* 
  这里是封装的内容模块
*/
import React, {useEffect, useState} from 'react'
import {Button, message} from 'antd'
import {useHistory, useParams} from 'react-router'
import {Chart} from '@antv/g2'
import httpUtil from 'utils/request'
import './index.css'

// 收集状态
let stateObj = {}

// 定义一个图表
let chart

export const OwnerEquity = props => {
  // 将状态有关的东西存入stateObj
  stateObj = {...props.state}

  const params = useParams()
  const history = useHistory()

  const [username, setUsername] = useState([])

  // 发送请求获取数据
  const getChartData = () => {
    const formdata = new FormData()
    // 从url的参数中获取到小组名
    formdata.append('groupName', params.groupName)
    httpUtil.getMemberCapacity(formdata).then(res => {
      console.log(res.data)
      // 渲染数据到图表上
      for (let i = 0; i < res.data.length; i++) {
        res.data[i].member = res.data[i].member.userName
      }
      console.log(res.data)
      renderChartData(res.data)
    })
  }
  const renderChartData = data => {
    chart.data(data)
    chart.render()
  }
  useEffect(() => {
    // 渲染图表
    chart = new Chart({
      container: 'container',
      autoFit: true
    })
    chart.tooltip({
      showMarkers: false
    })
    // chart.interval().position('name*number');
    chart.interval().position(`member*capacity`)
    chart.interaction('element-active')

    // 发送请求
    getChartData()

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

      <div id='container-wrapper'>
        <div id='container' />
        {/* <div id='groupName-wrapper'>
          <span id='groupName-box'>{username[0]}</span>
        </div> */}
      </div>
    </>
  )
}

{
  /* <Button type="primary" onClick={showOwnerEquity?.bind(this, text)}>设置成绩</Button> */
}
