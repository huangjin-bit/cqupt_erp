import React from 'react'
import './index.scss'
function LogoHeader(props) {
  return (
    <>
      <div className="logo-header-wrapper">
        <div className="middle-title">{props.title}</div>
      </div>
    </>
  )
}

export default LogoHeader
