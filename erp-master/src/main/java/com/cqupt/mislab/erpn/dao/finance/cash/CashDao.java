package com.cqupt.mislab.erpn.dao.finance.cash;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 张烈文
 */
@Mapper
@Repository
public interface CashDao {

    /**
     * 查询现金金额
     * @param userUnique
     * @return
     */
    Double findCash(String userUnique);


    /**
     * 减少用户的现金
     * @param userUnique
     * @param summaryMoneyOfAd
     * @return
     */
    void reduceCash(@Param("userUnique") String userUnique, @Param("summaryMoneyOfAd") Double summaryMoneyOfAd);

    /**
     * 增加用户的现金
     * @param userUnique
     * @param cash
     */
    void addCash(@Param("userUnique") String userUnique, @Param("cash") Double cash);
}
