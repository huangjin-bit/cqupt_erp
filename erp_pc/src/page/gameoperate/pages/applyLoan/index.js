import React from 'react'

import { Select, Button, Divider, Input, message, Modal } from 'antd'
import './index.scss'
import httpUtil from 'utils/request/index'
import Bank from 'assets/images/bank.png'
import BankEyes from 'assets/images/bankeyes.png'

//可能要修改
import { useHistory } from 'react-router-dom'

function ApplyLoan(props) {
  // 所有贷款的信息
  let [loanMessage, setLoanMessage] = React.useState([])
  // 所选择的贷款类型
  let [loanTypeName, setLoanTypeName] = React.useState('')
  // 所选择的贷款期限
  let [remainTime, setRemainTime] = React.useState()
  // 贷款可选择时间
  let [loanTime, setLoanTime] = React.useState([])
  // 所填写贷款金额
  let [loanMoney, setLoanMoney] = React.useState()
  //Modal贷款弹窗控制
  let [isVisible, setIsVisible] = React.useState(false)
  //现金数量
  let [cash, setCash] = React.useState('')
  // 眨眼状态
  let [eyes, setEyes] = React.useState(true)

  const history = useHistory()

  //获取贷款的可选期限
  const getTime = length => {
    let arr = []
    for (let i = 0; i < length; i++) {
      arr.push(i + 1)
    }
    return arr
  }

  //获取现金数量
  const getCash = () => {
    httpUtil.info().then(res => {
      if (res.status === 0) {
        setCash(res.data.cash * 1)
      }
    })
  }

  //贷款期限改变
  const selectChange = value => {
    setRemainTime(value)
  }

  //获取所有贷款信息
  const getLoanInfo = () => {
    httpUtil.loanInfo().then(res => {
      if (res.status === 0) {
        let arr = []
        res.data.forEach(item => {
          if (item.loanTypeName === '短期贷款') arr[0] = item
          else if (item.loanTypeName === '长期贷款') arr[1] = item
          else arr[2] = item
        })
        setLoanMessage(arr)

        if (loanTypeName === '长期贷款') {
          let arr1 = getTime(res.data[1].permitYear)
          arr1.shift()
          setLoanTime(arr1)
        } else if (loanTypeName === '短期贷款') {
          let arr1 = getTime(res.data[0].permitPeriod)
          setLoanTime(arr1)
        } else {
          let arr1 = getTime(res.data[2].permitPeriod)
          setLoanTime(arr1)
        }
      }
    })
  }

  //默认数据获取
  React.useEffect(() => {
    getLoanInfo()
    getCash()
    let eyes = setInterval(() => {
      setEyes(false)
      setTimeout(() => {
        setEyes(true)
      }, 300)
    }, 3000)
    return () => {
      clearInterval(eyes)
    }
  }, [])

  //贷款金额改变
  const moneyChange = (value, type) => {
    setLoanMoney(value)
  }

  //申请贷款
  const requestLoan = (loanTypeName, money, loanTime) => {
    return () => {

      if (loanTypeName === '长期贷款') {
        if ((money <= loanMessage[1].maxOfLoan && money > 0) || money == '') {
        } else {
          message.warn(`请输入正确的金额`)
          return
        }
      }
      else if (loanTypeName === '短期贷款') {
        if ((money <= loanMessage[0].maxOfLoan && money > 0) || money == ''){}
        else {
          message.warn(`请输入正确的金额`)
          return

        }
      } else {
        if (money > 0 || money == '') {
          if (money > 99999){
            message.warn('超出最大输入金额')
            return
          }
          else {}
        }
        else {
          message.warn( `请输入正确的金额`)
          return
        }
      }



      if (money % 1 != 0 || money == '')
        return message.error('请输入正确的金额')
      let maxMoney
      loanTypeName === '长期贷款'
        ? (maxMoney = loanMessage[1].maxOfLoan)
        : (maxMoney = loanMessage[0].maxOfLoan)
      if (loanTypeName === '长期贷款') {
        if (
          loanMessage[1].beginYear * 1 === 1 &&
          loanMessage[1].totalYear - loanMessage[1].currentYear >= 2 &&
          money * 1 <= maxMoney
        ) {
        } else {
          console.log(
            loanMessage[1].beginYear * 1 === 1,
            loanMessage[1].totalYear - loanMessage[1].currentYear >= 2,
            money * 1 <= maxMoney
          )

          message.error('无法申请长期贷款')
          return
        }
      }
      if (loanTypeName === '短期贷款' && money * 1 > maxMoney) {
        message.error('无法申请短期贷款')
        return
      }

      if (money && loanTime) {
        setIsVisible(false)
        httpUtil.loanApply({ loanTypeName, money, loanTime }).then(res => {
          if (res.status === 0) {
            setLoanMoney('')
            message.success(`申请贷款成功`)
            getLoanInfo()
            setCash(cash * 1 + money * 1)
          } else message.error(res.data)
        })
      }
    }
  }

  //根据贷款类型获取可以选择的贷款期限
  const getLoan = changeType => {
    if (changeType === '长期贷款') {
      let arr1 = getTime(loanMessage[1].permitYear)
      arr1.shift()
      setLoanTime(arr1)
    } else if (changeType === '短期贷款') {
      let arr1 = getTime(loanMessage[0].permitPeriod)
      setLoanTime(arr1)
    } else {
      let arr1 = getTime(loanMessage[2].permitPeriod)
      setLoanTime(arr1)
    }
    setLoanTypeName(changeType)
    setIsVisible(true)
  }

  const cancelLoan = () => {
    setIsVisible(false)
    setLoanMoney('')
  }

  //跳转贷款管理页面
  const seeLoan = () => {
    history.push('/gameoperate/loanManagement')
  }

  return (
    <>
      <div className='applyLoan-wrapper'>
        <img
          className='bank-img'
          src={eyes == true ? Bank : BankEyes}
          alt='银行'
          style={{ width: '100%' }}
        />
        <div className='long-loan' onClick={() => getLoan('长期贷款')}></div>
        <div className='short-loan' onClick={() => getLoan('短期贷款')}></div>
        <div className='high-loan' onClick={() => getLoan('高利贷')}></div>
        <div className='see-loan' onClick={seeLoan}></div>

        <div className='cash-div'>
          <span>
            现金:
            <em className='cash-number'>
              &nbsp;{Math.round(cash * 100) / 100}
            </em>
            &nbsp;万元
          </span>
        </div>

        <Modal
          className='loanModal'
          destroyOnClose
          visible={isVisible}
          title='申请贷款'
          footer={null}
          onCancel={cancelLoan}>
          {isVisible === false ? (
            ''
          ) : (
            <div className='middle-content'>
              <div className='middle-left'>
                <p>
                  贷款名称： <em className='loan-em'>{loanTypeName}</em>
                </p>
                <p className='year-profit'>
                  年利率：{' '}
                  <em className='loan-em'>
                    {loanTypeName == '长期贷款'
                      ? loanMessage[1].basicRate
                      : loanTypeName == '短期贷款'
                        ? loanMessage[0].basicRate
                        : loanMessage[2].basicRate}
                  </em>
                </p>
                <p>
                  贷款金额：{' '}
                  <Input
                    className='money-input'
                    onChange={e => moneyChange(e.target.value, loanTypeName)}
                    value={loanMoney}
                  />{' '}
                  万元
                </p>
                <span className='loan-span'>
                  贷款期限：{' '}
                  <Select
                    className='select'
                    size='middle'
                    onChange={value => selectChange(value)}>
                    {loanTime.map(item => {
                      return (
                        <Select.Option value={item} key={item}>
                          {item}
                        </Select.Option>
                      )
                    })}
                  </Select>
                  &nbsp;{loanTypeName == '长期贷款' ? `年` : '期'}
                </span>
              </div>
              <Divider />
              <div className='middle-right'>
                <span className='first-message'>
                  {loanTypeName == '长期贷款'
                    ? loanMessage[1].totalYear - loanMessage[1].currentYear >=
                      2 &&
                      loanMessage[1].beginYear * 1 === 1 &&
                      loanMessage[1].maxOfLoan > 0
                      ? `贷款金额必须在0.0万 - ${loanMessage[1].maxOfLoan}万之间！ 贷款期限必须为${loanMessage[1].permitYear}年之内！`
                      : '无法申请长期贷款！'
                    : loanTypeName == '短期贷款'
                      ? loanMessage[0].maxOfLoan > 0
                        ? `贷款金额必须在0.0万 - ${loanMessage[0].maxOfLoan}万之间！ 贷款期限必须为${loanMessage[0].permitPeriod}期之内！`
                        : '无法申请短期贷款！'
                      : `贷款金额必须大于0.0万 ！ 贷款期限必须为${loanMessage[2].permitPeriod}期之内！`}
                </span>
                <br />
                <span className='second-message'>
                  {loanTypeName == '短期贷款'
                    ? `说明：企业每期偿还利息，在贷款到期后归还本金`
                    : `说明：企业每期偿还利息，在贷款到期后归还本金`}
                </span>
              </div>
            </div>
          )}
          <Divider />
          <p className='card-bottom'>
            <Button
              className='bottom-btn'
              type='primary'
              onClick={requestLoan(loanTypeName, loanMoney, remainTime)}>
              申请
            </Button>
            <Button className='bottom-btn' type='primary' onClick={cancelLoan}>
              取消
            </Button>
          </p>
        </Modal>
      </div>
    </>
  )
}

export default ApplyLoan
