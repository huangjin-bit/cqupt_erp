import React from 'react'
import httpUtil from 'utils/request/index'

import './index.scss'
import warehouse from 'assets/images/warehouse.png'


function ProductInventory(props) {

  let [products, setProducts] = React.useState([])
  let [materials, setMaterials] = React.useState([])

  let [success, setSuccess] = React.useState(false)

  React.useEffect(() => {
    httpUtil.products().then(res => {
      if (res.status == 0) {
        setProducts(res.data)
      }
    })
    httpUtil.materials().then(res => {
      if (res.status == 0) {
        setMaterials(res.data)
      }
    })

    setSuccess(true)
  }, [])


  return <div className="userprepare-wrapper">
    <img className='ware-img' src={warehouse} alt="仓库" />
    {
      success ? (
        <>
          {products.map((item, index) => {
            return <div className={`ware-p${index + 1}`} >{item.pnumber}</div>
          })}
          {
            materials.map((item, index) => {
              return <div className={`ware-r${index + 1}`}>{item.mnumber}</div>
            })
          }
        </>)
        : ''
    }

  </div>
}

export default ProductInventory
