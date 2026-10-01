package com.cqupt.mislab.erpn.dao.loan;

import com.cqupt.mislab.erpn.model.entity.loan.LoanOfUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/8/12 22:49
 */
@Mapper
@Repository
public interface LoanOfUserDao {

    /**
     * 根据类型获取尚未归还的贷款
     *
     * @param userUnique
     * @param status     0-贷款未归还，1-贷款已归还
     * @param loanTypeName   贷款类型,'长期贷款','短期贷款','高利贷',如果给一个null就会查询所有贷款记录
     * @return
     */
    List<LoanOfUser> findLoanOfUser(@Param("userUnique") String userUnique, @Param("status") Integer status, @Param("loanTypeName") String loanTypeName);

    /**
     * 新增贷款记录
     * @param loanOfUser
     */
    void insertLoanOfUser(LoanOfUser loanOfUser);

    /**
     * 获取指定的贷款记录
     * @param userUnique
     * @param loanId
     * @return
     */
    LoanOfUser getLoanOfUser(@Param("userUnique") String userUnique,@Param("loanId") Integer loanId);

    /**
     * 修改指定的loanOfUser
     * @param userUnique
     * @param loanId
     */
    void updateLoanOfUser(@Param("userUnique") String userUnique, @Param("loanId") Integer loanId);
}
