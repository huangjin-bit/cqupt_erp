/**
 * @description 所有接口的封装类
 * @author 任崇伟
 * @date 2020-7-26
 * @lastModifiedBy 任崇伟
 */

/**
 * @description 添加教师接口，封装post方法formdata格式
 * @author 胡彪
 * @date 2021-04-17
 * @lastModifiedBy 黄显瑜
 */

import HttpUtils from './http'

const encodeQuery = value => encodeURIComponent(value == null ? '' : value)

class Https {
  //* *******************************************************接口也要分类放********************************************************/
  //* ****************************************************************************************************************************/

  //* ***************************************************************************************************************************/
  //* *********************************************************教师端接口*********************************************************/
  //* ***************************************************************************************************************************/
  /*                                                           ***    ***
   ***    ***
   ***    ***
   ***    ***
   ***    ***
   ***          ***
   ***        ***
   ***      ***
   ***    ***
   ***  ***
   **
   */

  //* ***************************************************************************************************************************/
  //* **********************************************************教师端登陆接口******************************************************/
  /**
   *教师登录
   */
  postTeacherLogin = parmas => HttpUtils.post('/erpn/teacher/login', parmas)

  /**
   * 教师注销登录
   */
  teacherLogout = () => HttpUtils.get(`/erpn/teacher/logout`)

  //* ***************************************************************************************************************************/
  //* **********************************************************用户管理接口******************************************************/

  /**
   * 取出审批用户(用户列表)
   */
  findAllApprovedUser = parmas =>
    HttpUtils.get('/erpn/teacher/findAllApprovedUser', parmas)

  /**
   * 取出待审批用户
   */
  findAllRegister = parmas =>
    HttpUtils.get('/erpn/teacher/findAllRegister', parmas)

  /**
   * 批量审批用户
   */
  passBatchRegisterUsers = parmas =>
    HttpUtils.post(
      `/erpn/teacher/passBatchRegisterUsers?userIds=${parmas.userIds}&&adminId=${parmas.adminId}`
    )

  /**
   * 审批用户
   */
  passRegisterUser = parmas =>
    HttpUtils.post('/erpn/teacher/passRegisterUser', parmas)

  /*
   *拒绝审批用户 refuseRegisterUsers
   */
  refuseRegisterUsers = parmas =>
    HttpUtils.post(
      `/erpn/teacher/passBatchRegisterUsers?userIds=${parmas.userId}&&adminId=${parmas.adminId}`
    )

  // 使用body传参方式
  //   /*
  //    *批量拒绝审批用户 refuseBatchRegisterUsers
  //    */
  //   refuseBatchRegisterUsers = (parmas) =>
  //     HttpUtils.post(`/erpn/teacher/refuseBatchRegisterUsers`, parmas)

  /*
   *批量拒绝审批用户 refuseBatchRegisterUsers
   */
  refuseBatchRegisterUsers = parmas =>
    HttpUtils.post(
      `/erpn/teacher/refuseBatchRegisterUsers?userIds=${parmas.userIds}&&adminId=${parmas.adminId}`
    )

  /**
   * 批量删除学生
   * @returns
   */
  //   deletestus = (formdata) =>
  //     HttpUtils.post_formdata('/erpn/teacher/deletestus', formdata)
  deletestus = parmas =>
    HttpUtils.get(`/erpn/teacher/deletestus?userIds=${parmas.userIds}`)

  /**
   * 删除学生
   * @returns
   */
  //   deletestu = (formdata) =>
  //     HttpUtils.post_formdata('/erpn/teacher/deletestu', formdata)
  deletestu = parmas =>
    HttpUtils.get(`/erpn/teacher/deletestu?userId=${parmas.userId}`)

  //* ***************************************************************************************************************************/
  //* **********************************************************创建比赛接口******************************************************/
  /**
   * 清空全部学生并重置系统
   * @param {Object} params
   * @returns
   */
  resetSystemData = params => HttpUtils.post('/erpn/teacher/resetSystemData', params)


