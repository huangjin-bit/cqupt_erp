import React, {useState, useEffect, useRef} from 'react'
import {connect} from 'react-redux'
import {
  Modal,
  Form,
  Input,
  Select,
  Button,
  Collapse,
  Popover,
  Divider,
  message
} from 'antd'
import httpUtil from 'utils/request/index'
import './index.scss'

import madeFactoryImg from '../../../../assets/images/made.jpg'
import addLineIcon from '../../../../assets/images/addLine.svg'
import makingFactoryImg from '../../../../assets/images/making.jpg'
import rentFactoryImg from '../../../../assets/images/rent.jpg'
import handmadeIcon from '../../../../assets/images/handmade.jpg'
import semiAutomaticIcon from '../../../../assets/images/semiAutomatic.jpg'
import automaticIcon from '../../../../assets/images/automatic.jpg'
import softIcon from '../../../../assets/images/soft.jpg'

const {Option} = Select
const {Panel} = Collapse
const ReachableContext = React.createContext()
// 生产状态
const chooseBuildingStatus = [
  {
    value: 1,
    status: '全部'
  },
  {
    value: 2,
    status: '修建中'
  },
  {
    value: 3,
    status: '已建成'
  },
  {
    value: 4,
    status: '租用中'
  }
]
// 产地
const chooseBuildingPlace = [
  {
    value: '全部',
    place: '全部'
  },
  {
    value: '本地市场',
    place: '本地市场'
  },
  {
    value: '区域市场',
    place: '区域市场'
  },
  {
    value: '国际市场',
    place: '国际市场'
  },
  {
    value: '亚洲市场',
    place: '亚洲市场'
  },
  {
    value: '国内市场',
    place: '国内市场'
  }
]

// 厂房分类
const factoryType = [
  {
    type: '大厂房',
    periodRent: 2
  },
  {
    type: '小厂房',
    periodRent: 1
  }
]

// 生产线类型
const productLine = [
  {
    type: '手工生产线',
    value: '手工生产线'
  },
  {
    type: '半自动生产线',
    value: '半自动生产线'
  },
  {
    type: '全自动生产线',
    value: '全自动生产线'
  },
  {
    type: '柔性生产线',
    value: '柔性生产线'
  }
]

let allMadeFactory = [],
  allMakingFactory = [],
  allRentFactory = []
