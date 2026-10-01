package com.cqupt.mislab.erpn.dao.market;

import com.cqupt.mislab.erpn.model.entity.market.DevelopedMarket;
import com.cqupt.mislab.erpn.model.entity.market.DevelopingMarket;
import com.cqupt.mislab.erpn.model.entity.market.UndevelopMarket;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author whl
 * @date 2020/11/22 - 20:10
 */
@Repository
@Mapper
public interface MarketDao {
    //返回一个List,存储DevelopedMarket
    List<DevelopedMarket> getDevelopedMarketListByUserUnique(@Param("userUnique") String userUnique);

    //返回一个List,存储DevelopingMarket
    List<DevelopingMarket> getDevelopingMarketListByUserUnique(@Param("userUnique") String userUnique);

    //返回一个List,存储UndevelopMarket
    List<UndevelopMarket> getUndevelopMarketListByUserUnique(@Param("userUnique") String userUnique);

    //获得一个DevelopedMarket当前的状态:1表示正在维护;0表示暂停维护
    int getDevelopedMarketStatus(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    //获得一个DevelopingMarket当前的状态:1表示正在开拓;0表示暂停开拓
    int getDevelopingMarketStatus(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    //把一个DevelopedMarket状态设为1(正在维护)
    Boolean setDevelopedMarketStatusOne(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    //把一个DevelopedMarket状态设为0(暂停维护)
    Boolean setDevelopedMarketStatusZero(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    //把一个DevelopingMarket状态设为0(暂停开拓)
    Boolean setDevelopingMarketStatusZero(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    //把一个DevelopingMarket状态设为1(继续开拓)
    Boolean setDevelopingMarketStatusOne(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    //根据marketName查询指定的UndevelopMarket
    UndevelopMarket getUndevelopMarket(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    //在数据表中增加一个DevelopingMarket
    Boolean addDevelopingMarket(@Param("developMarket") DevelopingMarket developMarket);

    //在数据表中删除一个UnDevelopingMarket
    Boolean deleteUnDevelopingMarket(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    //获取所有市场名称
    List<String> findMarketName();

    /**
     * 通过userUnique获取该用户已经开拓的市场名
     * @param userUnique
     * @return
     */
    List<DevelopedMarket> getMarketsByUserUnique(String userUnique);

    int findDevelopedMarket(String userUnique,String marketName);
}
