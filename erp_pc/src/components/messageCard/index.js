import React from 'react'
import './index.scss'
function messageCard(props) {
  return (
    <div className="message-card-wrapper">
      <div className="header"></div>
      <p className="content">{props.children}</p>
    </div>
  )
}

export default messageCard
