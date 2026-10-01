import React from 'react'

import httpUtil from 'utils/request/index'
import { useHistory } from 'react-router-dom'
import './index.scss'
import { message, Modal, Spin } from 'antd'
import img from '../../../../assets/images/pic_next.jpg'

function Time(props) {
  const history = useHistory()
  //控制加载和显示页面
  const [loading, setLoading] = React.useState(false)
  const [isVisible, setIsVisible] = React.useState(true)

  const next = async () => {
    Modal.confirm({
      visible: isVisible,
      title: '是否推进下一周期？',
      okText: '确认',
      cancelText: '取消',
      async onOk() {
        setLoading(true)
        httpUtil.forwardCycle().then(res => {
          if (!res.status) {
            switch (res.data.forwardStatus) {
              case 0:
                history.push('/gameover/checkJournalEntry')
                return message.error(res.data.message)
              case 1:
                history.push('/loading')
                history.push('/gameoperate/main')
                return message.success(res.data.message)
              case 2:
                message.error(res.data.message)
                break
              case 3:
                message.success(res.data.message || '游戏已结束')
                history.push('/gameover/checkJournalEntry')
                break
              default:
                message.error(res.data.message)
            }
          }
        }).finally(() => {
          setLoading(false)
        })
      },
      onCancel() {
        setIsVisible(false)
      }
    })
  }

  return (
    <div className='userprepare-wrapper'>
      {loading ? (
        <Spin size='large' />
      ) : (
        <img className='time-img' src={img} alt='进入下一周期' onClick={next} />
      )}
    </div>
  )
}

export default Time
