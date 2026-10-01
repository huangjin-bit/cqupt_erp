package com.cqupt.mislab.erpn.service.enterpriseevalueate.Impl;

import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.enterpriseevaluate.EnterPriseEvaluateDao;
import com.cqupt.mislab.erpn.dao.user.ApprovedUserDao;
import com.cqupt.mislab.erpn.dao.user.LoginUserDao;
import com.cqupt.mislab.erpn.model.dto.enterpriseevalueate.MarketShareDTO;
import com.cqupt.mislab.erpn.model.dto.enterpriseevalueate.ProductShareDTO;
import com.cqupt.mislab.erpn.model.entity.enterpriseevalueate.*;
import com.cqupt.mislab.erpn.model.dto.gamegroup.GameGroupMemberDTO;
import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.service.enterpriseevalueate.EnterPriseEvaluateService;
import com.cqupt.mislab.erpn.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张烈文
 * @date 2021/10/7 14:30
 */
@Service
@Slf4j
public class EnterPriseEvaluateServiceImpl implements EnterPriseEvaluateService {

    @Autowired
    CommonDao commonDao;
    @Autowired
    LoginUserDao loginUserDao;
    @Autowired
    private EnterPriseEvaluateDao enterPriseEvaluateDao;

    @Override
    public Double CalOwnerBenifit(String userUnique, int period) {
        return enterPriseEvaluateDao.CalOwnerBenifit(userUnique, period);
    }

    @Override
    public List<AdminIncomeBeanNewly> showEndValue(int year, int period, String groupName, String userUnique) {
        List<GameGroupMemberDTO> memberDTOS = enterPriseEvaluateDao.getGroupMember(groupName);
//        int periodOfYear = 0;
//        if(memberDTOS.size() > 0) {
//            periodOfYear = commonDao.findPeriodsOfOneYearByUserUnique(memberDTOS.get(0).getUserUnique());
//        }
        List<AdminIncomeBeanNewly> list = new ArrayList<>();
        for (int i = 0; i < memberDTOS.size() ; i++){
            AdminIncomeBeanNewly adminIncomeBean = new AdminIncomeBeanNewly();

            double endValue = CalOwnerBenifit(memberDTOS.get(i).getUserUnique(), (year - 1) * 4 + period);
            //这行代码可能有问题
            //String userName = commonDao.findUserNameByUserUniqueAndUserId(memberDTOS.get(i).getUserUnique(),memberDTOS.get(i).getUserId());

            adminIncomeBean.setCurrentPeriod(year);
            adminIncomeBean.setUserUnique(memberDTOS.get(i).getUserUnique());
            adminIncomeBean.setUserId(memberDTOS.get(i).getUserId());
            adminIncomeBean.setOwnerBenifit(endValue);
            adminIncomeBean.setUserName(memberDTOS.get(i).getUserName());
            list.add(adminIncomeBean);
        }
        return list;
    }

    @Override
    public List<ProduceCapacity> getMembersCapacity(String groupName) {
        List<ProduceCapacity> proCapacities = new ArrayList<ProduceCapacity>();
        List<GameGroupMemberDTO> members = enterPriseEvaluateDao.getGroupMember(groupName);
        for (int i = 0; i < members.size(); i++) {
            ProduceCapacity proCapacity = new ProduceCapacity();
            proCapacity.setMember(members.get(i));
            proCapacity.setCapacity(getMemberCapacity(proCapacity.getMember()
                    .getUserUnique()));
            proCapacities.add(proCapacity);
        }
        return proCapacities;
    }

    @Override
    public List<MemberSaleOfMarket> getGeneralMarketShare(String groupName, Integer year) {
        String[] MarketNames = new String[5];
        MarketNames[0] = "本地市场";
        MarketNames[1] = "区域市场";
        MarketNames[2] = "国内市场";
        MarketNames[3] = "亚洲市场";
        MarketNames[4] = "国际市场";
        List<MemberSaleOfMarket> list = null;
        List<MemberSaleOfMarket> memberSales = new ArrayList<>();
        for (int j = 0; j < MarketNames.length; j++) {
            list = getMemberSalesByMarket(groupName, MarketNames[j], year);
            for (int i = 0; i < list.size(); i++) {
                memberSales.add(list.get(i));
            }
        }
        return memberSales;
    }

