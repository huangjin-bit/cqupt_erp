/**
 * 验证码组件
 */

import React, { useState, useCallback } from 'react'
import './index.scss'
const url = `/erpn/user/VerifyCode`

function VerificationImg() {
  // 验证码时间戳，更新时间戳即可刷新验证码
  let [imgTimestamp, updateImgTimestamp] = useState(new Date().getTime())

  const imgChange = useCallback(() => {
    updateImgTimestamp(() => {
      return new Date().getTime()
    })
  }, [])

  return (
    <>
      <img
        onClick={imgChange}
        className="verificationImg"
        src={`${url}?timestamp=${imgTimestamp}`}
        alt="verificationImg"></img>
    </>
  )
}

export default VerificationImg
