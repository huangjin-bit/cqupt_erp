import React from 'react'
import { connect } from 'react-redux'
import './index.scss'
import loadingImg from 'assets/images/loading.gif'
function Loading(props) {
  return (
    <>
      <div className="loading-wrapper">
        <img src={loadingImg} class="loadImg" />
        <br />
        <span>加载中，请稍等</span>
      </div>
    </>
  )
}

const mapStateToProps = state => ({
  common: state.common,
})
export default connect(mapStateToProps, null)(Loading)
