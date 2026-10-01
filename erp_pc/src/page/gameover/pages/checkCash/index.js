import React from 'react'

import httpUtil from 'utils/request/index'
import './index.scss'
import atm from '../../../../assets/images/atm.jpg'

function CheckCash(props) {

  let [money, setMoney] = React.useState("")
  React.useEffect(() => {
    httpUtil.info().then(res => {
      if (res.status === 0) {
        setMoney(res.data.cash)
      }
    })
  }, [])

  return <div className="userprepare-wrapper">
    <img className='cash-img' src={atm} alt="ATM机" />
    <span className='cash-span'><em className='cash-em'>{money}</em>  万</span>
  </div>
}

export default CheckCash
