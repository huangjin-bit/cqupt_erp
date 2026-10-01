import React, {Fragment, useState, useEffect} from 'react'
import {OwnerEquity, ContentBox, SearchBox} from './components/index'
import httpUtil from 'utils/request'
import {Switch, Route} from 'react-router-dom'
import {message} from 'antd'

// 定义学生和老师的标号
const tea = 1,
  stu = 0

export default function AdvertisingInvestmentAnalysis(props) {
  // 创建状态
  // 小组数据列表,tagdataList用于存储所有数据，showData用于展示
  const [dataList, setDataList] = useState({teacher: '', student: ''})
  const [showData, setShowData] = useState(null)
  // 用于标识当前页面展示的是哪个数据列表（tea:老师自建列表,stu:学生自建列表）
  const [curListTag, setCurListTag] = useState(stu)
  // 控制数据获取过程的加载
  const [dataListLoading, setDataListLoading] = useState(true)
  // 控制contentBox的page保存，解决点击进度管理之后返回page变成1以及pageSize变成默认6的问题
  const [pageControl, setPageControl] = useState({
    page: 1,
    pageSize: 6
  })
  // 保存搜索框的内容
  const [searchContent, setSearchContent] = useState('')

  // 获取小组列表数据,初始为获取学生列表
  useEffect(() => {
    const formData = new FormData()
    formData.append('status', stu)
    httpUtil.showGroups(formData).then(res => {
      setDataList({
        ...dataList,
        student: res.data
      })
      setShowData(res.data)
      setCurListTag(stu)
    })
  }, [])

  // 收集数据用于搜索
  const onSelectChange = value => {
    setCurListTag(value)
    // 当数据列表为0，也就是老师自建列表时，如果此时老师自建列表还没获取，则发送请求获取数据
    if (value == tea && !dataList.teacher) {
      const formData = new FormData()
      formData.append('status', tea)
      httpUtil.showGroups(formData).then(res => {
        setDataList({
          ...dataList,
          teacher: res.data
        })
        setShowData(res.data)
        setCurListTag(tea)
      })
    } else {
      setShowData(value == tea ? dataList.teacher : dataList.student)
    }
  }
  // 封装独立一个搜索判断函数
  const searchJudge = (searchItem, searchTarget, value) => {
    // 当搜索框内容为空时，直接返回全部内容
    if (value === '') {
      message.warning('请输入小组名')
    } else if (value === '\\' || value === '|') {
      message.warning('请输入合法内容')
    } else {
      // 当搜索框中有值时，根据该值搜索，同时支持大小写转换
      const ret = dataList[searchTarget]?.filter(item => {
        return (
          item[searchItem].search(value) !== -1 ||
          item[searchItem].search(value.toLowerCase()) !== -1 ||
          item[searchItem].search(value.toUpperCase()) !== -1
        )
      })
      if (ret === undefined) {
        message.error('无数据')
      } else if (ret?.length !== 0 && ret) {
        message.success('搜索成功')
        setPageControl(pre => ({...pre, page: 1}))
      } else {
        message.error('未找到该小组')
      }
      // 将搜索到的结果展示出来
      setShowData(ret)
    }
  }
  const onSearch = value => {
    // 在这里定义搜索项，来确定按照什么搜索 groupCreatorId
    const searchItem = 'groupName'
    // value是搜索框输入的值
    curListTag === tea
      ? searchJudge(searchItem, 'teacher', value)
      : searchJudge(searchItem, 'student', value)
  }

  // 当搜索框中没有内容时，列表显示全部内容
  const onSearchChange = e => {
    setSearchContent(e.target.value)
    if (e.target.value === '' && curListTag === tea) {
      setShowData(dataList.teacher)
    } else if (e.target.value === '' && curListTag === stu) {
      setShowData(dataList.student)
    }
  }

  // 组装状态
  const PCState = {showData, setShowData, curListTag, dataList}
  const SBState = {
    onSelectChange,
    onSearch,
    onSearchChange,
    curListTag,
    searchContent
  }
  const CBState = {
    showData,
    dataListLoading,
    setDataListLoading,
    pageControl,
    setPageControl
  }

  return (
    <Fragment>
      <Switch>
        <Route path='/teacher/businessValuation/1/showAnalysis/:groupName'>
          <OwnerEquity state={{...PCState}} tea={tea} stu={stu} />
        </Route>
        <Route path='/teacher/businessValuation/1'>
          <Fragment>
            <div className='search-box'>
              <SearchBox fun={{...SBState}} tea={tea} stu={stu} />
            </div>
            <div className='content-box'>
              <ContentBox state={{...CBState}} />
            </div>
          </Fragment>
        </Route>
      </Switch>
    </Fragment>
  )
}
