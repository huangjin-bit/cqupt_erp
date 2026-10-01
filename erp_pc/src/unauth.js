import React from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router'
import { useState, useEffect, useRef } from 'react'
import { Form, Input, Button, InputNumber, Select } from 'antd'

function throtten(fn, delay) {
  let pre = new Date().getTime()
  return function () {
    let now = new Date().getTime()
    if (now - pre >= delay) {
      fn()
      pre = now
    }
  }
}
function Unauth(props) {
  const myref = useRef()
  const [content, setContent] = useState('')
  function handleChange1() {
    setContent(myref.current.value)
  }
  const handleChange = throtten(handleChange1, 1000)
  return (
    <div>
      <input type="text" onChange={() => handleChange()} ref={myref}></input>
    </div>
  )
}
export default Unauth
