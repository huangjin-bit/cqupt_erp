package com.cqupt.mislab.erpn.dao.finance.profit;

import com.cqupt.mislab.erpn.model.vo.finance.ProfitVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;

/**
 * @author 张烈文
 * @date 2021/8/12 20:35
 */
@Mapper
@Repository
public interface ProfitDao {


    /**
     * 查询利润表
     * @param userUnique
     * @param period
     * @return
     */
    List<ProfitVO> findProfitSheet(@Param("userUnique") String userUnique, @Param("period") Integer period);
}
