import React, {useState, useEffect, useLayoutEffect} from 'react'
import * as echarts from 'echarts'
import httpUtil from 'utils/request'
import {Button, Select} from 'antd'
import './index.scss'
function MarketForecast(props) {
  let [marketValue, setValue] = useState('本地市场')
  const [quantity, setQuantity] = useState(0)
  const [price, setPrice] = useState(0)
  useEffect(() => {
    httpUtil.marketForectEchart({marketName: '本地市场'}).then(res => {
      setQuantity(res.data.quantity)
      setPrice(res.data.price)
    })
  }, [])

  useLayoutEffect(() => {
    var myChart = echarts.init(document.getElementById('chart1'))
    var option = {
      title: {
        text: `${marketValue}需求量预测图`
      },
      tooltip: {show: true, trigger: 'axis'},
      legend: {
        data: ['P1', 'P2', 'P3', 'P4'],
        right: 0
      },
      xAxis: {
        name: '时期（年）',
        type: 'category',
        data: ['1', '2', '3', '4', '5', '6']
      },
      yAxis: {
        name: '需求量',
        type: 'value'
      },
      series: [
        {
          name: 'P1',
          data: quantity[0],
          type: 'bar'
        },
        {
          name: 'P2',
          data: quantity[1],

          type: 'bar'
        },
        {
          name: 'P3',
          data: quantity[2],
          type: 'bar'
        },
        {
          name: 'P4',
          data: quantity[3],
          type: 'bar'
        }
      ]
    }

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option)

    window.addEventListener('resize', function () {
      myChart.resize()
    })

    var myChart2 = echarts.init(document.getElementById('chart2'))

    var option2 = {
      title: {
        text: `${marketValue}销售价格预测图`
      },
      tooltip: {show: true, trigger: 'axis'},

      legend: {
        data: ['P1', 'P2', 'P3', 'P4'],
        right: 0
      },

      xAxis: {
        name: '时期（年）',
        type: 'category',
        data: ['1', '2', '3', '4', '5', '6']
      },
      yAxis: {
        name: '价格',
        type: 'value'
      },
      series: [
        {
          name: 'P1',
          data: price[0],
          type: 'line'
        },
        {
          name: 'P2',

          data: price[1],

          type: 'line'
        },
        {
          name: 'P3',

          data: price[2],

          type: 'line'
        },
        {
          name: 'P4',

          data: price[3],

          type: 'line'
        }
      ]
    }
    // 使用刚指定的配置项和数据显示图表。
    myChart2.setOption(option2)
  }, [price])

  const selectValue = e => {
    setValue((marketValue = e))
  }
  const select = marketName => {
    return () => {
      httpUtil
        .marketForectEchart({marketName})
        .then(res => {
          setQuantity(res.data.quantity)
          setPrice(res.data.price)
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
  return (
    <div className='marketforecast-wrapper'>
      <div className='select-box'>
        <span>请选择市场：</span>
        <Select defaultValue='本地市场' onChange={e => selectValue(e)}>
          <Select.Option value='本地市场' key='1'>
            本地市场
          </Select.Option>
          <Select.Option value='区域市场' key='2'>
            区域市场
          </Select.Option>
          <Select.Option value='国内市场' key='3'>
            国内市场
          </Select.Option>
          <Select.Option value='亚洲市场' key='4'>
            亚洲市场
          </Select.Option>
          <Select.Option value='国际市场' key='5'>
            国际市场
          </Select.Option>
        </Select>
        <Button type='primary' onClick={select(marketValue)}>
          查询
        </Button>
      </div>
      <div className='chart-wrapper'>
        <div id='chart1'></div>
        <div id='chart2'></div>
      </div>
    </div>
  )
}

export default MarketForecast
