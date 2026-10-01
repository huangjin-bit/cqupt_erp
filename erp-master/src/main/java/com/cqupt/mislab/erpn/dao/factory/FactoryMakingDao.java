package com.cqupt.mislab.erpn.dao.factory;

import com.cqupt.mislab.erpn.model.entity.factory.FactoryMaking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/11 22:31
 */
@Mapper
@Repository
public interface FactoryMakingDao {

    /**
     * 获取在建厂房
     * @param userUnique
     * @return
     */
    List<FactoryMaking> listFactoryMakings(String userUnique);

    /**
     * 获取指定市场和指定用户的已建好的厂房
     * @param userUnique
     * @param place
     * @return
     */
    List<FactoryMaking> listFactoryMakingsByPlace(String userUnique, String place);

    /**
     * 新建厂房
     * @param factoryMaking
     * @return
     */
    Boolean insertFactory(FactoryMaking factoryMaking);

    /**
     * 停止修建厂房
     *
     * @param userUnique
     * @return
     */
    Boolean stopMakingFactory(@Param("userUnique") String userUnique,@Param("factoryId") Integer factoryId);

    /**
     * 继续修建厂房
     * @param userUnique
     * @return
     */
    Boolean keepMakingFactory(@Param("userUnique") String userUnique,@Param("factoryId") Integer factoryId);
}
