/* 
  这里是封装的内容模块
*/
import React, {useEffect, useState} from 'react'
import {Cascader, Button, Space, message} from 'antd'
import {useHistory, useParams} from 'react-router'
import {Chart} from '@antv/g2'
import httpUtil from 'utils/request'
import './index.css'

// 收集状态
let stateObj = {}

// 定义一个图表
let chart

// 选项栏
const options = [
  {
    value: 1,
    label: '第一年',
    children: [
      {
        value: 1,
        label: '第一周期'
      },
      {
        value: 2,
        label: '第二周期'
      },
      {
        value: 3,
        label: '第三周期'
      },
      {
        value: 4,
        label: '第四周期'
      }
    ]
  },
  {
    value: 2,
    label: '第二年',
    children: [
      {
        value: 1,
        label: '第一周期'
      },
      {
        value: 2,
        label: '第二周期'
      },
      {
        value: 3,
        label: '第三周期'
      },
      {
        value: 4,
        label: '第四周期'
      }
    ]
  },
  {
    value: 3,
    label: '第三年',
    children: [
      {
        value: 1,
        label: '第一周期'
      },
      {
        value: 2,
        label: '第二周期'
      },
      {
        value: 3,
        label: '第三周期'
      },
      {
        value: 4,
        label: '第四周期'
      }
    ]
  },
  {
    value: 4,
    label: '第四年',
    children: [
      {
        value: 1,

        label: '第一周期'
      },
      {
        value: 2,
        label: '第二周期'
      },
      {
        value: 3,
        label: '第三周期'
      },
      {
        value: 4,
        label: '第四周期'
      }
    ]
  },
  {
    value: 5,
    label: '第五年',
    children: [
      {
        value: 1,

        label: '第一周期'
      },
      {
        value: 2,
        label: '第二周期'
      },
      {
        value: 3,
        label: '第三周期'
      },
      {
        value: 4,
        label: '第四周期'
      }
    ]
  },
  {
    value: 6,
    label: '第六年',
    children: [
      {
        value: 1,
        label: '第一周期'
      },
      {
        value: 2,
        label: '第二周期'
      },
      {
        value: 3,
        label: '第三周期'
      },
      {
        value: 4,
        label: '第四周期'
      }
    ]
  }
]

export const OwnerEquity = props => {
  // 将状态有关的东西存入stateObj
  stateObj = {...props.state}

  const params = useParams()
  const history = useHistory()

  // 设置状态
  const [yearAndPeriod, setYearAndPeriod] = useState([1, 1])
  const [hasNewTableData, setHasNewTableData] = useState(true)
  const [timer, setTimer] = useState(null)

  const renderChartData = data => {
    // 对表格数据做取整
    chart.data(
      data.map(item => ({...item, ownerBenifit: Math.round(item.ownerBenifit)}))
    )
    chart.render()
  }

  // 发送请求获取数据
  const getChartData = (yearAndPeriod, isShowMessage = 1) => {
    const formdata = new FormData()
    // 从url的参数中获取到小组名
    formdata.append('groupName', params.groupName)
    formdata.append('userUnique', 1)
    formdata.append('year', yearAndPeriod[0])
    formdata.append('period', yearAndPeriod[1])
    httpUtil.intermediateEquity(formdata).then(res => {
      // 通过isShowMessage 来实现，数据首次加载时不出现第几年数据获取成功的提示
      isShowMessage &&
        message.success(
          `第${yearAndPeriod[0]}年${yearAndPeriod[1]}期数据获取成功！`
        )
      // 请求成功说明有新的chart数据
      setHasNewTableData(true)
      // 渲染数据到图表上
      renderChartData(res.data)
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
      getChartData(yearAndPeriod)
    } else if (hasNewTableData === true) {
      message.warning('请选择其他年/周期！')
    }
  }

  // 展示上一期的所有者权益表
  const showLastPeriod = () => {
    const newYearAndPeriod = [...yearAndPeriod]
    newYearAndPeriod[1]--
    if (newYearAndPeriod[1] === 0) {
      newYearAndPeriod[0]--
      newYearAndPeriod[1] = 4
      if (newYearAndPeriod[0] === 0) {
        newYearAndPeriod[0] = 6
      }
    }
    // 发送请求
    debounceGetChartData(newYearAndPeriod)
    // 保存最新的yearAndPeriod
    setYearAndPeriod(newYearAndPeriod)
  }

  // 展示下一期的所有者权益表
  const showNextPeriod = () => {
    const newYearAndPeriod = [...yearAndPeriod]
    newYearAndPeriod[1]++
    if (newYearAndPeriod[1] === 5) {
      newYearAndPeriod[0]++
      newYearAndPeriod[1] = 1
      if (newYearAndPeriod[0] === 7) {
        newYearAndPeriod[0] = 1
      }
    }
    // 发送请求
    debounceGetChartData(newYearAndPeriod)
    // 保存最新的yearAndPeriod
    setYearAndPeriod(newYearAndPeriod)
  }

  // 下拉框变化时

  const onChange = value => {
    // 如果下拉框进行选择，就说明没有新的chart数据了
    setHasNewTableData(false)
    setYearAndPeriod(value)
  }

  useEffect(() => {
    // 渲染图表
    chart = new Chart({
      container: 'container',
      autoFit: true,
      height: 450
    })
    chart.tooltip({
      showMarkers: false
    })
    // chart.interval().position('name*number');
    chart.interval().position('userName*ownerBenifit')
    chart.interaction('element-active')

    // 发送请求
    getChartData(yearAndPeriod, 0)

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
          value={yearAndPeriod}
          placeholder='年/周期'
        />
        <Button type='primary' onClick={showOwnerEquity}>
          查看
        </Button>
        <Button type='primary' onClick={showLastPeriod}>
          上一期
        </Button>
        <Button type='primary' onClick={showNextPeriod}>
          下一期
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

{
  /* <Button type="primary" onClick={showOwnerEquity?.bind(this, text)}>设置成绩</Button> */
}
