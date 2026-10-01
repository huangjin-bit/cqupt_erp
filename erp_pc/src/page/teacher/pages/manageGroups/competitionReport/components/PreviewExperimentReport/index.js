import React, {Fragment} from 'react'
import {nanoid} from 'nanoid'
import httpUtil from 'utils/request/index'
import {Drawer, Spin, message} from 'antd'
import './index.scss'

export function PreviewExperimentReport(props) {
  const [errorMessage, setErrorMessage] = React.useState('')
  const {
    visible,
    setVisible,
    groupName,
    data,
    setData,
    isHaveData,
    setIsHaveData
  } = props.state
  const onClose = () => {
    setVisible(false)
  }

  // 将HTML转换成字符串
  const changeHtml = str => {
    let strData = str
    if (strData) {
      strData = strData.split('<w:br/>')
    }
    return strData
  }

  React.useEffect(() => {
    // 发送请求
    const formData = new FormData()
    formData.append('groupName', groupName)
    // 如果数据已经有了，就不需要再请求了
    if (Reflect.has(data, groupName) === false) {
      setIsHaveData(false)
      httpUtil.viewReport(formData).then(res => {
        if (
          res.data[0]?.code &&
          (res.data[0].code == '11001' || res.data[0].code == '11000')
        ) {
          message.warning(res.data[0].message)
          setErrorMessage(res.data[0].message)
          setIsHaveData(false)
        } else {
          setData(data => {
            const totalData = {
              ...data
            }
            Reflect.set(totalData, groupName, res.data)
            return totalData
          })
          setIsHaveData(true)
        }
      })
    }
    return () => {}
  }, [])

  return (
    <>
      <Drawer
        title={`${groupName}--比赛报告预览`}
        placement='right'
        // placement="bottom"
        width='50%'
        // height="90%"
        onClose={onClose}
        visible={visible}>
        {isHaveData ? (
          data[groupName]?.map((report, index) => {
            return (
              <div style={{width: '100%'}} className='teacher-report-wrapper'>
                <h1 className='report-h1' id={`report-${index + 1}`}>
                  ERP沙盘模拟运营实验报告{index + 1}
                </h1>
                <br />
                <h4 className='report-h4'>
                  小组组长:&nbsp;{report.group.teamLeader}
                </h4>
                <h4 className='report-h4'>小组成员</h4>
                <table border='1'>
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
                    {report.group.members.map(item => {
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
                {report.businesses.map(item => {
                  return (
                    <Fragment key={nanoid()}>
                      <h2 className='report-h2'>
                        第{item.year}年，第{item.period}期
                      </h2>
                      <table border='1'>
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
                <div className='context-div'>
                  <p style={{margin: '0px', padding: '0px'}} key={nanoid()}>
                    {changeHtml(report.context)}
                  </p>
                </div>
              </div>
            )
          })
        ) : (
          <p className='report-otheInfo'>
            {errorMessage ? (
              errorMessage
            ) : (
              <Spin
                className='report-otheInfo-spin'
                tip='实验报告生成中，请耐心等待......'
              />
            )}
          </p>
        )}
      </Drawer>
    </>
  )
}