function WorkshopManagement(props) {
  const [isaddLineModalVisible, setIsaddLineModalVisible] = useState(false)
  const [isaddNewFactory, setIsaddNewFactory] = useState(false)
  const [isrentFactory, setIsrentFactory] = useState(false)
  const [madeFactory, setMadeFactory] = useState([])
  const [makingFactory, setMakingFactory] = useState([])
  const [rentFactory, setRentFactory] = useState([])
  const [myPlace, setMyPlace] = useState([])
  const [myProducts, setMyProducts] = useState([])
  const [addLineLoading, setAddlineLoading] = useState(false)
  const [addFactoryLoading, setAddFactoryLoading] = useState(false)
  const [rentFactoryLoading, setRentFactoryLoadin] = useState(false)
  const [addLineForm] = Form.useForm(),
    [addFactoryForm] = Form.useForm(),
    [rentFactoryForm] = Form.useForm(),
    [changeProductForm] = Form.useForm()
  const [modal, contextHolder] = Modal.useModal()
  const [productLineInfo, setProductLineInfo] = useState([])
  const [productLineNow, setProductLineNow] = useState()

  const queryStatus = useRef(1),
    queryPlace = useRef('全部')

  useEffect(() => {
    httpUtil.basicProductLine().then(res => {
      if (!res.status) setProductLineInfo(res.data)
    })
  }, [])

  const productLineChange = value => {
    let newArr = []
    productLineInfo.map(item => newArr.push(item.productLineType))
    let index = newArr.indexOf(value)
    setProductLineNow(productLineInfo[index])
  }

  // 获取已建成厂房信息
  const getMadeFactory = () => {
    return new Promise((resolve, reject) => {
      httpUtil
        .getMadeFactory()
        .then(res => {
          if (res.status === 0) resolve(res.data ? res.data : [])
          else return Promise.reject('获取已建成的厂房失败')
        })
        .catch(err => reject())
    })
  }

  // 获取正在修建厂房信息
  const getMakingFactory = () => {
    return new Promise((resolve, reject) => {
      httpUtil
        .getMakingFactory()
        .then(res => {
          if (res.status === 0) resolve(res.data ? res.data : [])
          else return Promise.reject('获取正在修建的厂房失败')
        })
        .catch(err => reject(err))
    })
  }

  // 获取正在修建厂房信息
  const getRentFactory = () => {
    return new Promise((resolve, reject) => {
      httpUtil
        .getRentFactory()
        .then(res => {
          if (res.status === 0) resolve(res.data ? res.data : [])
          else return Promise.reject('获取正在修建厂房失败')
        })
        .catch(err => reject(err))
    })
  }

  // 获取我的厂房地址
  const getFactoryPlace = () => {
    return new Promise((resolve, reject) => {
      httpUtil
        .getMarketList()
        .then(res => {
          if (!res.status) {
            let placeArr = res.data.map(item => item.marketName)
            resolve(placeArr)
          } else return Promise.reject('获取我的厂房地址失败')
        })
        .catch(err => reject(err))
    })
  }

  // 获取我的产品
  const getDevelopedProduct = () => {
    return new Promise((resolve, reject) => {
      httpUtil
        .getDevelopedProduct()
        .then(res => {
          if (!res.status) {
            let products = res.data.map(item => item.productName)
            resolve(products)
          } else return Promise.reject('获取我的产品失败')
        })
        .catch(err => reject(err))
    })
  }

  const allGetData = () => {
    Promise.all(
      [
        getMadeFactory(),
        getMakingFactory(),
        getRentFactory(),
        getDevelopedProduct()
      ].map(item =>
        item.catch(err => {
          message.error(String(err))
        })
      )
    )
      .then(res => {
        allMadeFactory = JSON.parse(JSON.stringify(res[0]))
        allMakingFactory = JSON.parse(JSON.stringify(res[1]))
        allRentFactory = JSON.parse(JSON.stringify(res[2]))
        setMadeFactory(JSON.parse(JSON.stringify(res[0])))
        setMakingFactory(JSON.parse(JSON.stringify(res[1])))
        setRentFactory(JSON.parse(JSON.stringify(res[2])))
        setMyProducts(JSON.parse(JSON.stringify(res[3])))
      })
      .catch(err => {})
  }

  // 获取租用厂房信息
  useEffect(() => {
    allGetData()
  }, [])

  // 查询
  const handleQuery = () => {
    const queryResMaking =
      queryStatus.current === 1 || queryStatus.current === 2
        ? queryPlace.current === '全部'
          ? allMakingFactory
          : allMakingFactory.filter(item => item.place === queryPlace.current)
        : []
    const queryResMade =
      queryStatus.current === 1 || queryStatus.current === 3
        ? queryPlace.current === '全部'
          ? allMadeFactory
          : allMadeFactory.filter(item => item.place === queryPlace.current)
        : []
    const queryResRent =
      queryStatus.current === 1 || queryStatus.current === 4
        ? queryPlace.current === '全部'
          ? allRentFactory
          : allRentFactory.filter(item => item.place === queryPlace.current)
        : []

    setMadeFactory([...queryResMade])
    setMakingFactory([...queryResMaking])
    setRentFactory([...queryResRent])
  }

  // 点击新建厂房
  const showAddFactoryModel = () => {
    if (!myPlace.length)
      {getFactoryPlace()
        .then(res => setMyPlace(res))
        .catch(err => message.warn(err))}
    setIsaddNewFactory(true)
  }

  // 点击租用厂房
  const showRentFactoryModel = () => {
    if (!myPlace.length)
      {getFactoryPlace()
        .then(res => setMyPlace(res))
        .catch(err => message.warn(err))}
    setIsrentFactory(true)
  }

  // 修改租用厂房类型
  const changeRentFactoryType = current => {
    let rent = factoryType.filter(item => item.type === current)
    rentFactoryForm.setFieldsValue({rentCost: rent[0].periodRent})
  }

  // 点击新增生产线获取已经生产的产品
  const addLine = e => {
    e.stopPropagation()
    const {factoryId, type} = e.target.dataset
    const currentItem =
      type === 'rentAdd'
        ? rentFactory.filter(item => item.factoryId === factoryId)[0]
        : madeFactory.filter(item => item.factoryId === factoryId)[0]
    if (currentItem.needPeriod && currentItem.needPeriod !== 0)
      return message.warn('等待周期未结束，无法增加生产线')
    if (currentItem.productLines.length >= currentItem.productLineCapacity)
      return message.warn('已达到最大生产线')
    addLineForm.setFieldsValue({factoryId, type})
    setIsaddLineModalVisible(true)
  }

  const closeModel = target => {
    if (target === 'addLine') setIsaddLineModalVisible(false)
    else if (target === 'addFactory') setIsaddNewFactory(false)
    else if (target === 'rentFactory') setIsrentFactory(false)
  }

  // 确认新增生产线
  const handleAddLineModalOk = async () => {
    try {
      setAddlineLoading(true)
      const {factoryId, type, productLineType, productName} =
        await addLineForm.validateFields()
      const data = {factoryId, productLineType, productName}
      const res = await httpUtil.addProductLine(data)
      if (!res.status) {
        if (type === 'rentAdd') {
          allRentFactory = [...allRentFactory].map(item => {
            if (item.factoryId === factoryId)
              item.productLines = JSON.parse(JSON.stringify(res.data))

            return item
          })
          const currentRentFactory = [...rentFactory].map(item => {
            if (item.factoryId === factoryId)
              item.productLines = JSON.parse(JSON.stringify(res.data))

            return item
          })
          setRentFactory(currentRentFactory)
        } else {
          allMadeFactory = [...allMadeFactory].map(item => {
            if (item.factoryId === factoryId)
              item.productLines = JSON.parse(JSON.stringify(res.data))

            return item
          })
          const currentMadeFactory = [...madeFactory].map(item => {
            if (item.factoryId === factoryId)
              item.productLines = JSON.parse(JSON.stringify(res.data))

            return item
          })
          setMadeFactory(currentMadeFactory)
        }
      }
    } catch (e) {
      message.warn(e)
    }
    setIsaddLineModalVisible(false)
    setAddlineLoading(false)
  }

  // 确认新增厂房
  const handleAddNewFactoryOk = async () => {
    try {
      setAddFactoryLoading(true)
      const formData = await addFactoryForm.validateFields()
      const data = {
        factoryType: formData.addFactoryType,
        place: formData.addFactoryPlace,
        payMode: 1
      }
      const res = httpUtil.addMakingFactory(data)
      if (res.status) return message.error('出错啦')
      const newMakingFactory = await getMakingFactory()
      allMakingFactory = JSON.parse(JSON.stringify(newMakingFactory))
      // console.log(allMakingFactory, newMakingFactory)
      setMakingFactory(JSON.parse(JSON.stringify(newMakingFactory)))
      setIsaddNewFactory(false)
      setAddFactoryLoading(false)

      allGetData()
    } catch (e) {
      setAddFactoryLoading(false)
      message.warn(e)
    }
  }

  // 确认租用厂房
  const handleRentFactoryOk = async () => {
    try {
      setRentFactoryLoadin(true)
      const formData = await rentFactoryForm.validateFields()
      const data = {
        factoryType: formData.rentFactoryType,
        place: formData.rentFactoryPlace,
        rentCost: formData.rentCost,
        needPeriod: formData.needPeriod
      }
      const res = await httpUtil.addRentFactory(data)
      if (res.status) return message.error('出错啦')
      const newRentFactory = await getRentFactory()
      allRentFactory = newRentFactory
      setRentFactory(newRentFactory)
      setIsrentFactory(false)
      setRentFactoryLoadin(false)
    } catch (e) {
      setRentFactoryLoadin(false)
      message.warn(e)
    }
  }

  // 暂停租用厂房弹窗
  const stopRentFactoryModal = e => {
    e.stopPropagation()
    const {factoryId} = e.target.dataset
    const index = rentFactory.findIndex(item => item.factoryId === factoryId)
    if (rentFactory[index].productLines.length)
      return message.error('有生产线，不能暂停租用~')
    const config = {
      title: '确定要暂停租用该厂房吗？',
      okText: '是',
      cancelText: '否',
      onOk: () => confirmStopRent(factoryId)
    }
    modal.confirm(config)
  }

  // 确认暂停租用
  const confirmStopRent = async factoryId => {
    const res = await httpUtil.stopRentFactory(factoryId)
    if (!res.status) {
      allRentFactory = allRentFactory.filter(
        item => item.factoryId !== factoryId
      )
      const currentFactory = [...rentFactory].filter(
        item => item.factoryId !== factoryId
      )
      setRentFactory(currentFactory)
    } else message.warn(res)
  }

  // 开始/暂停修建厂房
  const handleMaking = async e => {
    e.stopPropagation()
    const {factoryId} = e.target.dataset
    const index = allMakingFactory.findIndex(
      item => item.factoryId === factoryId
    )
    const currentStatus = allMakingFactory[index].status
    const res = currentStatus
      ? await httpUtil.stopMakingFactory(factoryId)
      : await httpUtil.keepMakingFactory(factoryId)
    if (!res.status) {
      allMakingFactory[index].status = !currentStatus
      const currentMakingFactory = [...makingFactory].map(item => {
        if (item.factoryId === factoryId) item.status = !item.status
        return item
      })
      setMakingFactory(currentMakingFactory)
    }
  }

  // 撤销正在安装的生产线
  const undo = e => {
    const {factoryId, lineid} = e.target.dataset
    const config = {
      title: '确定要撤销该生产线吗？',
      okText: '是',
      cancelText: '否',
      onOk: async () => {
        const res = await httpUtil.undoProductLine(lineid)
        if (!res.status) {
          const index = allMadeFactory.findIndex(
            item => item.factoryId === factoryId
          )
          console.log('index', index)
          allMadeFactory[index].productLines = allMadeFactory[
            index
          ].productLines.filter(
            item => Number(item.productLineId) !== Number(lineid)
          )
          const newMade = [...madeFactory]
          const currentIndex = newMade.findIndex(
            item => item.factoryId === factoryId
          )
          const newLine = madeFactory[currentIndex].productLines.filter(
            item => Number(item.productLineId) !== Number(lineid)
          )
          newMade[currentIndex].productLines = newLine
          setMadeFactory(newMade)
        }
      }
    }
    modal.confirm(config)
  }

  // 配置
  const lineConfig = async e => {
    let {factoryId, lineid, type, status} = e.target.dataset
    status = Number(status)
    let res, newStatus, start
    if (status === 0) {
      res = await httpUtil.suspendInstall(lineid)
      newStatus = 1
    } else if (status === 1) {
      res = await httpUtil.keepInstall(lineid)
      newStatus = 0
    } else if (status === 2) {
      res = await httpUtil.suspendMaking(lineid)
      newStatus = 3
    } else if (status === 3) {
      res = await httpUtil.keepMaking(lineid)
      newStatus = 2
    } else if (status === 4) {
      res = await httpUtil.startMaking(lineid)
      start = true
      newStatus = 2
    } else if (status === 5) {
      res = await httpUtil.suspendChange(lineid)
      newStatus = 6
    } else if (status === 6) {
      res = await httpUtil.keepChange(lineid)
      newStatus = 5
    } else return message.error('出错啦')

    if (!res.status) {
      if (start && res.data) return message.error(res.data.message)

      if (type === 'configMade') {
        const index = allMadeFactory.findIndex(
          item => item.factoryId === factoryId
        )
        allMadeFactory[index].productLines = allMadeFactory[
          index
        ].productLines.map(item => {
          if (item.productLineId === lineid) item.status = newStatus
          return item
        })
        let newCurrentMade = [...madeFactory]
        const currentIndex = newCurrentMade.findIndex(
          item => item.factoryId === factoryId
        )
        newCurrentMade[currentIndex].productLines = newCurrentMade[
          currentIndex
        ].productLines.map(item => {
          if (item.productLineId === lineid) item.status = newStatus
          return item
        })
        setMadeFactory(newCurrentMade)
      } else {
        const index = allRentFactory.findIndex(
          item => item.factoryId === factoryId
        )
        allRentFactory[index].productLines = allRentFactory[
          index
        ].productLines.map(item => {
          if (item.productLineId === lineid) item.status = newStatus
          return item
        })
        let newCurrentRent = [...rentFactory]
        const currentIndex = newCurrentRent.findIndex(
          item => item.factoryId === factoryId
        )
        newCurrentRent[currentIndex].productLines = newCurrentRent[
          currentIndex
        ].productLines.map(item => {
          if (item.productLineId === lineid) item.status = newStatus
          return item
        })
        setRentFactory(newCurrentRent)
      }
    } else message.error(String(res.message))
  }

  // 转产
  const changeProduct = async e => {
    const {factoryId, lineid, type} = e.target.dataset
    try {
      const {changeProduct} = await changeProductForm.validateFields()

      const data = {
        productLineId: lineid,
        productName: changeProduct
      }
      const res = await httpUtil.startChange(data)
      if (!res.status) {
        message.success('转产成功')
        if (type === 'changeMade') {
          const index = allMadeFactory.findIndex(
            item => item.factoryId === factoryId
          )
          allMadeFactory[index].productLines = allMadeFactory[
            index
          ].productLines.map(item => {
            if (item.productLineId === lineid)
              item = JSON.parse(JSON.stringify(res.data))
            return item
          })
          let newMade = [...madeFactory]
          const currentIndex = newMade.findIndex(
            item => item.factoryId === factoryId
          )
          newMade[currentIndex].productLines = newMade[
            currentIndex
          ].productLines.map(item => {
            if (item.productLineId === lineid)
              item = JSON.parse(JSON.stringify(res.data))
            return item
          })
          setMadeFactory(newMade)
        } else {
          const index = allRentFactory.findIndex(
            item => item.factoryId === factoryId
          )
          allRentFactory[index].productLines = allRentFactory[
            index
          ].productLines.map(item => {
            if (item.productLineId === lineid)
              item = JSON.parse(JSON.stringify(res.data))
            return item
          })
          let newRent = [...rentFactory]
          const currentIndex = newRent.findIndex(
            item => item.factoryId === factoryId
          )
          newRent[currentIndex].productLines = newRent[
            currentIndex
          ].productLines.map(item => {
            if (item.productLineId === lineid)
              item = JSON.parse(JSON.stringify(res.data))
            return item
          })
          setRentFactory(newRent)
        }
      }
    } catch (e) {
      message.warn(e)
    }
  }

  // 出售生产线
  const sellLine = async e => {
    const {
      linetype: productLineType,
      lineid: productLineId,
      factoryId,
      type
    } = e.target.dataset
    const config = {
      title: '确定要出售该生产线吗？',
      okText: '是',
      cancelText: '否',
      onOk: async () => {
        const res = await httpUtil.saleProductLine({
          productLineId,
          productLineType
        })
        if (!res.status) {
          if (type === 'sellMade') {
            const index = allMadeFactory.findIndex(
              item => item.factoryId === factoryId
            )
            let lines = allMadeFactory[index].productLines
            const index2 = lines.findIndex(
              item => item.productLineId === productLineId
            )
            lines.splice(index2, 1)
            handleQuery()
          } else {
            const index = allRentFactory.findIndex(
              item => item.factoryId === factoryId
            )
            let lines = allRentFactory[index].productLines
            const index2 = lines.findIndex(
              item => item.productLineId === productLineId
            )
            lines.splice(index2, 1)
            handleQuery()
          }
        } else message.warn(res)
      }
    }
    modal.confirm(config)
  }

  // 出售厂房
  const sellFactory = async e => {
    e.stopPropagation()
    const {factoryId} = e.target.dataset
    const index = allMadeFactory.findIndex(item => item.factoryId === factoryId)
    if (allMadeFactory[index].productLines.length !== 0)
      return message.warn('有生产线，不能出售')
    const config = {
      title: '确定要出售该厂房吗？',
      okText: '是',
      cancelText: '否',
      onOk: async () => {
        const res = await httpUtil.saleFactory({factoryId})
        if (!res.status) {
          allMadeFactory.splice(index, 1)
          handleQuery()
        }
      }
    }
    modal.confirm(config)
  }

  return (
    <ReachableContext.Provider value='Light'>
      <div className='userprepare-wrapper'>
        <div className='head_operate'>
          <div className='head_operate_left'>
            <div>
              请选择状态：
              <Select
                  defaultValue={1}
                  style={{width: '200px'}}
                  onChange={value => {
                  queryStatus.current = value
                  handleQuery()
                }}>
                {chooseBuildingStatus.map(item => (
                  <Option
                      value={item.value}
                      key={'chooseBuidingStatus' + item.value}>
                    {item.status}
                  </Option>
                ))}
              </Select>
            </div>
            <div className='choosePlace'>
              请选择市场：
              <Select
                  defaultValue='全部'
                  style={{width: '200px'}}
                  onChange={value => {
                  queryPlace.current = value
                  handleQuery()
                }}>
                {chooseBuildingPlace.map(item => (
                  <Option
                      value={item.value}
                      key={'chooseBuidingPlace' + item.value}>
                    {item.place}
                  </Option>
                ))}
              </Select>
            </div>
            {/* <Button type='primary' className='search' onClick={handleQuery}>
            查询
          </Button> */}
          </div>
          <div className='head_operate_right'>
            <Button
                type='primary'
                onClick={showAddFactoryModel}
                className='head_operate_btn'>
              新建厂房
            </Button>
            <Button
                type='primary'
                className='rent head_operate_btn'
                onClick={showRentFactoryModel}>
              租用厂房
            </Button>
          </div>
        </div>
        <Collapse ghost='true'>
          {madeFactory.map(item => {
            return (
              <Panel
                  key={'madeFactory' + item.factoryId}
                  header={
                  <div className='managementItem'>
                    <img src={madeFactoryImg} alt='' className='status_icon' />
                    <div className='management_msg'>
                      <span className='title'>状态</span>
                      <span className='text'>已建成</span>
                    </div>
                    <div className='management_msg'>
                      <span className='title'>厂房编号</span>
                      <span className='text'>{item.factoryId}</span>
                    </div>
                    <div className='management_msg'>
                      <span className='title'>所在市场</span>
                      <span className='text'>{item.place}</span>
                    </div>
                    <div className='management_msg'>
                      <span className='title'>厂房类型</span>
                      <span className='text'>{item.factoryType}</span>
                    </div>
                    <div className='management_msg'>
                      <span className='title'>残值</span>
                      <span className='text'>{item.sellPrice}</span>
                    </div>
                    <div className='management_msg'>
                      <span className='title'>开工时间</span>
                      <span className='text'>{item.beginTime}</span>
                    </div>
                    <div className='management_msg'>
                      <span className='title'>完工时间</span>
                      <span className='text'>{item.finishTime}</span>
                    </div>
                    <div className='management_msg'>
                      <span className='title'>生产线数量</span>
                      <span className='text'>
                        {item.productLines.length}/{item.productLineCapacity}
                        <img
                            alt=''
                            onClick={addLine}
                            src={addLineIcon}
                            data-factory-id={item.factoryId}
                            data-type='madeAdd'
                            className='addLine'
                        />
                      </span>
                    </div>
                    <div
                        className='management_btn'
                        data-factory-id={item.factoryId}
                        onClick={sellFactory}>
                      出售
                    </div>
                  </div>
                }>
                <div>
                  {item.productLines.length !== 0 ? (
                    item.productLines.map(item_ => {
                      return (
                        <div
                            className='management_detail'
                            key={item_.productLineId}>
                          <img
                              alt=''
                              src={
                              item_.productLineType === '手工生产线'
                                ? handmadeIcon
                                : item_.productLineType === '半自动生产线'
                                ? semiAutomaticIcon
                                : item_.productLineType === '全自动生产线'
                                ? automaticIcon
                                : softIcon
                            }
                              className='type'
                          />
                          <div>{item_.productLineType}</div>
                          <div>
                            <span className='management_detail_msg'>
                              状态：
                            </span>
                            {Number(item_.status) === 0
                              ? '安装中'
                              : Number(item_.status) === 1
                              ? '暂停安装'
                              : Number(item_.status) === 2
                              ? '生产中'
                              : Number(item_.status) === 3
                              ? '暂停生产'
                              : Number(item_.status) === 4
                              ? '待生产'
                              : Number(item_.status) === 5
                              ? '转产'
                              : '暂停转产'}
                          </div>
                          <div>
                            <span className='management_detail_msg'>
                              {Number(item_.status) === 5 ||
                              Number(item_.status) === 6
                                ? '已转产周期：'
                                : '已生产周期：'}
                            </span>
                            {Number(item_.status) === 5 ||
                            Number(item_.status) === 6
                              ? item_.finishPeriod
                              : item_.productFinishPeriod}
                          </div>
                          <div>
                            <span className='management_detail_msg'>
                              {Number(item_.status) === 0 ||
                              Number(item_.status) === 1
                                ? '将生产产品'
                                : Number(item_.status) === 2
                                ? '生产中产品'
                                : Number(item_.status) === 3
                                ? '暂停生产产品'
                                : Number(item_.status) === 4
                                ? '待生产产品'
                                : Number(item_.status) === 5
                                ? '转产中产品'
                                : '暂停转产产品'}
                              ：
                            </span>
                            {item_.productName}
                          </div>
                          <div>
                            {(Number(item_.status) === 0 ||
                              Number(item_.status) === 1) &&
                            Number(item_.finishPeriod) === 0 ? (
                              <span
                                  className='detail_btn'
                                  data-lineid={item_.productLineId}
                                  data-factory-id={item.factoryId}
                                  onClick={undo}>
                                撤销
                              </span>
                            ) : (
                              ''
                            )}
                            <Popover
                                content={
                                <div>
                                  {Number(item_.status) === 2 ||
                                  Number(item_.status) === 3 ||
                                  Number(item_.status) === 4 ? (
                                    <div>
                                      离产品生产完成还差
                                      {item_.producePeriod -
                                        item_.productFinishPeriod}
                                      周期
                                    </div>
                                  ) : Number(item_.status) === 5 ||
                                    Number(item_.status) === 6 ? (
                                    <>
                                      <div>
                                        离转产生产产品 {item_.productName}{' '}
                                      </div>
                                      <div>
                                        还差{' '}
                                        {item_.changePeriod -
                                          item_.finishPeriod}{' '}
                                        周期
                                      </div>
                                      <div>
                                        转产每期所需费用：{item_.changeCost}
                                      </div>
                                    </>
                                  ) : (
                                    <>
                                      <div>
                                        离完成还差{' '}
                                        {item_.setupPeriod - item_.finishPeriod}{' '}
                                        周期
                                      </div>
                                      <div>需安装周期：{item_.setupPeriod}</div>
                                      <div>
                                        安装每期所需费用：
                                        {item_.setupPeriodPrice}
                                      </div>
                                    </>
                                  )}

                                  <Divider />
                                  <div>生产效率：{item_.producePeriod}</div>
                                  <div>残值数额：{item_.sellPrice}</div>
                                  <div>
                                    生产线开始使用后每期所需维护费用：
                                    {item_.mainCost}
                                  </div>
                                  <div>
                                    生产线开始使用后每期的折旧费用：
                                    {item_.depreciation}
                                  </div>
                                  <div>
                                    出售生产线后资金到账的账期：
                                    {item_.delayTime}
                                  </div>
                                </div>
                              }>
                              <span type='primary' className='detail_btn'>
                                查看
                              </span>
                            </Popover>
                            {Number(item_.status) === 4 ? (
                              <Popover
                                  content={
                                  <Form
                                      form={changeProductForm}
                                      className='change_product_form'>
                                    <Form.Item
                                        name='changeProduct'
                                        label='转产产品：'
                                        rules={[
                                        {
                                          required: true,
                                          message: '请选择转产产品'
                                        }
                                      ]}>
                                      <Select style={{width: '100px'}}>
                                        {myProducts.map(item => (
                                          <Option
                                              value={item}
                                              key={'changeProducts' + item}>
                                            {item}
                                          </Option>
                                        ))}
                                      </Select>
                                    </Form.Item>
                                    <div
                                        className='change_btn'
                                        data-type='changeMade'
                                        data-factory-id={item.factoryId}
                                        data-lineid={item_.productLineId}
                                        onClick={changeProduct}>
                                      开始转产
                                    </div>
                                  </Form>
                                }>
                                <span className='detail_btn'>转产</span>
                              </Popover>
                            ) : (
                              <span className='detail_btn unactive'>转产</span>
                            )}

                            <Popover
                                content={
                                <span
                                    onClick={lineConfig}
                                    className='config_btn'
                                    data-type='configMade'
                                    data-factory-id={item.factoryId}
                                    data-status={item_.status}
                                    data-lineid={item_.productLineId}>
                                  {Number(item_.status) === 0
                                    ? '暂停安装'
                                    : Number(item_.status) === 1
                                    ? '开始安装'
                                    : Number(item_.status) === 2
                                    ? '暂停生产'
                                    : Number(item_.status) === 3
                                    ? '继续生产'
                                    : Number(item_.status) === 4
                                    ? '开始生产'
                                    : Number(item_.status) === 5
                                    ? '暂停转产'
                                    : '继续转产'}
                                </span>
                              }>
                              <span className='detail_btn'>配置</span>
                            </Popover>
                            <span
                                data-lineid={item_.productLineId}
                                data-linetype={item_.productLineType}
                                data-factory-id={item.factoryId}
                                data-type='sellMade'
                                onClick={
                                Number(item_.status) === 4 ? sellLine : null
                              }
                                className={
                                Number(item_.status) === 4
                                  ? 'detail_btn'
                                  : 'unactive detail_btn'
                              }>
                              出售
                            </span>
                          </div>
                        </div>
                      )
                    })
                  ) : (
                    <div className='no_productline_tips'>暂无生产线信息</div>
                  )}
                </div>
              </Panel>
            )
          })}
        </Collapse>
        <Collapse ghost='true'>
          {rentFactory.map(item => (
            <Panel
                key={'rentFacroty' + item.factoryId}
                header={
                <div className='managementItem'>
                  <img alt='' src={rentFactoryImg} className='status_icon' />
                  <div className='management_msg'>
                    <span className='title'>状态</span>
                    <span className='text'>租用中</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>厂房编号</span>
                    <span className='text'>{item.factoryId}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>所在市场</span>
                    <span className='text'>{item.place}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>厂房类型</span>
                    <span className='text'>{item.factoryType}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>每期需要缴纳的租金</span>
                    <span className='text'>{item.rentCost}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>残值</span>
                    <span className='text'>{item.sellPrice}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>
                      {item.needPeriod === 0 ? '完工时间' : '等待使用周期'}
                    </span>
                    <span className='text'>
                      {item.needPeriod === 0 ? '租赁厂房可用' : item.needPeriod}
                    </span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>生产线数量</span>
                    <span className='text'>
                      {item.productLines.length}/{item.productLineCapacity}
                      <img
                          alt=''
                          data-factory-id={item.factoryId}
                          data-type='rentAdd'
                          onClick={addLine}
                          src={addLineIcon}
                          className='addLine'
                      />
                    </span>
                  </div>
                  <div
                      className='management_btn'
                      data-factory-id={item.factoryId}
                      onClick={stopRentFactoryModal}>
                    暂停租用
                  </div>
                </div>
              }>
              <div>
                {item.productLines.length !== 0 ? (
                  item.productLines.map(item_ => {
                    return (
                      <div
                          className='management_detail'
                          key={'line' + item.productLineId}>
                        <img
                            alt=''
                            src={
                            item_.productLineType === '手工生产线'
                              ? handmadeIcon
                              : item_.productLineType === '半自动生产线'
                              ? semiAutomaticIcon
                              : item_.productLineType === '全自动生产线'
                              ? automaticIcon
                              : softIcon
                          }
                            className='type'
                        />
                        <div>{item_.productLineType}</div>
                        <div>
                          <span className='management_detail_msg'>状态：</span>
                          {Number(item_.status) === 0
                            ? '安装中'
                            : Number(item_.status) === 1
                            ? '暂停安装'
                            : Number(item_.status) === 2
                            ? '生产中'
                            : Number(item_.status) === 3
                            ? '暂停生产'
                            : Number(item_.status) === 4
                            ? '待生产'
                            : Number(item_.status) === 5
                            ? '转产'
                            : '暂停转产'}
                        </div>
                        <div>
                          <span className='management_detail_msg'>
                            {Number(item_.status) === 5 ||
                            Number(item_.status) === 6
                              ? '已转产周期：'
                              : '已生产周期：'}
                          </span>
                          {Number(item_.status) === 5 ||
                          Number(item_.status) === 6
                            ? item_.finishPeriod
                            : item_.productFinishPeriod}
                        </div>
                        <div>
                          <span className='management_detail_msg'>
                            {Number(item_.status) === 0 ||
                            Number(item_.status) === 1
                              ? '将生产产品'
                              : Number(item_.status) === 2
                              ? '生产中产品'
                              : Number(item_.status) === 3
                              ? '暂停生产产品'
                              : Number(item_.status) === 4
                              ? '待生产产品'
                              : Number(item_.status) === 5
                              ? '转产中产品'
                              : '暂停转产产品'}
                            ：
                          </span>
                          {item_.productName}
                        </div>
                        <div>
                          <Popover
                              content={
                              <div>
                                {Number(item_.status) === 2 ||
                                Number(item_.status) === 3 ||
                                Number(item_.status) === 4 ? (
                                  <div>
                                    离产品生产完成还差
                                    {item_.producePeriod -
                                      item_.productFinishPeriod}
                                    周期
                                  </div>
                                ) : Number(item_.status) === 5 ||
                                  Number(item_.status) === 6 ? (
                                  <>
                                    <div>
                                      离转产生产产品 {item_.productName} 周期
                                    </div>
                                    <div>
                                      还差{' '}
                                      {item_.changePeriod - item_.finishPeriod}{' '}
                                      周期
                                    </div>
                                    <div>
                                      转产每期所需费用：{item_.changeCost}
                                    </div>
                                  </>
                                ) : (
                                  <>
                                    <div>
                                      离完成还差{' '}
                                      {item_.setupPeriod - item_.finishPeriod}{' '}
                                      周期
                                    </div>
                                    <div>需安装周期：{item_.setupPeriod}</div>
                                    <div>
                                      安装每期所需费用：{item_.setupPeriodPrice}
                                    </div>
                                  </>
                                )}
                                <Divider />
                                <div>生产效率：{item_.producePeriod}</div>
                                <div>残值数额：{item_.sellPrice}</div>
                                <div>
                                  生产线开始使用后每期所需维护费用：
                                  {item_.mainCost}
                                </div>
                                <div>
                                  生产线开始使用后每期的折旧费用：
                                  {item_.depreciation}
                                </div>
                                <div>
                                  出售生产线后资金到账的账期：{item_.delayTime}
                                </div>
                              </div>
                            }>
                            <span className='detail_btn'>查看</span>
                          </Popover>
                          {Number(item_.status) === 4 ? (
                            <Popover
                                content={
                                <Form
                                    form={changeProductForm}
                                    className='change_product_form'>
                                  <Form.Item
                                      name='changeProduct'
                                      label='转产产品：'
                                      rules={[
                                      {
                                        required: true,
                                        message: '请选择转产产品'
                                      }
                                    ]}>
                                    <Select style={{width: '100px'}}>
                                      {myProducts.map(item => (
                                        <Option
                                            value={item}
                                            key={'rentChangeProducts' + item}>
                                          {item}
                                        </Option>
                                      ))}
                                    </Select>
                                  </Form.Item>
                                  <div
                                      className='change_btn'
                                      data-type='changeRent'
                                      data-factory-id={item.factoryId}
                                      data-lineid={item_.productLineId}
                                      onClick={changeProduct}>
                                    开始转产
                                  </div>
                                </Form>
                              }>
                              <span className='detail_btn'>转产</span>
                            </Popover>
                          ) : (
                            <span className='detail_btn unactive'>转产</span>
                          )}
                          <Popover
                              content={
                              <span
                                  onClick={lineConfig}
                                  className='config_btn'
                                  data-type='configRent'
                                  data-factory-id={item.factoryId}
                                  data-status={item_.status}
                                  data-lineid={item_.productLineId}>
                                {Number(item_.status) === 0
                                  ? '暂停安装'
                                  : Number(item_.status) === 1
                                  ? '开始安装'
                                  : Number(item_.status) === 2
                                  ? '暂停生产'
                                  : Number(item_.status) === 3
                                  ? '继续生产'
                                  : Number(item_.status) === 4
                                  ? '开始生产'
                                  : Number(item_.status) === 5
                                  ? '暂停转产'
                                  : '继续转产'}
                              </span>
                            }>
                            <span className='detail_btn'>配置</span>
                          </Popover>
                          <span
                              data-lineid={item_.productLineId}
                              data-linetype={item_.productLineType}
                              data-factory-id={item.factoryId}
                              data-type='sellRent'
                              onClick={
                              Number(item_.status) === 4 ? sellLine : null
                            }
                              className={
                              Number(item_.status) === 4
                                ? 'detail_btn'
                                : 'unactive detail_btn'
                            }>
                            出售
                          </span>
                        </div>
                      </div>
                    )
                  })
                ) : (
                  <div className='no_productline_tips'>暂无生产线信息</div>
                )}
              </div>
            </Panel>
          ))}
        </Collapse>
        <div className='rent_factoy_collapse'>
          {makingFactory.map(item => (
            <Panel
                key={'makingFactory' + item.factoryId}
                header={
                <div className='managementItem rent_item'>
                  <img alt='' src={makingFactoryImg} className='status_icon' />
                  <div className='management_msg'>
                    <span className='title'>状态</span>
                    <span className='text'>
                      {item.status ? '修建中' : '暂停中'}
                    </span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>厂房编号</span>
                    <span className='text'>{item.factoryId}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>所在市场</span>
                    <span className='text'>{item.place}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>厂房类型</span>
                    <span className='text'>{item.factoryType}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>建造厂房的付款方式</span>
                    <span className='text'>
                      {Number(item.payMode) === 0
                        ? '按期支付建造资金'
                        : '一次性付清'}
                    </span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>开工时间</span>
                    <span className='text'>{item.beginTime}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>总建造周期</span>
                    <span className='text'>{item.makePeriod}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>已完成建造周期</span>
                    <span className='text'>{item.finishedPeriod}</span>
                  </div>
                  <div className='management_msg'>
                    <span className='title'>能容纳生产线</span>
                    <span className='text'>{item.productLineCapacity}</span>
                  </div>
                  <div
                      className='management_btn'
                      data-factory-id={item.factoryId}
                      onClick={handleMaking}>
                    {item.status ? '暂停修建' : '开始修建'}
                  </div>
                </div>
              }></Panel>
          ))}
        </div>
        <Modal
            className='addLineModal'
            title='新增生产线'
            visible={isaddLineModalVisible}
            onOk={handleAddLineModalOk}
            confirmLoading={addLineLoading}
            onCancel={() => {
            closeModel('addLine')
          }}
            okText='确认新建'
            cancelText='取消'>
          <Form form={addLineForm} labelCol={{span: 8}} wrapperCol={{span: 16}}>
            <Form.Item name='factoryId' hidden={true}>
              <Input disabled style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item name='type' hidden={true}>
              <Input disabled style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item
                name='productLineType'
                label='生产线类型'
                rules={[{required: true, message: '请选择生产线类型'}]}>
              <Select
                  placeholder='请选择生产线类型'
                  style={{width: '200px'}}
                  onChange={productLineChange}>
                {productLine.map(item => (
                  <Option
                      value={item.value}
                      key={'productLineType' + item.value}>
                    {item.type}
                  </Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item
                name='productName'
                label='生产产品'
                rules={[{required: true, message: '请选择生产产品'}]}>
              <Select style={{width: '200px'}}>
                {myProducts.map(item => (
                  <Option value={item} key={'productName' + item}>
                    {item}
                  </Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item label='安装周期'>
              <Input
                  value={productLineNow ? productLineNow.setupPeriod : ''}
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item label='每期安装费用'>
              <Input
                  value={productLineNow ? productLineNow.setupPeriodPrice : ''}
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item label='转产周期'>
              <Input
                  value={productLineNow ? productLineNow.changePeriod : ''}
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item label='每期转产费用'>
              <Input
                  value={productLineNow ? productLineNow.changeCost : ''}
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item label='生产效率'>
              <Input
                  value={productLineNow ? productLineNow.producePeriod : ''}
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item label='每期维护费用'>
              <Input
                  value={productLineNow ? productLineNow.mainCost : ''}
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item label='每期折旧费用'>
              <Input
                  value={productLineNow ? productLineNow.depreciation : ''}
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item label='残值数值'>
              <Input
                  value={productLineNow ? productLineNow.stumpCost : ''}
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
          </Form>
        </Modal>
        <Modal
            className='addLineModal'
            title='新建厂房'
            visible={isaddNewFactory}
            onOk={handleAddNewFactoryOk}
            confirmLoading={addFactoryLoading}
            onCancel={() => {
            closeModel('addFactory')
          }}
            okText='确认创建'
            cancelText='取消'>
          <Form
              labelCol={{span: 8}}
              wrapperCol={{span: 16}}
              form={addFactoryForm}>
            <Form.Item
                name='addFactoryType'
                label='新建厂房类型'
                rules={[{required: true, message: '请选择新建厂房类型'}]}>
              <Select style={{width: '200px'}}>
                {factoryType.map(item => (
                  <Option value={item.type} key={'new' + item.type}>
                    {item.type}
                  </Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item
                name='addFactoryPlace'
                label='厂房地址'
                rules={[{required: true, message: '请选择新建房地址'}]}>
              <Select style={{width: '200px'}}>
                {myPlace.map(item => (
                  <Option value={item} key={item}>
                    {item}
                  </Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item label='支付修建费用方式'>
              <Input
                  value='按期支付建造资金'
                  disabled
                  style={{width: '200px'}}></Input>
            </Form.Item>
            <Form.Item label='开始建造厂房时间'>
              <Input
                  value={localStorage.getItem('currentTime')}
                  style={{width: '200px'}}
                  disabled></Input>
            </Form.Item>
          </Form>
        </Modal>
        <Modal
            className='addLineModal'
            title='租用厂房'
            visible={isrentFactory}
            onOk={handleRentFactoryOk}
            confirmLoading={rentFactoryLoading}
            onCancel={() => {
            closeModel('rentFactory')
          }}
            okText='确认租用'
            cancelText='取消'>
          <Form
              labelCol={{span: 8}}
              form={rentFactoryForm}
              wrapperCol={{span: 16}}>
            <Form.Item
                name='rentFactoryType'
                label='租赁厂房类型'
                rules={[{required: true, message: '请选择租赁厂房类型'}]}>
              <Select style={{width: '200px'}} onChange={changeRentFactoryType}>
                {factoryType.map(item => (
                  <Option value={item.type} key={'rent' + item.type}>
                    {item.type}
                  </Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item
                name='rentFactoryPlace'
                label='厂房地址'
                rules={[{required: true, message: '请选择厂房地址'}]}>
              <Select style={{width: '200px'}}>
                {myPlace.map(item => (
                  <Option value={item} key={item}>
                    {item}
                  </Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item label='每期需缴纳租金' name='rentCost'>
              <Input disabled style={{width: '200px'}} />
            </Form.Item>
            <Form.Item
                label='需等待多少周期才可用'
                name='needPeriod'
                rules={[
                {
                  required: true,
                  message: '请输入有效的等待的周期',
                  pattern: /^[0-9]+$/
                }
              ]}>
              <Input style={{width: '200px'}}></Input>
            </Form.Item>
          </Form>
        </Modal>
      </div>
      {contextHolder}
    </ReachableContext.Provider>
  )
}

export default connect()(WorkshopManagement)
