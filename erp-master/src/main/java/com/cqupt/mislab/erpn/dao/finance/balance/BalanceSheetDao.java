package com.cqupt.mislab.erpn.dao.finance.balance;

import com.cqupt.mislab.erpn.model.vo.finance.BalanceSheetVo;
import com.cqupt.mislab.erpn.model.vo.finance.BalanceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/8/12 17:56
 */
@Mapper
@Repository
public interface BalanceSheetDao {


    /**
     * 查询资产负负债
     * @param userUnique
     * @param period
     * @return
     */
    List<BalanceVO> findBalanceSheet(@Param("userUnique") String userUnique, @Param("period") Integer period);

    /**
     * 获得当前周期的期初值
     * @param userUnique
     * @param currentPeriod
     * @return
     */
    List<Float> findBeginValue(@Param("userUnique") String userUnique, @Param("currentPeriod") Integer currentPeriod);

    List<BalanceSheetVo> findBalanceSheetVo(String userUnique);
}
