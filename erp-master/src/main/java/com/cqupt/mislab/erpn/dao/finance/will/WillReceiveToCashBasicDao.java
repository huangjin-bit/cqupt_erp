package com.cqupt.mislab.erpn.dao.finance.will;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 张烈文
 * @date 2021/8/12 21:09
 */
@Mapper
@Repository
public interface WillReceiveToCashBasicDao {

    Float getRate();
}
