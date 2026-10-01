package com.cqupt.mislab.erpn.dao.advertisement;

import com.cqupt.mislab.erpn.model.entity.advertisement.AdvertisementStatusOfUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 */
@Repository
@Mapper
public interface AdvertisementStatusOfUserDao {

    /**
     * 查询用户广告投放状态码
     *
     * 0-未投放
     *
     * 1-已投放
     * @param userUnique
     * @param currentPeriod
     * @return
     */
    Integer findFinishAdFlag(@Param("userUnique") String userUnique, @Param("currentPeriod") Integer currentPeriod);

    /**
     * 修改投放广告是否完成的状态
     * @param userUnique
     * @param currentPeriod
     * @param finishFlag  0-未完成，1-完成
     * @return
     */
    Boolean updateFinishAdFlag(@Param("userUnique") String userUnique, @Param("currentPeriod") Integer currentPeriod, @Param("finishFlag") Integer finishFlag);

    /**
     * 根据组内所有未破产成员的userUnique和当前周期
     * 获取组内成员广告的相关状态信息并封装为List
     * @param userUniques
     * @param period
     * @return
     */
    List<AdvertisementStatusOfUser> findAdvertisementStatusOfUserByUserUniques(@Param("userUniques") String[] userUniques, @Param("period") Integer period);

    /**
     * 查找用户当前是否在选订单
     * @param userUnique
     * 用户唯一标识
     * @param currentPeriod
     * 当前周期
     * @return
     * 表示用户是否可以开始当前期的选订单工作：0表示选单尚未开始；1表示选单正在进行中。
     */
    Integer findChooseOrderFlag(@Param("userUnique") String userUnique, @Param("currentPeriod") int currentPeriod);

    /**
     * 查找用户是否完成了选订单
     *
     * @param userUnique 用户唯一标识
     * @return 1-用户完成了选订单；
     * 0-用户尚未完成选订单
     */
    Integer findFinishOrderFlag(@Param("userUnique") String userUnique, @Param("currentPeriod") int currentPeriod);


    /**
     * 批量更新一堆指定用户的status
     * @param userUniques
     * @param period
     * @param status
     */
    void updateChooseOrderFlag(@Param("userUniques") String[] userUniques, @Param("period") int period, @Param("status") int status);

    /**
     * findAdvertisementStatusOfUserByUserUnique 查找单个对象的方法.
     * @param userUnique
     * @param period
     * @return
     */
    AdvertisementStatusOfUser findAdvertisementStatusOfUserByUserUnique(@Param("userUnique") String userUnique, @Param("period") Integer period);

    void updateFinishOrderFlag(@Param("userUnique") String userUnique, @Param("period") Integer period, @Param("finishFlag") Integer finishFlag);


    void addAdStatusOfUser(AdvertisementStatusOfUser advertisementStatusOfUser);

    /**
     * 检查订单状态
     * @param userUnique
     * @return
     */
    int checkFinishOrderFlag(String userUnique);
}