    @Override
    public List<MemberSaleOfProduct> getProductMarketShare(String groupName, Integer year) {
        String[] productNames = new String[5];
        productNames[0] = "P1";
        productNames[1] = "P2";
        productNames[2] = "P3";
        productNames[3] = "P4";
        List<MemberSaleOfProduct> list = new ArrayList<>();
        List<MemberSaleOfProduct> memberSales = new ArrayList<>();
        for (int j = 0; j < productNames.length - 1; j++) {
            list = getMemberSalesByProduct(groupName,productNames[j], year);
            for (int i = 0; i < list.size(); i++) {
                memberSales.add(list.get(i));
            }
        }
        return memberSales;
    }
    /**
     * 新增一个userName,管理员页面展示可以用userName而不是userId
     * @author 杨青
     * @since 2016/11/27
     */

    @Override
    public List<Map<String, Object>> getUserIORatesOfAd(String groupName, int year) {
        List<Map<String, Object>> userIORates = new ArrayList<Map<String, Object>>(10);
        List<UserInputAndOutputOfAd> userIOs = getUserInputAndOutput(year, groupName);
        for (int i = 0; i < userIOs.size(); i++) {
            ApprovedUser approvedUser = loginUserDao.findApprovedUserByUserId(userIOs.get(i).getUserID());
            Map<String, Object> userRate = new HashMap<String, Object>(5);
            userRate.put("userID", userIOs.get(i).getUserID());
            userRate.put("username", approvedUser.getUserName());
            userRate.put("rate", userIOs.get(i).getRate());
            userIORates.add(userRate);
        }
        return userIORates;
    }




//    @Override
//    public List<MemberCost> getCostStructureChanges(String groupName, String userunique) {
//        GameGroupMemberDTO member = enterPriseEvaluateDao.getGroupMemberByUserUnique(userunique);
//        List<GameGroupMemberDTO> groupMember = enterPriseEvaluateDao.getGroupMember(groupName);
//
//
//        List<MemberCost> memberCosts = new ArrayList<MemberCost>();
//        memberCosts = getMemberCostsByFinishedYears(userunique);
//        for (int j = 0; j < memberCosts.size(); j++) {
//            memberCosts.get(j).setMember(member);
//        }
//        return memberCosts;
//    }

    @Override
    public List<List<MemberCost>>  getCostStructureChanges(String groupName, String userunique) {
        List<GameGroupMemberDTO> groupMember = enterPriseEvaluateDao.getGroupMember(groupName);

        List<List<MemberCost>> allMemberListCosts = new ArrayList<>();
        for(GameGroupMemberDTO member: groupMember) {
            List<MemberCost> memberCosts = new ArrayList<MemberCost>();
            memberCosts = getMemberCostsByFinishedYears(member.getUserUnique());
            for (int j = 0; j < memberCosts.size(); j++) {
                memberCosts.get(j).setMember(member);
            }
            allMemberListCosts.add(memberCosts);
        }
        return allMemberListCosts;
    }


    private List<MemberCost> getMemberCostsByFinishedYears(String userunique) {
        List<MemberCost> memberCosts = new ArrayList<MemberCost>();
        int FinishedYears = getFinishedYears(userunique);
        for (int i = 1; i <= FinishedYears; i++) {
            MemberCost memberCost = new MemberCost();
            int firstPeriod = (i - 1) * 4 + 1;
            int lastPeriod = firstPeriod + 3;
            memberCost.setTotalSale(getUserOutput(userunique, firstPeriod, lastPeriod));
            memberCost.setProductCost(getProductCost(userunique, firstPeriod, lastPeriod));
            memberCost.setAdCost(getAdCost(userunique, firstPeriod));
            memberCost.setOperationCost(getOperationCost(userunique, firstPeriod, lastPeriod));
            memberCost.setManagementCost(getManagementCost(userunique, firstPeriod, lastPeriod));
            memberCost.setDepreciationCost(getDepreciationCost(userunique, firstPeriod, lastPeriod));
            memberCost.setInterestCost(getInterestCost(userunique, firstPeriod, lastPeriod));
            memberCost.setYear(i);
            memberCosts.add(memberCost);
        }
        return memberCosts;
    }
    private int getFinishedYears(String userunique) {
        int periodOfYears = commonDao.findPeriodsOfOneYearByUserUnique(userunique);
        int period = commonDao.findCurrentPeriod(userunique);
        return period/periodOfYears +1;
    }




    @Override
    public List<MemberCost> getCostStructure(String groupName, int year) {
        List<MemberCost> membersCosts = getMembersCosts(groupName, year);
        return membersCosts;
    }

