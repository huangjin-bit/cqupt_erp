import React from 'react'
import { Divider, Button } from 'antd'

import httpUtil from 'utils/request'

import './index.scss'

import m1 from 'assets/images/market_1.png'
import m2 from 'assets/images/market_2.png'
import m3 from 'assets/images/market_3.png'
import m4 from 'assets/images/market_4.png'
import m5 from 'assets/images/market_5.png'

function picChoose(market) {
  switch (market) {
    case '本地市场':
      return m1
    case '亚洲市场':
      return m5
    case '区域市场':
      return m4
    case '国内市场':
      return m2
    case '国际市场':
      return m3
    default:
      break
  }
}

function MarketManagement(props) {
  let [allData, changeState] = React.useState(0)
  let [success, changeSuccess] = React.useState(false)

  function getAllMarket() {
    httpUtil.getAllMarket()
      .then(res => {
        changeState({ ...res.data })
        changeSuccess(success = true)
      })

  }

  React.useEffect(() => {
    getAllMarket()
  }, [])

  function beginResearch(marketName) {
    return () => {
      httpUtil.beginResearch({ marketName })
        .then(() => {
          getAllMarket()
        })
    }
  }

  function controlResearch(marketName, status) {
    return () => {
      if (status === 1) {
        httpUtil.pauseDevelopingMarket({ marketName })
          .then(() => {
            getAllMarket()
          })
      }
      else {
        httpUtil.continueDevelopingMarket({ marketName })
          .then(() => {
            getAllMarket()
          })
      }

    }
  }

  // function controlMaintain(marketName, status) {
  //   return () => {
  //     if (status === 1) {
  //       httpUtil.pauseMaintainDevelopedMarket({ marketName })
  //         .then(() => {
  //           getAllMarket()
  //         })
  //     }
  //     else {
  //       httpUtil.continueMaintainDevelopedMarket({ marketName })
  //         .then(() => {
  //           getAllMarket()
  //         })
  //     }
  //   }
  // }

  return (
    <div className="userprepare-wrapper">
      {success ? allData.developedMarket !== [] ? <Divider className='hr' orientation="left">已开拓市场</Divider> : '' : ''}
      {
        success ?
          allData.developedMarket !== [] ?
            allData.developedMarket.map((item) => {
              return (<div className='div-box'>
                <img src={picChoose(item.marketName)} alt="市场" />
                <div className='box-item'><em>市场名称</em><span className='item-span'>{item.marketName}</span></div>
                <div className='box-item'><em>每期维护费用</em><span className='item-span'>{item.maintainCost}</span></div>
                <div className='box-item'><em>当前状态</em><span className='item-span'>{item.status === 1 ? '正在维护' : '暂停维护'}</span></div>
                <div className='box-item'><em>上期状态</em><span className='item-span'>{item.lastStatus === 1 ? '上期已维护' : '上期未维护'} </span></div>
                <div className='box-item'></div>
              </div>)
            }) : '' : ''
      }

      {success ? allData.developingMarket !== [] ? <Divider orientation="left">开拓中市场</Divider> : '' : ''}
      {
        success ?
          allData.developingMarket !== [] ?
            allData.developingMarket.map((item) => {
              return (<div className='div-box'>
                <img src={picChoose(item.marketName)} alt="市场" />
                <div className='box-item'><em>市场名称</em><span className='item-span'>{item.marketName}</span></div>
                <div className='box-item'><em>开拓周期</em><span className='item-span'>{item.researchPeriod}</span></div>
                <div className='box-item'><em>每期开拓费用</em><span className='item-span'>{item.researchCost}</span></div>
                <div className='box-item'><em>已完成周期</em><span className='item-span'>{item.finishedPeriod} </span></div>
                <div className='box-item'><em>开拓开始时间</em><span className='item-span'>{item.beginTime} </span></div>
                <div className='box-item'><em>当前状态</em><span className='item-span'>{item.status === 1 ? '正在开拓' : '暂停开拓'} </span></div>
                <div className='box-item'><Button className='button' type='primary' onClick={controlResearch(item.marketName, item.status)}>{item.status === 1 ? '暂停开拓' : '继续开拓'}</Button></div>
              </div>)
            }) : '' : ''
      }

      {success ? allData.undevelopMarket !== [] ? <Divider orientation="left">未开拓市场</Divider> : '' : ''}
      {
        success ?
          allData.undevelopMarket !== [] ?
            allData.undevelopMarket.map((item) => {
              return (<div className='div-box'>
                <img src={picChoose(item.marketName)} alt="市场" />
                <div className='box-item'><em>市场名称</em><span className='item-span'>{item.marketName}</span></div>
                <div className='box-item'><em>开拓所需总周期</em><span className='item-span'>{item.researchPeriod}</span></div>
                <div className='box-item'><em>每期开拓费用</em><span className='item-span'>{item.researchCost}</span></div>
                <div className='box-item'><em>每期维护费用</em><span className='item-span'>{item.maintainCost} </span></div>
                <div className='box-item'><Button className='button' type='primary' onClick={beginResearch(item.marketName)}>开始开拓</Button></div>
              </div>)
            }) : '' : ''
      }
    </div >
  )
}

export default MarketManagement
