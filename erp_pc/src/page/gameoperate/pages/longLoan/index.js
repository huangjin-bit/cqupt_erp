import React from 'react'

import httpUtil from 'utils/request/index'
import './index.scss'

function LongLoan(props) {

  let [allData, setAllData] = React.useState([])

  React.useEffect(() => {
    httpUtil.endOneYear().then(res => {
      console.log(res);
      if (res.status === 0) setAllData(res.data)
    })
  }, [])

  return <div className="userprepare-wrapper">
    <table>
      <thead>
        <tr>
          <td>贷款编号</td>
          <td>一年内到期的长期负债</td>
          <td>贷款开始时间</td>
          <td>还款时间</td>
          <td>贷款金额</td>
        </tr>
      </thead>
      <tbody>
        {allData === [] ? "" :
          allData.map(item => {
            return <tr key={item.loanId} style={{ backgroundColor: 'white' }}>
              <td>{item.loanId}</td>
              <td>{item.loanTypeName}</td>
              <td>{item.beginYear}</td>
              <td>{item.endYear}</td>
              <td>{item.money}</td>
            </tr>
          })
        }
      </tbody>
    </table>
  </div>
}

export default LongLoan
