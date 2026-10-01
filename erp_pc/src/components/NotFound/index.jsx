import React from 'react'
import pic from '../../assets/images/404.png'
import { Typography } from 'antd'
import './index.css'

export default function NotFound() {
  return (
    <div>
      <div className="notfound-wrapper">
        <img src={pic} alt="404" className="notfound-img"/>
        <Typography.Title level={3} >404~你的页面跑到火星去了哦~</Typography.Title>
      </div>

    </div>
  )
}
