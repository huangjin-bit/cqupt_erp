import axios from 'axios'
const instance = axios.create({
  withCredentials: true
})
instance.interceptors.response.use((res) => {
  return res.data
})

//控制弹窗数量
let num = 0

export default class HttpUtil {

  static get(url, params = {}) {

    return new Promise((resolve, reject) => {
      instance
        .get(url, { params })
        .then(data => {
          // console.log(data)
          if (data.code === 10001) {
            if (num === 0) {
              alert('登录过期，请重新登陆')
              num++
            }
            window.location.href = '/erp/#/login'
            return
          }

          resolve(data)
        })
        .catch((err) => {
          reject({ err: JSON.stringify(err) })
        })
    })
  }


  static post(url, params = {}, boolean = true) {
    return new Promise((resolve, reject) => {
      instance
        .post(url, { ...params })
        .then(data => {
          if (data.code === 10001) {
            if (num === 0) {
              alert('登录过期，请重新登陆')
              num++
            }
            window.location.href = '/erp/#/login'
            return
          }
          resolve(data)
        })
        .catch((err) => {
          reject({ err: JSON.stringify(err) })
        })

    })
  }

  /** 创建一个new FormData()，用append()方法将数据写入formdata
  var formdata = new FormData()
  //可以通过append()方法来追加数据
  formdata.append('page', this.state.pageNumber)
  formdata.append('status', this.state.status)
  formdata.append('limit', this.state.pageSize)
*/
  //formdata格式post方法
  static post_formdata(url, formdata) {
    return new Promise((resolve, reject) => {
      instance
        .post(url, formdata, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        .then(data => {
          if (data.code === 10001) {
            if (num === 0) {
              alert('登录过期，请重新登陆')
              num++
            }
            window.location.href = '/erp/#/login'
            return
          }
          resolve(data)
        })
        .catch(err => {
          reject({err: JSON.stringify(err)})
        })

    })
  }
  //传统方法
  //   axios
  //   .post('/erpn/teacher/search', formdata)
  //   .then(function (response) {
  //     const listData = response.data.data
  //     console.log(response)
  //     // if (listData) {
  //     //   _this.setState({
  //     //     data: listData,
  //     //   })
  //     // }
  //   })
  //   .catch(function (error) {
  //     console.log(error)
  //   })
}

