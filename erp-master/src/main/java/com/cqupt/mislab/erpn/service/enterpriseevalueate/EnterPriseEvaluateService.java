package com.cqupt.mislab.erpn.service.enterpriseevalueate;

import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.*;

import java.util.List;
import java.util.Map;

/**
 * @author 张烈文
 * @date 2021/10/7 14:29
 */
public interface EnterPriseEvaluateService {

    /**
     * 计算个人所有者权益合计
     *
     * @param userUnique
     * @param period
     * @return
     */
    public Double CalOwnerBenifit(String userUnique, int period);

    List<AdminIncomeBeanNewly> showEndValue (int year,int peroid, String groupName, String userUnique);

    /**
     * 生产力分析
     * @param groupName
     * @return
     */
    List<ProduceCapacity> getMembersCapacity (String groupName);

    List<MemberSaleOfMarket> getGeneralMarketShare(String groupName, Integer year);

    List<MemberSaleOfProduct> getProductMarketShare(String groupName, Integer year);
    /**
     * 广告投入产出分析
     * @param groupName
     * @param year
     */
    List<Map<String, Object>> getUserIORatesOfAd(String groupName, int year);
    /**
     * 成本费用占销售比例分析
     * @param groupName
     * @param year
     */
    List<MemberCost> getCostStructure(String groupName, int year);
    /**
     * 成本费用占销售比例变化分析
     * @param groupName
     */
//    List<MemberCost> getCostStructureChanges(String groupName,String userunique);

     List<List<MemberCost>>  getCostStructureChanges(String groupName, String userunique);
    /**
     * 产品贡献利润分析
     * @param groupName
     * @param year
     */
    List<ProductProfit> getProductsProfit(String groupName, int  year);
}
