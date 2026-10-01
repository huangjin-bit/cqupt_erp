package com.cqupt.mislab.erpn.dao.finance.account;

import com.cqupt.mislab.erpn.model.entity.finance.account.AccountHead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author whl
 * @date 2021/2/2 - 22:42
 */
@Repository
@Mapper
public interface AccountHeadDao {
    /**
     * 向accounthead表中插入一条记录
     * @param userUnique
     * @param happenTime
     * @param accountIdDescription
     */
    void addAccountHead(@Param("userUnique") String userUnique,@Param("happenTime") Integer happenTime,@Param("accountIdDescription") String accountIdDescription);

    /**
     * 在accounthead表中查找userUnique对应的最大AccountID值
     * @param userUnique
     * @return
     */
    Integer findAccountId(String userUnique);

    /**
     * 查询在[minPeriod,maxPeriod]内的所有accountId
     * @param userUnique
     * @param minPeriod
     * @param maxPeriod
     * @return
     */
    List<AccountHead> findAccountHeadsByUserUnique(@Param("userUnique") String userUnique, @Param("minPeriod") Integer minPeriod, @Param("maxPeriod") Integer maxPeriod);
}
