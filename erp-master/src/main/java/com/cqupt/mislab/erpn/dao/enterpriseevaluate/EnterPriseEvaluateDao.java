package com.cqupt.mislab.erpn.dao.enterpriseevaluate;

import com.cqupt.mislab.erpn.model.dto.enterpriseevalueate.MarketShareDTO;
import com.cqupt.mislab.erpn.model.dto.enterpriseevalueate.ProductShareDTO;
import com.cqupt.mislab.erpn.model.dto.gamegroup.GameGroupMemberDTO;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.MemberSaleOfMarket;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.ProduceCapacity;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.Products;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.UserInputAndOutputOfAd;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/10/7 14:17
 */
@Mapper
public interface EnterPriseEvaluateDao {
    /**
     * 计算所有者权益合计
     * @param userUnique
     * @param period
     * @return
     */
    Double CalOwnerBenifit(@Param("userUnique") String userUnique,@Param("period") int period);

    List<GameGroupMemberDTO> getGroupMember(@Param("groupName") String groupName);

    List<ProduceCapacity> getMemberCapacity(@Param("userUnique") String userUnique);

    Double getMemberSaleByMarket(MarketShareDTO marketShareDTO);
    Double getMemberSaleByProduct(ProductShareDTO productShareDTO);

    List<UserInputAndOutputOfAd> getGroupMembersOnIOAd(@Param("groupName") String groupName);

    Double getUserOutput(@Param("userUnique")String userUnique, @Param("firstPeriod")int firstPeriod, @Param("lastPeriod")int lastPeriod);

    Double getUserInput(@Param("userUnique")String userUnique, @Param("period")int period);

    List<Products> getProducts(String userunique, int firstPeriod, int lastPeriod);

    Double  getAdCost(String userunique, int period) ;

    Double getItemCost(String userunique, String item, int firstPeriod, int lastPeriod);

    GameGroupMemberDTO getGroupMemberByUserUnique(String userunique);

    List<Products> getProductsByGroupName(String userunique, String productName, int firstPeriod, int lastPeriod);
}
