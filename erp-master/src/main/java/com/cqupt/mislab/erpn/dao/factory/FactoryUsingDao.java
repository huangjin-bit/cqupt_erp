package com.cqupt.mislab.erpn.dao.factory;


import com.cqupt.mislab.erpn.model.entity.factory.FactoryMaking;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryRenting;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryUsing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 */
@Mapper
@Repository
public interface FactoryUsingDao {

    /**
     * 获取已经建造好的厂房
     * @param userUnique
     * @return
     */
    List<FactoryUsing> listFactoryUsingsByUserUnique(String userUnique);


    /**
     * 获取指定市场和指定用户的已建好的厂房
     * @param userUnique
     * @param place
     * @return
     */
    List<FactoryUsing> listFactoryUsingsByPlace(@Param("userUnique") String userUnique,@Param("place") String place);

    /**
     *
     * 删除指定的已修键好的厂房
     * @param userUnique
     * @param factoryId
     * @return
     */
    Boolean deleteFactory(@Param("userUnique") String userUnique,@Param("factoryId") Integer factoryId);

    /**
     * 查找指定已建好的厂房
     * @param userUnique
     * @param factoryId
     * @return
     */
    FactoryUsing findUsingDetailById(@Param("userUnique") String userUnique,@Param("factoryId") Integer factoryId);
}
