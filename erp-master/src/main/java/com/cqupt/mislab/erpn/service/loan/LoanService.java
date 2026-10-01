package com.cqupt.mislab.erpn.service.loan;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.loan.LoanOfUser;
import com.cqupt.mislab.erpn.model.vo.loan.LoanOfUserVO;

/**
 * @author 张烈文
 * @date 2021/8/13 10:00
 */
public interface LoanService {


    /**
     * 找到贷款的基本信息
     *
     * @param userUnique
     * @return
     */
    Result findLoanBasicVoByLoanTypeName(String userUnique);

    /**
     * 申请贷款
     * @param userUnique
     * @param loanOfUser
     * @return
     */
    Result applyLoan(String userUnique, LoanOfUserVO loanOfUser);


    /**
     * 根据指定类型及状态获取用户贷款单
     * @param userUnique

     * @return
     */
    Result getLoanOfUser(String userUnique,String loanTypeName,Integer status);

    /**
     * 获取一年内到期的长期贷款
     * @param userUnique
     * @return
     */
    Result findEndOneYear(String userUnique);

    /**
     * 归还贷款
     * @param userUnique
     * @param loanId
     * @return
     */
    Result returnLoan(String userUnique, Integer loanId);
}