  /**
   * csv文件导入，生成用户列表
   * @returns
   */
  importstu = (teacherId, formdata) =>
    HttpUtils.post_formdata(
      `/erpn/teacher/importstu?adminId=${teacherId}`,
      formdata
    )

  /**
   * 导入csv文件，直接创建比赛
   * @returns
   */
  importStuForGame = (teacherId, formdata) =>
    HttpUtils.post_formdata(
      `/erpn/teacher/importStuToGame?adminId=${teacherId}`,
      formdata
    )

  /**
   * 按学号查找
   * @param {*} formdata
   * @returns
   */
  search = formdata => HttpUtils.post_formdata('/erpn/teacher/search', formdata)

  /**
   * 筛选学生创建比赛
   * @param {*} formdata
   * @returns
   */
  selectStuStart = formdata =>
    HttpUtils.post_formdata('/erpn/teacher/selectStuStart', formdata)

  /**
   * 获取当前教师信息
   * @returns
   */
  getCurTeacherInfo = parmas => HttpUtils.get('/erpn/teacher/logInfo', parmas)

  // ***********************以下接口在其他地方使用过****************************

  // /**
  //   * 获取老师列表
  //   */
  // getTeacherList = () => HttpUtils.get('/erpn/group/getTeacherList')

  // /**
  //  * 删除学生
  //  * @returns
  //  */
  // //   deletestu = (formdata) =>
  // //     HttpUtils.post_formdata('/erpn/teacher/deletestu', formdata)
  // deletestu = parmas =>
  //   HttpUtils.get(`/erpn/teacher/deletestu?userId=${parmas.userId}`)

  // /**
  //  * 取出审批用户(用户列表)
  //  */
  //  findAllApprovedUser = parmas =>
  //  HttpUtils.get('/erpn/teacher/findAllApprovedUser', parmas)

  // /**
  //  * 创建分组
  //  * @param {string} groupName - 分组名称
  //  * @param {string} maxEnterpriseNumber - 最大企业数量
  //  * @param {string} teacherId - 教师 id
  //  */
  //    postCreateGroup = (userId, parmas) =>
  //    HttpUtils.post(`/erpn/group/create?userId=${userId}`, parmas)

  // *************************************************************************

  //* **********************************post_formdata格式********************************//
  //* ***********************************************************************************//

  /**
   * 获取指定页数用户
   * @returns
   */
  pageUsers = formdata =>
    HttpUtils.post_formdata('/erpn/teacher/pageUsers', formdata)

  /**
   * 重置密码
   * @param {*} formdata
   * @returns
   */
  resetPassword = formdata =>
    HttpUtils.post_formdata('/erpn/teacher/resetPassword', formdata)

  /**
   * 获取分页数量
   */
  pages = formdata => HttpUtils.post_formdata(`/erpn/teacher/pages`, formdata)

  //* ***************************************************************************************************************************/
  //* **********************************************************管理比赛接口******************************************************/

  //* ********************************进度管理模块********************************

  /**
   * 获取小组列表
   */

