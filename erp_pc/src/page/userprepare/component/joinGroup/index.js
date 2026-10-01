import React, { useCallback } from 'react'
import { withRouter } from 'react-router'
import { connect } from 'react-redux'
import { actionCreators } from '../../store/index.js'
import './index.scss'
import { PlusSquareOutlined } from '@ant-design/icons'
import pic_no from 'assets/images/pic_no.jpg'
function JoinGroup(props) {
  const JoinGroup = useCallback(
    groupName => {
      props.joinGroup({ groupName: groupName, userId: props.common.userMessage.userId })
    },
    [props]
  )

  return (
    <div className="join-group-wrapper">
      {props.userprepare.groupList.map(item => {
        if (item) {
          return (
            <div className="card-wrapper" key={item.groupName}>
              <div className="card">
                <div>
                  <p>{item.groupName}</p>
                  <p>第 {item.years} 年</p>
                  <p>
                    成员数：{item.joinNumber} / {item.userNumbers}
                  </p>
                </div>
              </div>
              <PlusSquareOutlined className="icon" onClick={JoinGroup.bind(this, item.groupName)} />
            </div>
          )
        } else {
          return (
            <div className="card-wrapper" key={Math.random()}>
              <div className="card">
                <img alt="example" src={pic_no} />
              </div>
              <PlusSquareOutlined className="disabled-icon" />
            </div>
          )
        }
      })}
    </div>
  )
}

const mapStateToProps = state => ({
  userprepare: state.userprepare,
  common: state.common,
})

const mapDispatchToProps = {
  joinGroup: actionCreators.joinGroup,
}

export default connect(mapStateToProps, mapDispatchToProps)(withRouter(JoinGroup))
