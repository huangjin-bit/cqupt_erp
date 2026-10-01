import React, { useEffect, useRef, useState } from 'react'
import { Divider } from 'antd'

import httpUtil from 'utils/request/index'
import ResearchItem from '../../components/ReseachItem/index'

import './index.scss'
import p1_f from '../../../../assets/images/p1_f.gif'
import p1_s from '../../../../assets/images/p1_s.gif'
import p2_f from '../../../../assets/images/p2_f.gif'
import p2_s from '../../../../assets/images/p2_s.gif'
import p3_f from '../../../../assets/images/p3_f.gif'
import p3_s from '../../../../assets/images/p3_s.gif'
import p4_f from '../../../../assets/images/p4_f.gif'
import p4_s from '../../../../assets/images/p4_s.gif'
import p1 from '../../../../assets/images/P1.png'
import p2 from '../../../../assets/images/P2.png'
import p3 from '../../../../assets/images/P3.png'
import p4 from '../../../../assets/images/P4.png'

function ProductResearch(props) {
  const [undevelopMsg, setUndevelopMsg] = useState([])
  const [developingMsg, setDevelopingMsg] = useState([])
  const [developedImg, setDevelopedImg] = useState([])

  const refUndevelopMsg = useRef()
  const refDevelopingMsg = useRef()

  refUndevelopMsg.current = undevelopMsg
  refDevelopingMsg.current = developingMsg
  //开始研发
  const startDevelop = product_name => {
    httpUtil
      .startDevelopProduct(product_name)
      .then(res => {
        if (!res.status) {
          const newUndevelopMsg = [...refUndevelopMsg.current]
          const delIndex = newUndevelopMsg.findIndex(item => {
            return item.productName === product_name
          })
          newUndevelopMsg.splice(delIndex, 1)
          setUndevelopMsg(newUndevelopMsg)
          getDevelopingProduct().then(res => setDevelopingMsg(res))
        }
      })
      .catch(err => {
        console.log(err)
      })
  }

  //继续研发
  const recoverDevelop = product_name => {
    httpUtil
      .recoveryDevelopProduct(product_name)
      .then(res => {
        if (!res.status) {
          const newDevelopingMsg = [...refDevelopingMsg.current]
          const changeIndex = newDevelopingMsg.findIndex(item => {
            return item.productName === product_name
          })
          newDevelopingMsg[changeIndex].handleClick = {
            clickEvent: product_name => {
              pauseDevelop(product_name)
            },
            value: '暂停研发',
          }
          newDevelopingMsg[changeIndex].status.value = '正在研发'
          setDevelopingMsg(newDevelopingMsg)
        }
      })
      .catch(err => console.log(err))
  }

  //暂停研发
  const pauseDevelop = product_name => {
    httpUtil
      .stopDevelopProduct(product_name)
      .then(res => {
        if (!res.status) {
          const newDevelopingMsg = [...refDevelopingMsg.current]
          const changeIndex = newDevelopingMsg.findIndex(item => {
            return item.productName === product_name
          })
          newDevelopingMsg[changeIndex].handleClick = {
            clickEvent: product_name => {
              recoverDevelop(product_name)
            },
            value: '继续研发',
          }
          newDevelopingMsg[changeIndex].status.value = '暂停研发'
          setDevelopingMsg(newDevelopingMsg)
        }
      })
      .catch(err => console.log(err))
  }

  //获取未研发的产品信息
  const getUndevelopsProduct = () => {
    return new Promise((resolve, reject) => {
      httpUtil.getUndevelopsProduct().then(res => {
        if (!res.status) {
          const data = Array.isArray(res.data) ? res.data : []
          let result = []
          result = data.map(item => {
            const productName = item.productName
            let img
            switch (productName) {
              case 'P1':
                img = p1
                break
              case 'P2':
                img = p2
                break
              case 'P3':
                img = p3
                break
              case 'P4':
                img = p4
                break
              default:
                break
            }
            return {
              img,
              productName,
              handleClick: {
                clickEvent: product_name => {
                  startDevelop(product_name)
                },
                value: '开始研发',
              },
              product_name: {
                title: '产品名称',
                value: productName,
              },
              researchPeriod: {
                title: '研发产品需要周期',
                value: item.researchPeriod,
              },
              researchCost: {
                title: '每期研发费用',
                value: item.researchCost,
              },
            }
          })
          resolve(result)
        } else {
          reject('获取为研发的产品信息失败')
        }
      })
    })
  }

  //获取研发中的产品信息
  const getDevelopingProduct = () => {
    return new Promise((resolve, reject) => {
      httpUtil.getDevelopingProduct().then(res => {
        if (!res.status) {
          const data = Array.isArray(res.data) ? res.data : []
          let result = []
          result = data.map(item => {
            const { productName, status } = item
            let img
            let handleClick = {}
            switch (productName) {
              case 'P1':
                img = p1
                break
              case 'P2':
                img = p2
                break
              case 'P3':
                img = p3
                break
              case 'P4':
                img = p4
                break
              default:
                break
            }
            handleClick = !status
              ? {
                clickEvent: product_name => {
                  recoverDevelop(product_name)
                },
                value: '继续研发',
              }
              : {
                clickEvent: product_name => {
                  pauseDevelop(product_name)
                },
                value: '暂停研发',
              }
            return {
              img,
              productName,
              handleClick,
              product_name: {
                title: '产品名称',
                value: productName,
              },
              researchPeriod: {
                title: '研发产品需要周期',
                value: item.researchPeriod,
              },
              researchCost: {
                title: '每期研发费用',
                value: item.researchCost,
              },
              finishedPeriod: {
                title: '已完成研发周期',
                value: item.finishedPeriod,
              },
              beginTime: {
                title: '开始研发时间',
                value: item.beginTime,
              },
              status: {
                title: '当前状态',
                value: status ? '正在研发' : '暂停研发',
              },
            }
          })
          resolve(result)
        } else {
          reject('获取研发中的产品信息失败')
        }
      })
    })
  }

  //获取研发完成的产品信息
  const getDevelopedProduct = () => {
    return new Promise((resolve, reject) => {
      httpUtil.getDevelopedProduct().then(res => {
        if (!res.status) {
          const data = Array.isArray(res.data) ? res.data : []
          const all_product = ['P1', 'P2', 'P3', 'P4']

          let developed_product = data.map(item => item.productName)
          let developed_img = developed_product.map(item => {
            switch (item) {
              case 'P1':
                return p1_s
              case 'P2':
                return p2_s
              case 'P3':
                return p3_s
              case 'P4':
                return p4_s
              default:
                return null
            }
          })

          let undeveloped_product = all_product.filter(
            item => developed_product.indexOf(item) === -1,
          )
          let undeveloped_img = undeveloped_product.map(item => {
            switch (item) {
              case 'P1':
                return p1_f
              case 'P2':
                return p2_f
              case 'P3':
                return p3_f
              case 'P4':
                return p4_f
              default:
                return null
            }
          })
          let result = developed_img.concat(undeveloped_img)
          resolve(result)
        } else {
          reject('获取研发完成的产品失败')
        }
      })
    })
  }

  useEffect(() => {
    Promise.all([
      getUndevelopsProduct(),
      getDevelopingProduct(),
      getDevelopedProduct(),
    ])
      .then(res => {
        setUndevelopMsg(res[0])
        setDevelopingMsg(res[1])
        setDevelopedImg(res[2])
      })
      .catch(err => console.log(err))
  }, [])

  return (
    <div className='productResearch-wrapper'>
      <Divider orientation='left' plain>
        研发完成的产品
      </Divider>
      <div className='product-img'>
        {developedImg.map((item, index) => (
          <img src={item} key={index} alt='' />
        ))}
      </div>
      <Divider orientation='left' plain>
        研发中的产品
      </Divider>
      <div>
        {developingMsg.map(item => {
          return (
            <ResearchItem
              product_msg={item}
              key={'developing' + item.productName}
            />
          )
        })}
      </div>
      <Divider orientation='left' plain>
        研发新的产品
      </Divider>
      <div>
        {undevelopMsg.map(item => {
          return (
            <ResearchItem
              product_msg={item}
              key={'undevelop' + item.productName}
            />
          )
        })}
      </div>
    </div>
  )
}

export default ProductResearch
