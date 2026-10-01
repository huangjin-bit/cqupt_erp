package com.cqupt.mislab.erpn.controller.teacher;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.AdminIncomeBeanNewly;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.MemberSaleOfMarket;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.MemberSaleOfProduct;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.ProduceCapacity;
import com.cqupt.mislab.erpn.service.enterpriseevalueate.EnterPriseEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author xhx
 * @Date 2021/11/3 21:37
 */
@RestController
@RequestMapping("/teacherEvaluate")
public class EnterpriseEvaluateController {
    @Autowired
    EnterPriseEvaluateService service;

    /**
     * 所有者权益分析
     * @param year
     * @param groupName
     * @param userUnique
     * @return
     */
    @RequestMapping("/intermediateEquity")
    public Result OwnerEquity(@RequestParam("year") Integer year, @RequestParam("groupName") String groupName, @RequestParam("userUnique")String userUnique, @RequestParam("period")Integer period){
        List<AdminIncomeBeanNewly> adminIncomeBeanNewlies = service.showEndValue(year, period, groupName, userUnique);
        return ResultUtil.success(adminIncomeBeanNewlies);
    }

    /**
     * 生产能力分析
     * @return
     */
    @RequestMapping("/getMemberCapacity")
    public Result getMembersCapacity(@RequestParam("groupName") String groupName) {
        return ResultUtil.success(service.getMembersCapacity(groupName));
    }

    /**
     * 市场占有率
     * @param groupName
     * @param year
     * @return
     */
    @RequestMapping("/getMarketShare")
    public Result getGeneralMarketShare(@RequestParam("groupName")String groupName, @RequestParam("year")Integer year) {
        List<MemberSaleOfMarket> memberSaleOfMarkets = service.getGeneralMarketShare(groupName, year);
        return ResultUtil.success(memberSaleOfMarkets);
    }

    /**
     * 产品市场占有率
     * @param groupName
     * @param year
     * @return
     */
    @RequestMapping("/getProductMarketShare")
    public Result getProductMarketShare(@RequestParam("groupName")String groupName, @RequestParam("year")Integer year) {
        List<MemberSaleOfProduct> memberSaleOfProducts = service.getProductMarketShare(groupName, year);
        return ResultUtil.success(memberSaleOfProducts);
    }

    /**
     * 广告投入产出分析
     * @param groupName
     * @param year
     * @return
     */
    @RequestMapping("/getUserIORatesOfAd")
    public Result getUserIORatesOfAd(@RequestParam("groupName")String groupName, @RequestParam("year")Integer year) {
        return ResultUtil.success(service.getUserIORatesOfAd(groupName, year));
    }

    /**
     * 成本费用占销售比例分析
     * @param groupName
     * @param year
     * @return
     */
    @RequestMapping("/getCostStructure")
    public Result getCostStructure(@RequestParam("groupName")String groupName, @RequestParam("year")Integer year) {
        return ResultUtil.success(service.getCostStructure(groupName, year));
    }

    /**
     * 成本费用占销售比例变化分析
     * @param groupName
     * @param userUnique
     * @return
     */
    @RequestMapping("/getCostStructureChanges")
    public Result getCostStructureChanges(@RequestParam("groupName")String groupName, @RequestParam(value = "userUnique", required = false)String userUnique) {
        return ResultUtil.success(service.getCostStructureChanges(groupName, userUnique));
    }

    /**
     * 产品贡献利润分析
     * @param groupName
     * @param year
     * @return
     */
    @RequestMapping("/getProductsProfit")
    public Result getProductsProfit(@RequestParam("groupName")String groupName, @RequestParam("year")Integer year) {
        return ResultUtil.success(service.getProductsProfit(groupName, year));
    }
}
