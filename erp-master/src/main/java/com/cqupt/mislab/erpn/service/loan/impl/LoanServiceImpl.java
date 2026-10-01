package com.cqupt.mislab.erpn.service.loan.impl;

import com.cqupt.mislab.erpn.commons.constant.LoanTypeEnum;
import com.cqupt.mislab.erpn.commons.constant.ResultEnum;
import com.cqupt.mislab.erpn.commons.utils.ERPUtils;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.finance.balance.BalanceSheetDao;
import com.cqupt.mislab.erpn.dao.finance.cash.CashDao;
import com.cqupt.mislab.erpn.dao.loan.LoanBasicDao;
import com.cqupt.mislab.erpn.dao.loan.LoanOfUserDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.loan.LoanAccountDTO;
import com.cqupt.mislab.erpn.model.entity.loan.LoanBasic;
import com.cqupt.mislab.erpn.model.entity.loan.LoanOfUser;
import com.cqupt.mislab.erpn.model.vo.loan.EndOneYearLoanOfUser;
import com.cqupt.mislab.erpn.model.vo.loan.LoanBasicVO;
import com.cqupt.mislab.erpn.model.vo.loan.LoanOfUserVO;
import com.cqupt.mislab.erpn.service.finance.account.AccountDetailService;
import com.cqupt.mislab.erpn.service.finance.account.AccountHeadService;
import com.cqupt.mislab.erpn.service.loan.LoanService;
import lombok.extern.slf4j.Slf4j;
import java.util.AbstractMap.SimpleEntry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 张烈文
 * @date 2021/8/13 10:00
 */