    private List<MemberCost> getMembersCosts(String groupName, int year) {
        List<MemberCost> membersCosts = new ArrayList<MemberCost>(10);
        List<GameGroupMemberDTO> members = enterPriseEvaluateDao.getGroupMember(groupName);
        int firstPeriod = (year - 1) * 4 + 1;
        int lastPeriod = firstPeriod + 3;
        for (int i = 0; i < members.size(); i++) {
            MemberCost memberCost = new MemberCost();
            memberCost.setMember(members.get(i));
            String userunique = memberCost.getMember().getUserUnique();
            memberCost.setTotalSale(getUserOutput(userunique, firstPeriod, lastPeriod));
            memberCost.setProductCost(getProductCost(userunique, firstPeriod, lastPeriod));
            memberCost.setAdCost(getAdCost(userunique, firstPeriod));
            memberCost.setOperationCost(getOperationCost(userunique, firstPeriod, lastPeriod));
            memberCost.setManagementCost(getManagementCost(userunique, firstPeriod, lastPeriod));
            memberCost.setDepreciationCost(getDepreciationCost(userunique, firstPeriod, lastPeriod));
            memberCost.setInterestCost(getInterestCost(userunique, firstPeriod, lastPeriod));
            memberCost.setYear(year);
            membersCosts.add(memberCost);
        }
        return membersCosts;
    }
    private double getProductCost(String userunique, int firstPeriod,
                                  int lastPeriod) {
        double productCost = 0;
        List<Products> Products = enterPriseEvaluateDao.getProducts(userunique, firstPeriod, lastPeriod);
        for (int i = 0; i < Products.size(); i++) {
            productCost += getCostOfOrder(Products.get(i).getProductname(), Products.get(i).getPnumber());
        }
        return productCost;

    }
    private double getCostOfOrder(String productName, int pNumber) {
        double costOfOrder = 0;
        if (productName != null) {
            if (productName.equals("P1")) {
                costOfOrder = pNumber * (1 + 0 + 0 + 0);
            } else if (productName.equals("P2")) {
                costOfOrder = pNumber * (1 + 1 + 0 + 0);
            } else if (productName.equals("P3")) {
                costOfOrder = pNumber * (0 + 2 + 1 + 0);
            } else if (productName.equals("P4")) {
                costOfOrder = pNumber * (0 + 1 + 1 + 2);
            }
        }
        return costOfOrder;
    }

    private double getAdCost(String userunique, int period) {
        Double adCost = enterPriseEvaluateDao.getAdCost(userunique, period);
        return adCost == null?0:adCost;
    }
    //没有调整以前的无用代码
    private double getOperationCost(String userunique, int firstPeriod, int lastPeriod) {
        double operationCost = 5;
        return operationCost;
    }

    private double getManagementCost(String userunique, int firstPeriod, int lastPeriod) {
        double mangementCost = 1 * 4;
        return mangementCost;
    }
    private double getDepreciationCost(String userunique, int firstPeriod, int lastPeriod) {
        Double itemCost = enterPriseEvaluateDao.getItemCost(userunique, "%折旧%", firstPeriod, lastPeriod);
        return itemCost == null?0:itemCost;
    }

    private double getInterestCost(String userunique, int firstPeriod, int lastPeriod) {
        Double itemCost = enterPriseEvaluateDao.getItemCost(userunique, "%利息%", firstPeriod, lastPeriod);
        return itemCost == null?0:itemCost;
    }




    private List<UserInputAndOutputOfAd> getUserInputAndOutput(int year, String groupName) {
        List<UserInputAndOutputOfAd> userIOs = enterPriseEvaluateDao.getGroupMembersOnIOAd(groupName);
        int firstPeriod = (year - 1) * 4 + 1;
        int lastPeriod = firstPeriod + 3;
        for (int i = 0; i < userIOs.size(); i++) {
            userIOs.get(i).setInput(getUserInput(userIOs.get(i).getUserunique(), firstPeriod));
            userIOs.get(i).setOutput(getUserOutput(userIOs.get(i).getUserunique(), firstPeriod, lastPeriod));
        }
        return userIOs;
    }

    private double getUserOutput(String userUnique, int firstPeriod, int lastPeriod) {
        Double userOutput = enterPriseEvaluateDao.getUserOutput(userUnique, firstPeriod, lastPeriod);
        return userOutput == null? 0:userOutput;
    }
    private double getUserInput(String userUnique, int period) {
        Double userInput = enterPriseEvaluateDao.getUserInput(userUnique,period);
        return userInput == null? 0:userInput;
    }

    private List<MemberSaleOfProduct> getMemberSalesByProduct(String groupName, String productName, int year) {
        List<GameGroupMemberDTO> members = enterPriseEvaluateDao.getGroupMember(groupName);
        List<MemberSaleOfProduct> memberSales = new ArrayList<MemberSaleOfProduct>(
                members.size());
        int firstPeriod = (year - 1) * 4 + 1;
        int lastPeriod = firstPeriod + 3;
        for (int i = 0; i < members.size(); i++) {
            MemberSaleOfProduct memberSale = new MemberSaleOfProduct();
            memberSale.setMember(members.get(i));
            memberSale.setProductName(productName);
            memberSale.setSale(getMemberSaleByProduct(memberSale.getMember().getUserUnique(), productName, firstPeriod, lastPeriod));
            memberSales.add(memberSale);
        }
        return memberSales;
    }

