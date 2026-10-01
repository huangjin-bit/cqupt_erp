import React from 'react'

import httpUtil from 'utils/request/index'
import './index.scss'
import img from 'assets/images/taxe.jpg'

function Taxes(props) {
  let [tax, setTax] = React.useState('')

  React.useEffect(() => {
    httpUtil.taxInfo().then(res => {
      if (res.status === 0) {
        setTax(res.data.tax)
      }
    })
  }, [])

  return (
    <div className='tax-wrapper'>
      <img className='taxe-img' src={img} alt='税务局' />
      <div className='taxe-div'>{`${Math.floor(tax * 100) / 100}`}</div>
    </div>
  )
}

export default Taxes
