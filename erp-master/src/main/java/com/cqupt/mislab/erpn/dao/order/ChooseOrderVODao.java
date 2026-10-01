package com.cqupt.mislab.erpn.dao.order;

import com.cqupt.mislab.erpn.model.vo.order.ChooseOrderVO;
import com.cqupt.mislab.erpn.model.vo.order.MarketProductPair;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

/**
 * @Author xhx
 * @Date 2021/2/9 16:23
 */
@Mapper
@Repository
public interface ChooseOrderVODao {
    List<ChooseOrderVO> findChooseOrderValueObject(@Param("period") int period, @Param("groupName") String gameGroupName, @Param("marketName") String marketName, @Param("productName") String productName, @Param("userUnique") String userUnique);

    /**
     * 寻找下一个选择订单的人
     * @param marketName
     * @param productName
     * @param period
     * @param groupName
     * @return userUnique
     */
    String findChooserOrderUser(@Param("marketName") String marketName,@Param("productName") String productName,@Param("period") Integer period,@Param("groupName") String groupName);

    /**
     * 与上述方法雷同findChooserOrderUser
     * @param period
     * @param groupName
     * @return
     */
    List<String> findUsersOrder(@Param("period") Integer period,@Param("groupName") String groupName);

    HashSet<MarketProductPair> getMarketProductPairs(int period, String userUnique, int chooseValue, String groupName);
}