    private double getMemberSaleByProduct(String userunique, String productName, int firstPeriod, int lastPeriod) {
        Double memberSaleByProduct = enterPriseEvaluateDao.getMemberSaleByProduct(new ProductShareDTO(userunique, productName, firstPeriod, lastPeriod));
        return memberSaleByProduct == null?0:memberSaleByProduct;

    }

    private double getMemberSaleByMarket(String userunique, String marketname, int firstPeriod, int lastPeriod) {
        Double memberSaleByMarket = enterPriseEvaluateDao.getMemberSaleByMarket(new MarketShareDTO(userunique, marketname, firstPeriod, lastPeriod));
        return memberSaleByMarket == null ? 0:memberSaleByMarket;
    }

    private List<MemberSaleOfMarket> getMemberSalesByMarket(String groupName, String marketName, int year) {
        List<GameGroupMemberDTO> memberDTOList = enterPriseEvaluateDao.getGroupMember(groupName);
        List<MemberSaleOfMarket> memberSaleOfMarkets = new ArrayList<>(memberDTOList.size());
        int firstPeriod = (year - 1) * 4 + 1;
        int lastPeriod = firstPeriod + 3;
        for (int i = 0; i < memberDTOList.size(); i++) {
            MemberSaleOfMarket memberSale = new MemberSaleOfMarket();
            memberSale.setMember(memberDTOList.get(i));
            memberSale.setMarketname(marketName);
            memberSale.setSale(getMemberSaleByMarket(memberSale.getMember().getUserUnique(), marketName, firstPeriod, lastPeriod));
            memberSaleOfMarkets.add(memberSale);
        }
        return memberSaleOfMarkets;
    }

    private double getMemberCapacity(String userunique) {
        double capacity = 0;
        List<ProduceCapacity> produceCapacity = getProducePeriod(userunique);
        for (int i = 0; i < produceCapacity.size(); i++) {
            capacity += 1.0 / produceCapacity.get(i).getProducePeriod();
        }
        return capacity;

    }
    private List<ProduceCapacity> getProducePeriod(String userunique) {
        return enterPriseEvaluateDao.getMemberCapacity(userunique);
    }


    @Override
    public List<ProductProfit> getProductsProfit(String groupName, int year) {

        List<GameGroupMemberDTO> members = enterPriseEvaluateDao.getGroupMember(groupName);
        List<ProductProfit> productsProfits = new ArrayList<ProductProfit>();
        List<ProductProfit> list = new ArrayList<ProductProfit>();
        for (int i = 0; i < members.size(); i++) {
            productsProfits = getProductsProfitSub(members.get(i).getUserUnique(), year);
            for (int j = 0; j < productsProfits.size(); j++) {
                productsProfits.get(j).setMember(members.get(i));
                list.add(productsProfits.get(j));
            }
        }
        return list;
    }

    public List<ProductProfit> getProductsProfitSub(String userunique, int year) {
        List<ProductProfit> productsProfits = new ArrayList<ProductProfit>();
        int firstPeriod = (year - 1) * 4 + 1;
        int lastPeriod = firstPeriod + 3;
        productsProfits.add(getProductProfit(userunique, "P1", firstPeriod,
                lastPeriod));
        productsProfits.add(getProductProfit(userunique, "P2", firstPeriod,
                lastPeriod));
        productsProfits.add(getProductProfit(userunique, "P3", firstPeriod,
                lastPeriod));
        productsProfits.add(getProductProfit(userunique, "P4", firstPeriod,
                lastPeriod));
        return productsProfits;
    }

    private ProductProfit getProductProfit(String userunique,
                                           String productName, int firstPeriod, int lastPeriod) {
        List<Products> products = enterPriseEvaluateDao.getProductsByGroupName(userunique, productName, firstPeriod, lastPeriod);
        ProductProfit productProfit = new ProductProfit();
        double cost = 0;
        double sale = 0;
        for (int i = 0; i < products.size(); i++) {
            cost += getCostOfOrder(productName, products.get(i).getPnumber());
            sale += products.get(i).getPnumber() * products.get(i).getPrice();
        }
        productProfit.setProductname(productName);
        productProfit.setCost(cost);
        productProfit.setSale(sale);
        return productProfit;
    }
}
