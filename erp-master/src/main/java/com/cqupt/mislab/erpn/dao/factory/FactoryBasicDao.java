package com.cqupt.mislab.erpn.dao.factory;

import com.cqupt.mislab.erpn.model.entity.factory.FactoryBasic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 张烈文
 * @date 2021/7/12 13:31
 */
@Mapper
@Repository
public interface FactoryBasicDao {

    /**
     * 根据厂房类型获取厂房基本信息
     * @param factoryType
     * @return
     */
    FactoryBasic getFactoryBasic(String factoryType);
}
