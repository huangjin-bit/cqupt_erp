package com.cqupt.mislab.erpn.dao.advertisement;

import com.cqupt.mislab.erpn.model.vo.advertisement.AdOfStatusVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author xhx
 * @Date 2021/10/19 20:53
 */
@Repository
@Mapper
public interface AdOfStatusVODao {
    AdOfStatusVO findAdOfStatusByUseruniqueAndPeriod(@Param("userUnique")String userUnique, @Param("period")String period);
}
