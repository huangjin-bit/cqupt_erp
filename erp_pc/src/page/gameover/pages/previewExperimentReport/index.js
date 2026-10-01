import React, {Fragment} from 'react'
import {nanoid} from 'nanoid'

import httpUtil from 'utils/request/index'
import {message} from 'antd'
import './index.scss'

function PreviewExperimentReport(props) {
  let [tableData, setTableData] = React.useState([])
  let [content, setContent] = React.useState('')
  let [group, setGroup] = React.useState()
  let [state, setState] = React.useState(false)
  let [errorMessage, setErrorMessage] = React.useState('')

  // 将HTML转换成字符串
  const changeHtml = str => {
    let strData = str
    strData = strData.split('<w:br/>')
    return strData
  }

  React.useEffect(() => {
    httpUtil.overViewReport().then(res => {
      if (
        !res.status &&
        (res.data.code == '11001' || res.data.code == '11000')
      ) {
        message.info(res.data.message)
        setErrorMessage(res.data.message)
        return
      } else if (!res.status) {
        setTableData(res.data.businesses)
        setContent(changeHtml(res.data.context))
        setGroup(res.data.group)
        setState(true)
      }
    })
  }, [])

  return (
    <div style={{width: '100%'}} className='userprepare-wrapper'>
      {state === true ? (
        <>
          <h1 className='report-h1'>ERP沙盘模拟运营实验报告</h1>
          <br />
          <h4 className='report-h4'>小组组长:&nbsp;{group.teamLeader}</h4>
          <h4 className='report-h4'>小组成员</h4>
          <table>
            <thead>
              <tr>
                <td>学号</td>
                <td>姓名</td>
                <td>职务</td>
                <td>成绩</td>
                <td>评级</td>
              </tr>
            </thead>
            <tbody>
              {group.members.map(item => {
                return (
                  <tr key={item.studentNo}>
                    <td>{item.studentNo}</td>
                    <td>{item.studentName}</td>
                    <td>{item.title}</td>
                    <td>{item.score}</td>
                    <td>{item.grade}</td>
                  </tr>
                )
              })}
            </tbody>
          </table>
          {tableData.map(item => {
            return (
              <Fragment key={nanoid()}>
                <h2 className='report-h2'>
                  第{item.year}年，第{item.period}期
                </h2>
                <table>
                  <thead>
                    <tr>
                      <td>资产</td>
                      <td>期初数</td>
                      <td>期末数</td>
                      <td>负债和所有者权益</td>
                      <td>期初数</td>
                      <td>期末数</td>
                    </tr>
                  </thead>
                  <tbody>
                    {item.rows.map(_item => {
                      return (
                        <tr key={nanoid()}>
                          <td>{_item.col1}</td>
                          <td>{_item.col2}</td>
                          <td>{_item.col3}</td>
                          <td>{_item.col4}</td>
                          <td>{_item.col5}</td>
                          <td>{_item.col6}</td>
                        </tr>
                      )
                    })}
                  </tbody>
                </table>
              </Fragment>
            )
          })}
          <h2 className='report-experience'>实验心得报告</h2>
          <div className='content-div'>
            {content.map(item => {
              return (
                <p style={{margin: '0px', padding: '0px'}} key={nanoid()}>
                  {item}
                </p>
              )
            })}
          </div>
        </>
      ) : (
        <p>{errorMessage}</p>
      )}
    </div>
  )
}

export default PreviewExperimentReport
