import React, { useState, useRef, useEffect } from 'react'
import { Divider } from 'antd';
import httpUtil from 'utils/request/index';

import ResearchItem from '../../components/ReseachItem/index'

import f1400 from '../../../../assets/images/1400_f.gif'
import s1400 from '../../../../assets/images/1400_s.gif'
import f9000 from '../../../../assets/images/9000_f.gif'
import s9000 from '../../../../assets/images/9000_s.gif'
import icon1400 from '../../../../assets/images/1400.gif'//8889-1
import icon9000 from '../../../../assets/images/9000.gif' //8889-2

import './index.scss'

function Authenticationmanagement(props) {
  const [undevelopMsg, setUndevelopMsg] = useState([]);
  const [developingMsg, setDevelopingMsg] = useState([]);
  const [developedImg, setDevelopedImg] = useState([]);

  const refUndevelopMsg = useRef()
  const refDevelopingMsg = useRef()

  refUndevelopMsg.current = undevelopMsg
  refDevelopingMsg.current = developingMsg

  //开始研发
  const startIsoDevelop = (iso_name) => {
    httpUtil.startDevelopIso(iso_name)
      .then(res => {
        if (!res.status) {
          const newUndevelopMsg = [...refUndevelopMsg.current];
          const delIndex = newUndevelopMsg.findIndex(item => {
            return item.productName === iso_name
          });
          newUndevelopMsg.splice(delIndex, 1);
          setUndevelopMsg(newUndevelopMsg);
          getDevelopingIso()
            .then(res => setDevelopingMsg(res))
        }
      })
      .catch(err => { console.log(err) })
  }

  //继续研发
  const recoverIsoDevelop = (iso_name) => {
    httpUtil.recoverDevelopIso(iso_name)
      .then(res => {
        if (!res.status) {
          const newDevelopingMsg = [...refDevelopingMsg.current];
          const changeIndex = newDevelopingMsg.findIndex(item => {
            return item.productName === iso_name
          })
          newDevelopingMsg[changeIndex].handleClick = { clickEvent: (iso_name) => { pauseIsoDevelop(iso_name) }, value: "暂停研发" };
          newDevelopingMsg[changeIndex].status.value = "正在研发"
          setDevelopingMsg(newDevelopingMsg)
        }
      })
      .catch(err => console.log(err))

  }

  //暂停研发
  const pauseIsoDevelop = (iso_name) => {
    httpUtil.stopDevelopProduct(iso_name)
      .then(res => {
        if (!res.status) {
          const newDevelopingMsg = [...refDevelopingMsg.current];
          const changeIndex = newDevelopingMsg.findIndex(item => {
            return item.productName === iso_name
          })
          newDevelopingMsg[changeIndex].handleClick = { clickEvent: (iso_name) => { recoverIsoDevelop(iso_name) }, value: "继续研发" };
          newDevelopingMsg[changeIndex].status.value = "暂停研发"
          setDevelopingMsg(newDevelopingMsg)
        }
      })
      .catch(err => console.log(err))

  }

  //获取未研发的ISO
  const getUndevelopIso = () => {
    return new Promise((resolve, reject) => {
      httpUtil.getUndevelopIso()
        .then(res => {
          console.log(res)
          if (!res.status) {
            const data = Array.isArray(res.data) ? res.data : [];
            let result = [];
            result = data.map(item => {
              const { isoName } = item;
              let img;
              switch (isoName) {
                case "PRODUCT1400质量认证": img = icon1400; break;
                case "PRODUCT9000质量认证": img = icon9000; break;
              }
              return {
                img,
                productName: isoName,
                handleClick: {
                  clickEvent: (iso_name) => { startIsoDevelop(iso_name) },
                  value: "开始研发"
                },
                iso_name: {
                  title: "ISO认证名称",
                  value: isoName
                },
                researchPeriod: {
                  title: "研发周期",
                  value: item.researchPeriod
                },
                researchCost: {
                  title: "每期开发费用",
                  value: item.researchCost
                },
                maintainCost: {
                  title: "每周期维护费用",
                  value: item.maintainCost
                }
              }
            })
            resolve(result)
          }
          else {
            reject("获取未研发的ISO失败")
          }
        })
    })
  }

  //获取正在研发的ISO
  const getDevelopingIso = () => {
    return new Promise((resolve, reject) => {
      httpUtil.getDevelopingIso()
        .then(res => {
          if (!res.status) {
            const data = Array.isArray(res.data) ? res.data : [];
            let result = [];
            result = data.map((item) => {
              const { isoName, status } = item;
              let img, handleClick = {};
              switch (isoName) {
                case "PRODUCT1400质量认证": img = icon1400; break;
                case "PRODUCT9000质量认证": img = icon9000; break;
              }
              handleClick = !status ?
                ({ clickEvent: (iso_name) => { recoverIsoDevelop(iso_name) }, value: "继续研发" }) :
                ({ clickEvent: (iso_name) => { pauseIsoDevelop(iso_name) }, value: "暂停研发" });

              return {
                img,
                productName: isoName,
                handleClick,
                iso_name: {
                  title: "ISO认证名称",
                  value: isoName
                },
                researchPeriod: {
                  title: "开发所需要周期",
                  value: item.researchPeriod
                },
                researchCost: {
                  title: "每期开发费用",
                  value: item.researchCost
                },
                finishedPrriod: {
                  title: "已完成开发周期",
                  value: item.finishedPeriod
                },
                status: {
                  title: "当前状态",
                  value: status ? "正在研发" : "暂停研发"
                }
              }

            })
            resolve(result)
          }
          else {
            reject("获取研发中的ISO失败")
          }
        })
    })
  }

  //获取研发完成的ISO
  const getDevelopedIso = () => {
    return new Promise((resolve, reject) => {
      httpUtil.getDevelopedIso()
        .then(res => {
          console.log(res)
          if (!res.status) {
            const data = Array.isArray(res.data) ? res.data : [];
            const allIso = ['PRODUCT9000质量认证', 'PRODUCT1400质量认证'];
            const developedIso = data.map(item => item.isoName)
            const developedIsoImg = developedIso.map(item => {
              switch (item) {
                case "PRODUCT1400质量认证": return s1400;
                case "PRODUCT9000质量认证": return s9000;
              }
            })

            const undevelopedIsoImg = allIso.filter(item => developedIso.indexOf(item) === -1)
            const undevelopIsoImg = undevelopedIsoImg.map(item => {
              switch (item) {
                case "PRODUCT1400质量认证": return f1400;
                case "PRODUCT9000质量认证": return f9000;
              }
            })

            resolve(developedIsoImg.concat(undevelopIsoImg))
          }
          else {
            reject("获取研发成功的iso失败")
          }
        })
    })
  }

  useEffect(() => {
    Promise.all([getUndevelopIso(), getDevelopingIso(), getDevelopedIso()])
      .then(res => {
        console.log(res)
        setUndevelopMsg(res[0])
        setDevelopingMsg(res[1])
        setDevelopedImg(res[2])
      })
      .catch(err => console.log(err))

  }, [])

  return (
    <div className="authentication-wrapper">
      <Divider orientation="left" plain>
        完成认证的ISO
      </Divider>
      <div className="iso-img">
        {developedImg.map((item, index) => <img src={item} key={index} />)}
      </div>
      <Divider orientation="left" plain>
        认证中的ISO
      </Divider>
      <div>
        {developingMsg.map(item => <ResearchItem product_msg={item} key={"developing" + item.productName} />)}
      </div>
      <Divider orientation="left" plain>
        认证新的ISO
      </Divider>
      <div>
        {
          undevelopMsg.map(item => {
            return <ResearchItem product_msg={item} key={"undevelop" + item.productName} />
          })
        }
      </div>
    </div>
  )
}

export default Authenticationmanagement