@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

    @Autowired
    private BalanceSheetDao balanceSheetDao;
    @Autowired
    private CommonDao commonDao;

    @Autowired
    private LoanBasicDao loanBasicDao;

    @Autowired
    private LoanOfUserDao loanOfUserDao;
    @Autowired
    private AccountHeadService accountHeadService;
    @Autowired
    private AccountDetailService accountDetailService;

    @Autowired
    private CashDao cashDao;
    /**
     * 获得长期/短期金额上限
     * @param userUnique
     * @param loanTypeName
     * @return
     */
    private double findMaxOfLoan(String userUnique, String loanTypeName) {
        Double s = 0.0;
        Double a = 0.0;
        // 根据userUnique得到当前周期获得期初资产
        List<Float> list_s = balanceSheetDao.findBeginValue(userUnique, commonDao.findCurrentPeriod(userUnique));
        for (Float t : list_s) {
            s += t;
        }
        // 获得已经在借的贷款,且没归还
        List<LoanOfUser> list_a = loanOfUserDao.findLoanOfUser(userUnique, 0,
                loanTypeName);
        for (LoanOfUser t : list_a) {
            a += t.getMoney();
        }
        return ERPUtils.round(2 * s - a);//lx：处理一下double变量，四舍五入

    }

    @Override
    public Result findLoanBasicVoByLoanTypeName(String userUnique) {
//        查找当前周期并计算当前年份
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        Integer currentYear = (currentPeriod - 1) / 4 + 1;
//        获取贷款类型的基本信息
        List<LoanBasic> loanBasics = loanBasicDao.listLoanBasicInfo();
        List<LoanBasicVO> loanBasicVOS = loanBasics.stream().map((item) -> {
            LoanBasicVO loanBasicVO = new LoanBasicVO();
            BeanUtils.copyProperties(item, loanBasicVO);
            loanBasicVO.setTotalYear(commonDao.findTotalYear(userUnique));
//            根据贷款类型来设置最大额度
            String loanTypeName = item.getLoanTypeName();
            if (loanTypeName.equals(LoanTypeEnum.SHORT_LOAN.getLoanTypeName())||loanTypeName.equals(LoanTypeEnum.LONG_LOAN.getLoanTypeName())) {
                loanBasicVO.setMaxOfLoan(findMaxOfLoan(userUnique, loanTypeName));
            }
//            根据currentPeriodOfYear是否>1来判断是否是年初
            loanBasicVO.setBeginYear(isBeginYear(userUnique, currentPeriod, currentYear));
            loanBasicVO.setCurrentYear(currentYear);
//            这两个根据贷款类型来设置年或期
            this.setPermitYearOrPeriod(userUnique, loanBasicVO, loanBasicVO.getLoanTypeName());
            return loanBasicVO;
        }).collect(Collectors.toList());
        return ResultUtil.success(loanBasicVOS);
    }

    @Transactional
    @Override
    public Result applyLoan(String userUnique, LoanOfUserVO loanOfUserVO) {
        //判断是否可以贷款
        SimpleEntry<Boolean, String> canLoan = isCanLoan(userUnique, loanOfUserVO);
        if(!canLoan.getKey()){
            return ResultUtil.error(canLoan.getValue());
        }
        //计算贷款时间
        HashMap<String, Object> ans = new HashMap<>();
        String loanTypeName = loanOfUserVO.getLoanTypeName();
        int beginTime = commonDao.findCurrentPeriod(userUnique);
        int endTime = 0;
        Integer periodsOfOneYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        if(LoanTypeEnum.LONG_LOAN.getLoanTypeName().equals(loanTypeName)){
            endTime = beginTime + loanOfUserVO.getLoanTime() * periodsOfOneYear;
        }else{
            endTime = beginTime + loanOfUserVO.getLoanTime();
        }


        //  在loanofuser表中添加新的记录
        LoanOfUser loanOfUser = new LoanOfUser();
        BeanUtils.copyProperties(loanOfUserVO, loanOfUser);
        loanOfUser.setBeginTime(beginTime);
        loanOfUser.setEndTime(endTime);
        loanOfUser.setUserUnique(userUnique);
//            这里由于数据表中MONEY字段是int类型，又由于咱们从传入的贷款金额为double类型，因此多了一句（数据库有问题）
        loanOfUser.setMoney(loanOfUserVO.getMoney().intValue());
        loanOfUserDao.insertLoanOfUser(loanOfUser);
//        处理会计
        LoanAccountDTO loanAccountDTO = new LoanAccountDTO();
        loanAccountDTO.setItem1("现金");
        loanAccountDTO.setItemType1("借");
        loanAccountDTO.setItemType2("贷");
        addDataToAccountDTO(loanOfUserVO.getLoanTypeName(), loanAccountDTO);
        this.dealLoanAccount(loanAccountDTO, userUnique, loanOfUserVO.getMoney(), beginTime);
        log.info("currentCash:{}---loanMoney:{}", cashDao.findCash(userUnique), loanOfUserVO.getMoney());
        cashDao.addCash(userUnique, loanOfUserVO.getMoney());

        return ResultUtil.success("贷款成功");
        /*
        try {
//        计算贷款时间，按长期loanTime单位为年，短期/高利贷loanTime单位为周期

//        计算并判断用户可贷款金额
            double maxOfLoan = findMaxOfLoan(userUnique, loanTypeName);
            if (maxOfLoan - loanOfUserVO.getMoney() >= 0) {


                if (loanTypeName.equals(LoanTypeEnum.LONG_LOAN.getLoanTypeName())) {
                    if (beginYear == 1){
                        endTime = beginTime + loanOfUserVO.getLoanTime() * periodsOfOneYear;
                    }
                    else {
                        return ResultUtil.error();
                    }
            } else {
                endTime = beginTime + loanOfUserVO.getLoanTime();
            }
//        在loanofuser表中添加新的记录
            LoanOfUser loanOfUser = new LoanOfUser();
            BeanUtils.copyProperties(loanOfUserVO, loanOfUser);
            loanOfUser.setBeginTime(beginTime);
            loanOfUser.setEndTime(endTime);
            loanOfUser.setUserUnique(userUnique);
//            这里由于数据表中MONEY字段是int类型，又由于咱们从传入的贷款金额为double类型，因此多了一句（数据库有问题）
            loanOfUser.setMoney(loanOfUserVO.getMoney().intValue());
            loanOfUserDao.insertLoanOfUser(loanOfUser);
//        处理会计
            LoanAccountDTO loanAccountDTO = new LoanAccountDTO();
            loanAccountDTO.setItem1("现金");
            loanAccountDTO.setItemType1("借");
            loanAccountDTO.setItemType2("贷");
            addDataToAccountDTO(loanOfUserVO.getLoanTypeName(), loanAccountDTO);
            this.dealLoanAccount(loanAccountDTO, userUnique, loanOfUserVO.getMoney(), beginTime);
            log.info("currentCash:{}---loanMoney:{}", cashDao.findCash(userUnique), loanOfUserVO.getMoney());
            cashDao.addCash(userUnique, loanOfUserVO.getMoney());
                ans.put("maxOfLoan", maxOfLoan - loanOfUser.getMoney());
            }else {
                ans.put("maxOfLoan", maxOfLoan);
                return ResultUtil.error(ResultEnum.ERROR.getStatus(),ResultEnum.ERROR.getMsg(),ans);
            }
        } catch (Exception e) {
            return ResultUtil.error();
        }
        return ResultUtil.success(ans);*/

    }

    private SimpleEntry<Boolean, String> isCanLoan(String userUnique, LoanOfUserVO loanOfUserVO) {

        String loanTypeName = loanOfUserVO.getLoanTypeName();
        if(LoanTypeEnum.LONG_LOAN.getLoanTypeName().equals(loanTypeName)||LoanTypeEnum.SHORT_LOAN.getLoanTypeName().equals(loanTypeName)){
            //计算并判断用户可贷款金额
            double maxOfLoan = findMaxOfLoan(userUnique, loanTypeName);
            //判断可用余额是否足够
            if (maxOfLoan - loanOfUserVO.getMoney() < 0){
                if(maxOfLoan<0){
                    return new SimpleEntry<>(false, "可贷款金额不足，剩余可贷款为："+0+"元");
                }
                return new SimpleEntry<>(false, "可贷款金额不足，剩余可贷款为："+maxOfLoan+"元");
            }
            //如果是长期贷款，还需要判断当前是否为年初
            if(LoanTypeEnum.LONG_LOAN.getLoanTypeName().equals(loanTypeName)){
                //判断当前时间是否为年初
                Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
                Integer currentYear = (currentPeriod - 1) / 4 + 1;
                Integer beginYear = isBeginYear(userUnique, currentPeriod, currentYear);
                if(beginYear!=1){
                    return new SimpleEntry<>(false, "非年初，不能长期贷款");
                }
            }

        }
        return new SimpleEntry<>(true, "");
    }


    @Override
    public Result getLoanOfUser(String userUnique,String loanTypeName,Integer status) {
        List<LoanOfUser> loanOfUser = loanOfUserDao.findLoanOfUser(userUnique, status, loanTypeName);
        return ResultUtil.success(loanOfUser);
    }

    @Override
    public Result findEndOneYear(String userUnique) {
//        获取所有未归还的长期贷款
        List<LoanOfUser> loanOfUser = loanOfUserDao.findLoanOfUser(userUnique, 0, LoanTypeEnum.LONG_LOAN.getLoanTypeName());
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        Integer periodsOfYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
//       从所有数据中取出一年内归还的长期贷款
        List<EndOneYearLoanOfUser> endOneYearLoanOfUsers = loanOfUser.stream().filter((item) -> item.getEndTime() - currentPeriod <= 4 ? true : false)
                .map((item) -> {
                    EndOneYearLoanOfUser endOneYearLoanOfUser = new EndOneYearLoanOfUser();
                    BeanUtils.copyProperties(item, endOneYearLoanOfUser);
                    int beginYear = (item.getBeginTime() - 1) / periodsOfYear + 1;
                    int endYear = (item.getEndTime() - 1) / periodsOfYear + 1;
                    endOneYearLoanOfUser.setBeginYear(beginYear);
                    endOneYearLoanOfUser.setEndYear(endYear);
                    return endOneYearLoanOfUser;
                }).collect(Collectors.toList());
        return ResultUtil.success(endOneYearLoanOfUsers);
    }

    @Transactional
    @Override
    public Result returnLoan(String userUnique, Integer loanId) {

        try {
            // 根据userUnique 和 loanId 取得loanTypeName beginTime money
            LoanOfUser loanOfUser = loanOfUserDao.getLoanOfUser(userUnique, loanId);
            Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
            Integer periodsOfOneYearByUserUnique = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
            String loanTypeName = loanOfUser.getLoanTypeName();
            Integer beginTime = loanOfUser.getBeginTime();
            Integer money = loanOfUser.getMoney();
            Integer endTime = loanOfUser.getEndTime();
//        会计操作
//        根据loanTypeName的值，如果为"长期贷款"则判断是否为一年内到期的长期贷款
            if (LoanTypeEnum.LONG_LOAN.getLoanTypeName().equals(loanTypeName) && (endTime - currentPeriod <= periodsOfOneYearByUserUnique)) {

                accountHeadService.addAccountHead(userUnique, currentPeriod,
                        "归还一年内到期的长期贷款" + loanId);
                accountDetailService.addAccountDetail(userUnique, "一年内到期的长期贷款",
                        "现金", "借", "贷", money.doubleValue(), -money.doubleValue(), -money.doubleValue());
                cashDao.reduceCash(userUnique, money.doubleValue());
            } else if (LoanTypeEnum.SHORT_LOAN.getLoanTypeName().equals(loanTypeName)) {
                // 判断loanTypeName的值，如果为"短期贷款"则计算利息
                double basicRate =loanBasicDao.getBasicRate(LoanTypeEnum.SHORT_LOAN.getLoanTypeName());
                //lx：处理一下double变量，四舍五入
                double interest = ERPUtils.round((currentPeriod - beginTime) * basicRate * money);

                if (interest > 0) {
                    accountHeadService.addAccountHead(userUnique, currentPeriod,
                            "归还短期贷款利息" + loanId);
                    accountDetailService.addAccountDetail(userUnique, "财务费用", "现金",
                            "借", "贷", interest, interest, -interest);
                    cashDao.reduceCash(userUnique, interest);
                }

                accountHeadService.addAccountHead(userUnique, currentPeriod, "归还"
                        + loanTypeName);
                accountDetailService.addAccountDetail(userUnique, loanTypeName,
                        "现金", "借", "贷", money.doubleValue(), -money.doubleValue(), -money.doubleValue());
                cashDao.reduceCash(userUnique, money.doubleValue());
            } else {
                accountHeadService.addAccountHead(userUnique, currentPeriod, "归还"
                        + loanTypeName);
                accountDetailService.addAccountDetail(userUnique, loanTypeName,
                        "现金", "借", "贷", money.doubleValue(), -money.doubleValue(), -money.doubleValue());
                cashDao.reduceCash(userUnique, money.doubleValue());
            }
            // 修改status的值为1
            loanOfUserDao.updateLoanOfUser(userUnique, loanId);
        } catch (Exception e) {
           throw new RuntimeException("还款操作出现异常", e);
        }
        return ResultUtil.success();
    }

    /**
     * 按不同贷款类型填充填充不同的会计信息
     * @param loanTypeName
     * @param loanAccountDTO
     */
    private void addDataToAccountDTO(String loanTypeName, LoanAccountDTO loanAccountDTO) {
        if (loanTypeName.equals(LoanTypeEnum.LONG_LOAN.getLoanTypeName())) {
            loanAccountDTO.setLoanDescription("申请长期贷款生效");
            loanAccountDTO.setItem2("长期贷款");
        } else if (loanTypeName.equals(LoanTypeEnum.SHORT_LOAN.getLoanTypeName())) {
            loanAccountDTO.setLoanDescription("申请短期贷款生效");
            loanAccountDTO.setItem2("长期贷款");
        }else{
            loanAccountDTO.setLoanDescription("申请长期贷款生效");
            loanAccountDTO.setItem2("长期贷款");
        }
    }

    /**
     * 添加会计分录
     * @param loanAccountDTO
     * @param userUnique
     * @param money
     * @param currentPeriod
     */
    private void dealLoanAccount(LoanAccountDTO loanAccountDTO,String userUnique,Double money,Integer currentPeriod) {
        accountHeadService.addAccountHead(userUnique, currentPeriod, loanAccountDTO.getLoanDescription());
        accountDetailService.addAccountDetail(userUnique, loanAccountDTO.getItem1(), loanAccountDTO.getItem2(),
                loanAccountDTO.getItemType1(), loanAccountDTO.getItemType2(), money, money, money);
    }


    /**
     * 按贷款类型设置允许的年或周期
     * @param userUnique
     * @param loanBasicVO
     * @param loanTypeName
     */
    private void setPermitYearOrPeriod(String userUnique,LoanBasicVO loanBasicVO,String loanTypeName) {
        if (loanTypeName.equals(LoanTypeEnum.LONG_LOAN.getLoanTypeName())) {
            loanBasicVO.setPermitYear(findRestOfYear(userUnique));
        }else{
            loanBasicVO.setPermitPeriod(findRestOfPeriod(userUnique));
        }
    }

    /**
     * 查询允许贷款的最长周期
     * @param userUnique
     * @return
     */
    private Integer findRestOfPeriod(String userUnique) {
        int currentPeriod = commonDao.findCurrentPeriod(userUnique);
        int periodsOfOneYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        int totalYear = commonDao.findTotalYear(userUnique);
        if (currentPeriod > (totalYear - 1) * periodsOfOneYear) {
            return totalYear * periodsOfOneYear - currentPeriod;
        } else {
            return periodsOfOneYear;
        }

    }

    /**
     * 查询允许贷款的最长年限
     * @param userUnique
     * @return
     */
    private Integer findRestOfYear(String userUnique) {
        int currentPeriod = commonDao.findCurrentPeriod(userUnique);
        int periodsOfYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        int totalYear = commonDao.findTotalYear(userUnique);
        return totalYear - ((currentPeriod-1) / periodsOfYear+1);
    }

    /**
     * 判断当前周期是否为年初
     * @param currentPeriod
     * @return
     */
    private Integer isBeginYear(String userUnique,Integer currentPeriod, Integer currentYear) {
        //        获取当前周期是第几年的第几周期
        Integer currentPeriodOfYear = currentPeriod - (currentYear - 1) * commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        log.info("currentPeriodOfYear:{}", currentPeriodOfYear);
        return currentPeriodOfYear > 1 ? 0 : 1;
    }
}