  showGroups = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/showGroups`, formdata)

  /**
   * 进度管理：获取进度信息
   */
  query = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/query`, formdata)

  /**
   * 推进下一周期
   */
  forwardPeriod = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/forwardPeriod`, formdata)

  /**
   * 结束经营
   */
  endPlayGame = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/endPlayGame`, formdata)

  /**
   * 结束广告投放
   */
  endAdvertising = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/endAdvertising`, formdata)

  /**
   * 结束选单
   */
  endChooseOrder = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/endChooseOrder`, formdata)

  //* ********************************贡献度查看确认模块********************************

  /**

  * 展示用户贡献度
  */
  listContribution = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/listContribution`, formdata)

  /**
   * 确认用户贡献度
   */
  confirmScore = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/confirmScore`, formdata)

  //* ********************************分数设置模块********************************

  /**

  * 确认用户贡献度
  */
  setGradeRange = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/setGradeRange`, formdata)

  /**
   * 搜索比赛
   */
  search = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherGroup/search`, formdata)

  //* ********************************比赛报告下载模块********************************

  /**

  * 下载比赛报告
  */
  getDownloadReportPath = groupName => {
    return fetch(`/erpn/teacher/downloadReport?groupName=${groupName}`, {
      method: 'post'
    })
  }
  // HttpUtils.post_formdata(`/erpn/teacher/downloadReport`, formdata)

  /**
   * 预览比赛报告
   */
  viewReport = formdata =>
    HttpUtils.post_formdata(`/erpn/teacher/viewReport`, formdata)

  //* ***************************************************************************************************************************/
  //* **********************************************************企业评价接口******************************************************/

  //* ********************************所有者权益分析模块********************************
  /**
   * 查看所有者权益
   */
  intermediateEquity = formdata =>
    HttpUtils.post_formdata(
      `/erpn/teacherEvaluate/intermediateEquity`,
      formdata
    )

  //* ********************************生产能力分析模块********************************
  /**
   * 查看生产能力分析
   */
  getMemberCapacity = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherEvaluate/getMemberCapacity`, formdata)

  //* ********************************综合市场占有率分析模块********************************
  /**
   * 查看综合市场占有率分析
   */
  getMarketShare = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherEvaluate/getMarketShare`, formdata)

  //* ********************************产品市场占有率分析模块********************************
  /**
   * 查看产品市场占有率分析
   */
  getProductMarketShare = formdata =>
    HttpUtils.post_formdata(
      `/erpn/teacherEvaluate/getProductMarketShare`,
      formdata
    )

  //* ********************************成本费用占销售比例分析分析模块********************************
  /**
   * 成本费用占销售比例分析
   */
  getCostStructure = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherEvaluate/getCostStructure`, formdata)

  //* ********************************广告投入产出分析模块********************************
  /**
   * 广告投入产出分析
   */
  getUserIORatesOfAd = formdata =>
    HttpUtils.post_formdata(
      `/erpn/teacherEvaluate/getUserIORatesOfAd`,
      formdata
    )
  //* ********************************成本费用占销售比例变化模块********************************
  /**
   * 成本费用占销售比例变化
   */
  getCostStructureChanges = formdata =>
    HttpUtils.post_formdata(
      `/erpn/teacherEvaluate/getCostStructureChanges`,
      formdata
    )
  //* ********************************产品贡献利润分析模块********************************
  /**
   * 产品贡献利润分析
   */
  getProductsProfit = formdata =>
    HttpUtils.post_formdata(`/erpn/teacherEvaluate/getProductsProfit`, formdata)
  //* ***************************************************************************************************************************/
  //* *********************************************************学生端接口*********************************************************/
  //* ***************************************************************************************************************************/
  /*                                                           ***    ***

                                                               ***    ***
                                                               ***    ***
                                                               ***    ***
                                                               ***    ***
                                                            ***          ***
                                                             ***        ***
                                                              ***      ***
                                                               ***    ***
                                                                ***  ***
                                                                   **
  */

  //* ****************************************************************************************************************************//

  //* **********************************************************用户接口**********************************************************/
  /**
   * 登录
   * @param {string} userId - 用户名
   * @param {string} password - 密码
   * @param {string} serverCode - 验证码
   */
  postLogin = parmas => HttpUtils.post('/erpn/user/login', parmas)

  /**
   * 用户注销
   */
  logout = () => HttpUtils.get('/erpn/user/logout')

  /**
   * 用户注册
   * @param {string} email - 电子邮件
   * @param {string} major - 专业
   * @param {string} password - 密码
   * @param {string} sclass - 班级
   * @param {string} sid - 学号
   * @param {string} tel - 联系方式（电话号码）
   * @param {string} userId - 用户名
   * @param {string} userName - 真实姓名
   */
  postRegister = parmas =>
    HttpUtils.post('/erpn/user/regester/addregester', parmas)

  /**
   *
   * 获取登录的用户信息
   */
  getBasic = () => HttpUtils.get('/erpn/user/getBasic')

  /**
   * 获取注册之后未审批的信息
   * @param {string} userId - 用户名
   */
  getRegister = parmas =>
    HttpUtils.get(`/erpn/user/register/getRegister?userId=${parmas}`)

  /**
   * 获取被审批的用户信息
   * @param {string} userId - 用户名
   */
  getApprovedUser = parmas =>
    HttpUtils.get(`/erpn/user/login/getApprovedUser?userId=${parmas}`)

  /**
   * 修改用户信息
   * @param {string} email - 电子邮件
   * @param {string} major - 专业
   * @param {string} password - 密码
   * @param {string} sclass - 班级
   * @param {string} sid - 学号
   * @param {string} tel - 联系方式（电话号码）
   * @param {string} userId - 用户名
   * @param {string} userName - 真实姓名
   */
  postModifyUser = parmas => HttpUtils.post('/erpn/user/modifyUser', parmas)
  //* ***************************************************************************************************************************/

  //* **********************************************************游戏分组接口******************************************************/
  /**
   * 获取老师列表
   */
  getTeacherList = () => HttpUtils.get('/erpn/group/getTeacherList')

  /**
   * 创建分组
   * @param {string} groupName - 分组名称
   * @param {string} maxEnterpriseNumber - 最大企业数量
   * @param {string} teacherId - 教师 id
   */
  postCreateGroup = (userId, parmas) =>
    HttpUtils.post(`/erpn/group/create?userId=${userId}`, parmas)

  /**
   * 获取分组列表
   *
   */
  getGroupList = () => HttpUtils.get(`/erpn/group/getGroupList`)

  /**
   * 获取分组成员列表
   * @param {string} groupName - 分组名称
   */
  getGroupMemberList = param =>
    HttpUtils.get(`/erpn/group/getGroupMemberList?groupName=${param}`)

  /** *
   * 加入分组
   * @param {string} groupName - 分组名称
   *  @param {string} userId - 用户 Id
   */
  joinGroup = param =>
    HttpUtils.post(
      `/erpn/group/join?groupName=${param.groupName}&userId=${param.userId}`
    )

  /**
   * 分组成员退出分组
   * @param {string} groupName - 分组名称
   *  @param {string} userId - 用户 Id
   */
  quitGroup = param =>
    HttpUtils.post(
      `/erpn/group/quit?groupName=${param.groupName}&userId=${param.userId}`
    )

  /**
   * 分组创建者剔除分组成员
   * @param {string} groupName - 分组名称
   */
  tickGroupMember = param =>
    HttpUtils.post(
      `/erpn/group/kick?groupName=${param.groupName}&userId=${param.userId}`
    )

  /**
   * 分组成员解散分组
   * @param {string} groupName - 分组名称
   */
  deleteGroup = param =>
    HttpUtils.get(`/erpn/group/delete?groupName=${param.groupName}`)

  /**
   * 分组成员退出或解散分组状态改变
   */
  modifyUserStatus = params => HttpUtils.get(`/erpn/group/modifyUserStatus`)

  /**
   * 开始比赛
   */
  startGame = param => HttpUtils.post('/erpn/group/startGame')

  //* ***************************************************************************************************************************/

  //* **********************************************************市场预测接口******************************************************/
  /**
   * 市场预测
   */
  marketForectEchart = param =>
    HttpUtils.get(`/erpn/market/predictions?marketName=${param.marketName}`)

  /**
  //****************************************************************************************************************************/

  //* **********************************************************市场管理接口******************************************************/
  /**
   * 获取所有市场
   */
  getAllMarket = () => HttpUtils.get(`/erpn/market/getAllMarket`)

  /**
   * 开始开拓一个市场
   */
  beginResearch = param =>
    HttpUtils.post(
      `/erpn/market/startUndevelopMarket?marketName=${param.marketName}`
    )

  /**
   * 暂停开拓一个开拓中的市场
   */
  pauseDevelopingMarket = param =>
    HttpUtils.post(
      `/erpn/market/pauseDevelopingMarket?marketName=${param.marketName}`
    )

  /**
   * 继续开拓一个开拓中的市场
   */
  continueDevelopingMarket = param =>
    HttpUtils.post(
      `/erpn/market/continueDevelopingMarket?marketName=${param.marketName}`
    )

  /**
   * 暂停维护一个开拓中的市场
   */

  // pauseMaintainDevelopedMarket = param =>
  //   HttpUtils.post(
  //     `/erpn/market/pauseMaintainDevelopedMarket?marketName=${param.marketName}`,
  //   )

  /**
   * 继续维护一个开拓中的市场
   */

  // continueMaintainDevelopedMarket = param =>
  //   HttpUtils.post(
  //     `/erpn/market/continueMaintainDevelopedMarket?marketName=${param.marketName} `,
  //   )

  //* ***************************************************************************************************************************/

  //* *****************************************************订单选择与广告投放接口**************************************************/
  /**
   * 广告投放进度条控制
   */
  getCurrentProgress = param => HttpUtils.get(`/erpn/ad/getCurrentProgress`)

  /**
   * 获取该用户已经开拓的市场
   */
  getMarketList = param => HttpUtils.get(`/erpn/ad/getMarketList`)

  /**
   * 获取指定市场的产品列表
   */
  getProductByMarket = param =>
    HttpUtils.get(`/erpn/ad/getProductByMarket?marketName=${encodeQuery(param.marketName)}`)

  /**
   * 投放广告
   */
  advertise = param =>
    HttpUtils.post(
      `/erpn/ad/putIntoAdvertisement?adId=${encodeQuery(param.adId)}&money=${encodeQuery(param.money)}`
    )

  /**
   * 获取已经投放的广告列表
   */
  getAlreadyAd = param => HttpUtils.get(`/erpn/ad/getAlreadyAd`)

  /**
   * 确认投放广告
   */
  confirm = param => HttpUtils.post(`/erpn/ad/confirm`)

  /**
   * 获取用户比赛信息
   */
  info = () => HttpUtils.get(`/erpn/user/info`)

  /**
   * 获取所有组员的进度
   */
  getMembersAdInfo = () => HttpUtils.get(`/erpn/ad/getMembersAdInfo`)

  /**
   *获取订单列表
   */
  getAll = () => HttpUtils.get(`/erpn/orderofusers/getall`)

  /**
   * 获取某个用户可以选择的订单列表
   */
  getMy = param => HttpUtils.get(`/erpn/chooseorder/getmy`)

  /**
   * 用户选择订单
   */
  chooseOrder = params =>
    HttpUtils.post(`/erpn/chooseorder/chooseorder?orderId=${encodeQuery(params.orderId)}`)

  /**
   * 获取订单详情
   */
  getOrderDetail = () => HttpUtils.get(`/erpn/chooseorder/getOrderDetail`)

  /**
   * 用户结束某个订货会
   */
  endSelectOrder = params =>
    HttpUtils.get(
      `/erpn/chooseorder/finishOrderByMarketNameAndProductName?marketName=${encodeQuery(params.marketName)}&productName=${encodeQuery(params.productName)}`
    )

  /**
   * 用户结束全部选择订单
   */
  finishAll = () => HttpUtils.post(`/erpn/chooseorder/finishAll`)

  /**
   * 修改Session信息
   */
  modifyStatus = () => HttpUtils.get(`/erpn/group/modifystatus`)

  //* ***************************************************************************************************************************/

  //* **********************************************************采购管理接口******************************************************/
  /**
   * 获取用户原料及原料基本信息
   */
  materialBasic = () => HttpUtils.get(`/erpn/material/materialbasic`)

  /**
   * 新增购买原料的订单
   */
  saveMaterialOrder = param =>
    HttpUtils.get(
      `/erpn/material/savematerialorder?materialName=${param.materialName}&mNumber=${param.number}`
    )

  /**
   * 获取用户还未到达的原料订单
   */
  unreachMaterial = param =>
    HttpUtils.get(
      `/erpn/material/unreachmaterial?pageIndex=${param.index}&pageSize=${param.size}`
    )

  //* ***************************************************************************************************************************/

  //* **********************************************************产品研发接口******************************************************/
  /*
   * 获取未研发的产品
   */
  getUndevelopsProduct = params => HttpUtils.get('/erpn/product/undevelops')

  /**
   * 获取研发中的产品
   */
  getDevelopingProduct = params => HttpUtils.get('/erpn/product/developing')

  /**
   * 获取研发完成的产品
   */
  getDevelopedProduct = params => HttpUtils.get('/erpn/product/developeds')
  /**
   * 开始研发产品
   */
  startDevelopProduct = params =>
    HttpUtils.get(`/erpn/product/start?productName=${params}`)

  /**
   * 暂停研发产品
   */
  stopDevelopProduct = params =>
    HttpUtils.get(`/erpn/product/stop?productName=${params}`)

  /**
   * 继续研发产品
   */
  recoveryDevelopProduct = params =>
    HttpUtils.get(`/erpn/product/recovery?productName=${params}`)

  /**
   * 获取未研发的ISO
   */
  getUndevelopIso = params => HttpUtils.get(`/erpn/iso/showUndevelop`)

  /**
   *获取研发中的ISO
   */
  getDevelopingIso = params => HttpUtils.get(`/erpn/iso/showDeveloping`)

  /**
   *获取研发完成的ISO
   */
  getDevelopedIso = params => HttpUtils.get(`/erpn/iso/showDeveloped`)

  /**
   *开始开拓一个ISO市场
   */
  startDevelopIso = params => HttpUtils.get(`/erpn/iso/start`, {isoName: params})

  /**
   *暂停一个研发中iso
   */
  stopDevelopIso = params =>
    HttpUtils.get(`/erpn/iso/stopDeveloping`, {isoName: params})

  /**
   *继续一个研发中iso
   */
  recoverDevelopIso = params =>
    HttpUtils.get(`/erpn/iso/continueDeveloping`, {isoName: params})
  //* ***************************************************************************************************************************/

  //* **********************************************************厂房管理接口******************************************************/

  //* ***************************************************************************************************************************/

  //* **********************************************************销售管理接口******************************************************/
  /**
   * 查看订单
   */
  checkOrders = () => HttpUtils.get(`/erpn/orderofusers/checkOrders`)

  /**
   * 交货
   */
  deliveryOrder = params =>
    HttpUtils.get(`/erpn/orderofusers/deliveryOrder`, params)

  //* ***************************************************************************************************************************/

  //* **********************************************************库存管理接口******************************************************/
  /**
   * 获取用户产品库存
   */
  products = () => HttpUtils.get(`/erpn/product/products`)

  /*
   *获取用户原料库存
   */
  materials = () => HttpUtils.get(`/erpn/material/materials`)

  //* ***************************************************************************************************************************/

  //* **********************************************************财务管理接口******************************************************/
  /*
   *获取用户资产负债表
   */
  balanceInfo = params => HttpUtils.get(`/erpn/balance/info`, params)

  /**
   * 获取用户会计目录
   */
  accountInfo = params => HttpUtils.get(`/erpn/account/info`, params)

  /**
   * 获取用户利润表
   */
  profitInfo = params => HttpUtils.get(`/erpn/profit/info`, params)

  /**
   * 获取用户应收账款贴现表
   */
  willreceiveInfo = () => HttpUtils.get(`/erpn/willreceive/info`)

  /**
   * 用户账款贴现
   */
  willreceiveDelete = params => HttpUtils.get(`/erpn/willreceive/delete`, params)

  //* ***************************************************************************************************************************/

  //* **********************************************************税金管理接口******************************************************/

  /**
   * 获取用户需缴纳税金
   */
  taxInfo = () => HttpUtils.get(`/erpn/tax/info`)

  //* ***************************************************************************************************************************/

  //* **********************************************************税金管理接口******************************************************/

  /**
   * 获取贷款的基本信息
   */
  loanInfo = () => HttpUtils.get(`/erpn/loan/info`)

  /**
   * 用户申请贷款
   */
  loanApply = params => HttpUtils.post(`/erpn/loan/apply`, params)

  /**
   * 获取用户贷款管理表
   */
  loanOfuser = params => HttpUtils.get(`/erpn/loan/loanofuser`, params)

  /**
   * 用户归还贷款
   */
  returnLoan = params => HttpUtils.get(`/erpn/loan/returnloan`, params)

  /**
   * 获取用户一年内的长期负债表
   */
  endOneYear = () => HttpUtils.get(`/erpn/loan/endOneYear`)

  //* ***************************************************************************************************************************/

  //* **********************************************************厂房管理接口******************************************************/
  /**
   * 获取已建好的厂房
   */

  getMadeFactory = () => HttpUtils.get(`/erpn/factory/factoryusing`)

  /**
   * 获取正在修建厂房信息
   */
  getMakingFactory = () => HttpUtils.get(`/erpn/factory/factorymaking`)

  /**
   * 获取正在租用厂房信息
   */
  getRentFactory = () => HttpUtils.get(`/erpn/factory/factoryrenting`)

  /**
   * 新增租用厂房
   */
  addRentFactory = params =>
    HttpUtils.post(`/erpn/factory/saverentingfactory`, params)

  /**
   * 新建厂房
   */
  addMakingFactory = params =>
    HttpUtils.post(`/erpn/factory/savemakingfactory`, params)

  /**
   * 暂停修建
   */
  stopMakingFactory = params =>
    HttpUtils.get(`/erpn/factory/stopmakingfactory?factoryId=${params}`)

  /**
   * 继续修建
   */
  keepMakingFactory = params =>
    HttpUtils.get(`/erpn/factory/keepmakingfactory?factoryId=${params}`)

  /**
   * 暂停租用厂房
   */
  stopRentFactory = params =>
    HttpUtils.get(`/erpn/factory/removerentingfactory?factoryId=${params}`)

  /**
   * 新增生产线
   */
  addProductLine = params =>
    HttpUtils.post(`/erpn/productline/addproductline`, params)

  /**
   * 撤销安装中的生产线
   */
  undoProductLine = params =>
    HttpUtils.get(`/erpn/productline/removeproductline?productLineId=${params}`)

  /**
   * 暂停安装
   */
  suspendInstall = params =>
    HttpUtils.get(
      `/erpn/productline/suspendedproductline?productLineId=${params}`
    )

  /**
   *恢复安装
   */
  keepInstall = params =>
    HttpUtils.get(`/erpn/productline/recoverproductline?productLineId=${params}`)

  /**
   * 暂停生产
   */
  suspendMaking = params =>
    HttpUtils.get(`/erpn/productline/suspendedproduct?productLineId=${params}`)

  /**
   * 恢复生产
   */
  keepMaking = params =>
    HttpUtils.get(`/erpn/productline/recoverproduct?productLineId=${params}`)

  /**
   * 开始生产
   */
  startMaking = params =>
    HttpUtils.get(`/erpn/productline/startproduct?productLineId=${params}`)

  /**
   * 开始转产
   */
  startChange = params =>
    HttpUtils.get(`/erpn/productline/beginchangeproduct`, params)
  /**
   * 暂停转产
   */
  suspendChange = params =>
    HttpUtils.get(
      `/erpn/productline/suspendedchangeproduct?productLineId=${params}`
    )

  /**
   * 继续转产
   */
  keepChange = params =>
    HttpUtils.get(
      `/erpn/productline/recoverchangeproduct?productLineId=${params}`
    )

  /**
   * 获取生产线信息
   */
  basicProductLine = () => HttpUtils.get(`/erpn/productline/basicProductLine`)

  /**
   * 出售生产线
   */
  saleProductLine = params =>
    HttpUtils.get(`/erpn/productline/saleproductline`, params)

  /**
   * 出售厂房
   */

  saleFactory = parmas => HttpUtils.get(`/erpn/factory/salefactory`, parmas)

  //* ***************************************************************************************************************************/

  //* **********************************************************推进下一周期******************************************************/

  /**
   * 推进下一周期
   */
  forwardCycle = () => HttpUtils.get(`/erpn/forward/cycle`)

  //* ***************************************************************************************************************************/

  //***********************************************************结束经营页面******************************************************/

  /**
   * 下载实验报告
   */
  // downloadDoc = params => HttpUtils.get('/erpn/report/downLoadDoc')
  downloadDoc = params => {
    return fetch('/erpn/report/downLoadDoc', {
      method: 'get'
    })
  }

  /**
   * 预览实验报告
   */
  overViewReport = params => HttpUtils.get(`/erpn/report/viewReport`)

  /**
   * 获取实验心得
   */
  getContent = params => HttpUtils.get('/erpn/experience/context')

  /**
   * 提交实验心得
   */
  addContent = params => HttpUtils.post('/erpn/experience/addcontext', params)

  /**
   * 获取小组成员信息
   */
  getMemberInfo = params => HttpUtils.get('/erpn/members/getMemberInfo')

  /**
   * 提交小组成员信息
   */
  submitInfo = params => HttpUtils.post(`/erpn/members/submit`, params)

  /**
   * 结束经营
   */
  overGame = params => HttpUtils.post('/erpn/group/stopOperation')

  /**
   * 重新开始
   */
  restartGame = params => HttpUtils.post('/erpn/user/replay')

  //* ***************************************************************************************************************************/
  //* *********************************************************超级管理员接口*********************************************************/
  //* ***************************************************************************************************************************/
  /*                                                           ***    ***
   ***    ***
   ***    ***
   ***    ***
   ***    ***
   ***          ***
   ***        ***
   ***      ***
   ***    ***
   ***  ***
   **
   */
  /**
   * 超级管理员登录
   */
  adminLogin = params => HttpUtils.post(`/erpn/superadmin/login`, params)

  /**
   * 获取管理员用户列表
   */
  getAdminList = params => HttpUtils.get(`/erpn/superadmin/adminlist`, params)

  /**
   * 新建管理员用户
   */
  addAdmin = params => HttpUtils.post(`/erpn/superadmin/adminadd`, params)
  // ?adminId=${params.adminId}&adminName=${params.adminName}&`

  /**
   * 删除管理员用户
   */
  deleteAdmin = params =>
    HttpUtils.post(`/erpn/superadmin/delete?adminId=${params.adminId}`)

  /**
   * 管理员密码重置
   */
  setpwd = params =>
    HttpUtils.get(`/erpn/superadmin/setpwd?adminId=${params.adminId}`)

  /**
   * 获取报告文件列表
   */
  getAllFiles = params => HttpUtils.get(`/erpn/superadmin/filelist`)

  /**
   * 删除单个文件
   */
  delFile = params =>
    HttpUtils.get(`/erpn/superadmin/deleteFile?fileName=${params.fileName}`)

  /**
   * 删除所有文件
   */
  delAllFiles = params => HttpUtils.get(`/erpn/superadmin/deleteAllFiles`)

  /**
   * 批量删除文件
   */
  delFiles = params => HttpUtils.post(`/erpn/superadmin/deleteFiles`, params)

  /**
   * 删除所有数据
   */
  delAllDatas = params => HttpUtils.get(`/erpn/superadmin/clearall`)

  /**
   * 下载说明书
   */
  downloadHelp = () => fetch('/erpn/download/erp-rules.doc', {method: 'get'})

  /**
   * 下载导入用户数据的csv模板
   */
  downloadTemplate = () => fetch('/erpn/download/template.csv', {method: 'get'})
  /**
   * 下载用于创建比赛的csv文件模板
   */
  downloadTemplateForGame = () =>
    fetch('/erpn/download/templategame.csv', {method: 'get'})
}
export default new Https()


