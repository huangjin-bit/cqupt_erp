package com.cqupt.mislab.erpn.dao.loan;

import com.cqupt.mislab.erpn.model.entity.loan.LoanBasic;
import lombok.Data;
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
public interface LoanBasicDao {

    /**
     * 获取贷款的基本信息
     * @return
     */
    List<LoanBasic> listLoanBasicInfo();

    /**
     * 获取指定类型的利率
     * @param loanTypeName
     * @return
     */
    float getBasicRate(@Param("loanTypeName") String loanTypeName);
}
