import React from 'react'
// import {Divider} from 'antd'

import { Divider } from 'antd'
import './index.scss'

import httpUtil from 'utils/request/index'
// import R1 from '../../../../assets/images/R1.gif'
// import R2 from '../../../../assets/images/R2.gif'
// import R3 from '../../../../assets/images/R3.gif'
// import R4 from '../../../../assets/images/R4.gif'
// import product from '../../../../assets/images/productMade.png'

// function CompositionOfProduct(props) {

//   let [materials, setMaterials] = React.useState([])
//   let [success, setSuccess] = React.useState(false)

//   React.useEffect(() => {
//     httpUtil.materials().then(res => {
//       if (res.status == 0) {
//         setMaterials(res.data)
//         setSuccess(true)
//       }
//     })
//   }, [])





//   return <div className="userprepare-wrapper">
//     <Divider orientation="left">原料库存</Divider>
//     <div className='materials'>
//       {materials.map(item => {
//         return <div className='inside-item' key={item.materialName}>
//           <img src={picChoose(item.materialName)} alt='产品' /><br />
//           <span>x {item.mnumber}</span>
//         </div>
//       })}
//     </div>

//     <Divider orientation="left">产品构成</Divider>
//     <img src={product} className='product-img' />
//   </div>




import arrow from '../../../../assets/images/arrow.gif'
import P1 from '../../../../assets/images/P1.png'
import P2 from '../../../../assets/images/P2.png'
import P3 from '../../../../assets/images/P3.png'
import P4 from '../../../../assets/images/P4.png'
import R1 from '../../../../assets/images/R1.png'
import R2 from '../../../../assets/images/R2.png'
import R3 from '../../../../assets/images/R3.png'
import R4 from '../../../../assets/images/R4.png'

function CompositionOfProduct(props) {
  let [materials, setMaterials] = React.useState([])
  let [success, setSuccess] = React.useState(false)

  React.useEffect(() => {
    httpUtil.materials().then(res => {
      if (res.status == 0) {
        setMaterials(res.data)
        setSuccess(true)
      }
    })
  }, [])

  const picChoose = (name) => {
    switch (name) {
      case 'R1':
        return R1;
      case 'R2':
        return R2;
      case 'R3':
        return R3;
      case 'R4':
        return R4;
      default:
        break;
    }
  }

  const composition = [
    {
      productName: 'p1',
      productIcon: P1,
      cost: 1,
      material: [1, 0, 0, 0]
    },
    {
      productName: 'p2',
      productIcon: P2,
      cost: 2,
      material: [1, 1, 0, 0]
    },
    {
      productName: 'p1',
      productIcon: P3,
      cost: 3,
      material: [0, 2, 1, 0]
    },
    {
      productName: 'p1',
      productIcon: P4,
      cost: 4,
      material: [0, 2, 1, 1]
    }
  ]
  return (
    <div className='userprepare-wrapper'>
      <Divider orientation='left' plain>
        原料库存
      </Divider>
      <div className='my_storage'>




        {success === true ? materials.map(item => {
          return <div className='storage_item' key={item.materialName}>
            <img className='material_icon' src={picChoose(item.materialName)} alt='产品' />
            <div className='material_number'>x {item.mnumber}</div>
          </div>
        }) : ''}
      </div>
      <Divider orientation='left' plain>
        产品构成
      </Divider>
      <div className='composition'>
        {composition.map(item => (
          <div className='composition_item' key={item.productName}>
            <div className='composition_material'>
              <div className='material_item'>
                <img src={R1} className='item_img' />
                <div className='item_num'>{item.material[0]}</div>
              </div>
              <div className='material_item'>
                <img src={R2} className='item_img' />
                <div className='item_num'>{item.material[1]}</div>
              </div>
              <div className='material_item'>
                <img src={R3} className='item_img' />
                <div className='item_num'>{item.material[2]}</div>
              </div>
              <div className='material_item'>
                <img src={R4} className='item_img' />
                <div className='item_num'>{item.material[3]}</div>
              </div>
            </div>
            <div className='composition_arrow'>
              <img src={arrow} />
            </div>
            <div className='product'>
              <div className='product_detail'>
                <img className='product_img' src={item.productIcon} />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )

}

export default CompositionOfProduct
