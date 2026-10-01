package com.cqupt.mislab.erpn.dao.advertisement;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.advertisement.Advertisement;
import com.cqupt.mislab.erpn.model.entity.order.OrderSequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 */
@Repository
@Mapper
public interface AdvertisementDao {


    /**
     * 没有开发完成的产品也可以投广告
     * 根据市场名和userUnique返回产品信息
     * @param marketName
     * @param userUnique
     * @return
     */
    List<Advertisement> findAdvertisementByMarketNameAndUserUnique(@Param("marketName") String marketName, @Param("userUnique") String userUnique);

    /**
     * 修改某个广告投入的资金
     * @param adId
     * @param money
     * @return
     */
    Boolean updateMoneyByAdId(@Param("adId") Integer adId, @Param("money") Double money);

    /**
     * 获取已经（即money>0则代表已经投放）投放广告的advertisement
     *
     * period已经封装在sql里面
     * （由于每年（4期）都需要重新再投广告，因此就需要period）
     * @param userUnique
     * @return
     */
    List<Advertisement> findAlreadyAdvertisement(String userUnique);


    /**
     * 查找用户总共投放的广告费用
     * @param userUnique
     * @param currentPeriod
     * @return
     */
    Double findSummaryMoney(@Param("userUnique") String userUnique, @Param("currentPeriod") Integer currentPeriod);

    /**
     * 根据某个市场,某个产品,某个组,某个周期 来返回一个投放广告的序列
     * @param groupName
     * @param period
     * @param marketName
     * @param productName
     * @return
     */
    List<OrderSequence> findAdvertisementMoneyOfUsers(@Param("groupName") String groupName, @Param("period") Integer period, @Param("marketName") String marketName, @Param("productName") String productName);

    /**
     * 查看用户在某个市场某个产品 里面投放广告费用的多少
     * @param userUnique
     * @param period
     * @param marketName
     * @param productName
     * @return
     */
    Double findUserAdvertisementMoney(@Param("userUnique") String userUnique, @Param("period") Integer period, @Param("marketName") String marketName, @Param("productName") String productName);

    List<Advertisement> findMarketNameAndProductName();

    void addAdvertisementList(List<Advertisement> list);
}
