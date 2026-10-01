package com.cqupt.mislab.erpn.dao.factory;

import com.cqupt.mislab.erpn.model.entity.factory.FactoryRenting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/11 22:32
 */
@Mapper
@Repository
public interface FactoryRentingDao {


    /**
     * 获取租借的厂房
     * @param userUnique
     * @return
     */
    List<FactoryRenting> listFactoryRentingsByUserUnique(String userUnique);


    /**
     * 获取指定市场和指定用户的已建好的厂房
     * @param userUnique
     * @param place
     * @return
     */
    List<FactoryRenting> listFactoryRentingsByPlace(@Param("userUnique") String userUnique,@Param("place") String place);


    /**
     * 新增租用的厂房
     *
     * @param factoryRenting
     * @return
     */
    Boolean insertFactoryRenting(FactoryRenting factoryRenting);


    /**
     * 不再租用指定厂房
     * @param userUnique
     * @param factoryId
     * @return
     */
    Boolean deleteFactoryRenting(@Param("userUnique") String userUnique,@Param("factoryId") Integer factoryId);






}
