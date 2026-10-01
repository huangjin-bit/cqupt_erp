import React, { useRef } from 'react'

import httpUtil from 'utils/request'
import '../materialPurchase/index.scss'

import { Input, Button, Modal, message } from 'antd';

import R1 from 'assets/images/R1.gif'
import R2 from 'assets/images/R2.gif'
import R3 from 'assets/images/R3.gif'
import R4 from 'assets/images/R4.gif'

function MaterialPurchase(props) {
  let datas = [{ materialName: 'R1', price: null }, { materialName: 'R2', price: null }, { materialName: 'R3', price: null }, { materialName: 'R4', price: null }]
  let [materials, setMaterials] = React.useState([])
  let [success, setSuccess] = React.useState(false)
  let [all, setAll] = React.useState(datas)

  React.useEffect(() => {
    httpUtil.materialBasic().then(res => {
      setMaterials(res.data)
      setSuccess(true)
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

  // 修改购买数量
  const numberChange = (money, materialName) => {
    if (money > 99999) {
      message.warn('超出最大输入数量')
    }
    else {
      let allData = all
      allData.forEach((item) => {
        if (item.materialName === materialName) {
          item.price = money
          setAll([...allData])
        }
      })
    }

  }

  // 购买原料
  const toBuy = (materialName) => {
    return () => {
      let names = []
      all.map(item => names.push(item.materialName))
      let index = names.indexOf(materialName)
      if (all[index].price * 1 > 0 && all[index].price !== '') {
        if (all[index].price * 1 % 1 === 0 || all[index].price * 1 == 0) {

          Modal.confirm({
            title: '是否要订购该原料?(提醒:订单一旦生成，不允许撤销)',
            okText: '确认',
            cancelText: '取消',
            onOk() {
              httpUtil.saveMaterialOrder({ materialName, number: all[index].price })
                .then((res) => {
                  if (!res.data) {
                    message.success('订购成功！')

                    setAll([...datas])
                  }
                  else message.error('订购失败')
                })
            },
            onCancel() {
            },
          });
        }
        else message.error(`购买数量只允许正整数`)
      }
      else message.error(`购买数量只允许正整数`)
    }

  }

  return <div className="userprepare-wrapper">
    {
      success === true ?
        <div className='contents'>
          <div className='content-item'>
            {materials.map((item, index) => {
              if (item.materialName === 'R1' || item.materialName === 'R2') {
                return <div key={item.materialName} className="item">
                  <div className='top'>
                    <img src={picChoose(item.materialName)} alt='产品' />
                    <div>
                      <span>库存数量:  {item.mnumber}</span><br /><br />
                      <span>单价:   {item.price}</span><br /><br />
                      <span>等待周期:   {item.delayTime}</span>
                    </div>
                  </div>
                  <div className='bottom'>
                    <span>购买数量:<Input className='item-input' value={all[index].price} onChange={e => numberChange(e.target.value, item.materialName)} /></span><br />
                    <Button className='item-button' type="primary" onClick={toBuy(item.materialName)}>购买</Button>
                  </div>
                </div>
              }
            })}
          </div>
          <div className='content-item'>
            {materials.map((item, index) => {
              if (item.materialName === 'R3' || item.materialName === 'R4') {
                return <div key={item.materialName} className="item">
                  <div className='top'>
                    <img src={picChoose(item.materialName)} alt='产品' />
                    <div>
                      <span>库存数量:  {item.mnumber}</span><br /><br />
                      <span>单价:   {item.price}</span><br /><br />
                      <span>等待周期:   {item.delayTime}</span>
                    </div>
                  </div>
                  <div className='bottom'>
                    <span>购买数量:<Input className='item-input' value={all[index].price} onChange={e => numberChange(e.target.value, item.materialName)} /></span><br />
                    <Button className='item-button' type="primary" onClick={toBuy(item.materialName)}>购买</Button>
                  </div>
                </div>
              }
            })}
          </div>
        </div>
        : ''
    }
  </div>
}

export default MaterialPurchase
