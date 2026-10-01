package com.cqupt.mislab.erpn.service.factory.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.factory.FactoryBasicDao;
import com.cqupt.mislab.erpn.dao.factory.FactoryUsingDao;
import com.cqupt.mislab.erpn.dao.finance.cash.CashDao;
import com.cqupt.mislab.erpn.dao.finance.will.WillReceiveDao;
import com.cqupt.mislab.erpn.dao.productline.ProductLineDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryBasic;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryUsing;
import com.cqupt.mislab.erpn.model.entity.finance.will.WillReceive;
import com.cqupt.mislab.erpn.model.entity.productline.ProductLine;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryUsingVO;
import com.cqupt.mislab.erpn.model.vo.productline.ProductLineVO;
import com.cqupt.mislab.erpn.service.factory.FactoryUsingService;
import com.cqupt.mislab.erpn.service.finance.account.AccountDetailService;
import com.cqupt.mislab.erpn.service.finance.account.AccountHeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/11 23:02
 */
@Service
@Slf4j
public class FactoryUsingServiceImpl implements FactoryUsingService {


    @Autowired
    private FactoryUsingDao factoryUsingDao;
    @Autowired
    private ProductLineDao productLineDao;

    @Autowired
    private FactoryBasicDao factoryBasicDao;

    @Autowired
    private AccountHeadService accountHeadService;
    @Autowired
    private AccountDetailService accountDetailService;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private CashDao cashDao;

    @Autowired
    private WillReceiveDao willReceiveDao;
    @Override
    public  List<FactoryUsingVO> listFactoryUsingsByUserUnique(String userUnique) {

        if (StringUtils.isEmpty(userUnique)) {
            log.warn("userUnique:{}", userUnique);
            return null;
        }
        List<FactoryUsing> factoryUsings = factoryUsingDao.listFactoryUsingsByUserUnique(userUnique);
        List<FactoryUsingVO> factoryUsingVOS = new ArrayList<>();
        for (FactoryUsing factory : factoryUsings) {
            FactoryUsingVO factoryUsingVO = getFactoryUsingVO(factory, userUnique);
            factoryUsingVOS.add(factoryUsingVO);
        }

        return factoryUsingVOS;

    }

    @Override
    public Result listFactoryUsingByUserUniqueAndPlace(String userUnique, String place) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(place)) {
            log.error("userUnique:{},place:{}", userUnique, place);
            return ResultUtil.error(HttpServletResponse.SC_FORBIDDEN);
        }
        List<FactoryUsing> factoryUsings = factoryUsingDao.listFactoryUsingsByPlace(userUnique, place);
        List<FactoryUsingVO> factoryUsingVOS = new ArrayList<>();
        for (FactoryUsing factory : factoryUsings) {
            FactoryUsingVO factoryUsingVO = getFactoryUsingVO(factory, userUnique);
            factoryUsingVOS.add(factoryUsingVO);
        }

        return ResultUtil.success(factoryUsingVOS);

    }

    @Override
    public Result saleRemoveUsingFactory(String userUnique, Integer factoryId) {


        List<ProductLine> productLines = productLineDao.listProductLineByUserUniqueAndFactoryId(userUnique, String.valueOf(factoryId));
        if (productLines!=null&&productLines.size()>0) {
            return ResultUtil.error("该厂房存在生产线");
        }
//        1.会计分录
            FactoryUsing factoryUsing = factoryUsingDao.findUsingDetailById(userUnique, factoryId);
            String factoryType = factoryUsing.getFactoryType();
            Double A = Double.valueOf(factoryUsing.getSellPrice());
            FactoryBasic factoryBasic = factoryBasicDao.getFactoryBasic(factoryType);
            String saleCatagory = factoryBasic.getSellDescription();
            Double C = Double.valueOf(factoryBasic.getMakeCost() * factoryBasic.getMakePeriod());
            Double E = Double.valueOf(factoryBasic.getSellDifferentPrice());
            Double B = C - A;
            Double D = A - E;
            Integer happenTime = commonDao.findCurrentPeriod(userUnique);
            String description = "";
            if (saleCatagory.startsWith("现金方式")) {
                //插入第一条会计分录明细信息
                accountHeadService.addAccountHead(userUnique, happenTime, "采用现金交易方式出售厂房_1");
                accountDetailService.addAccountDetail(userUnique, "固定资产清理", "累计折旧", "厂房", "借", "借", "贷", A, B, (-C));
                //插入第二条会计分录明细信息
                accountHeadService.addAccountHead(userUnique, happenTime, "采用现金交易方式出售厂房_2");
                accountDetailService.addAccountDetail(userUnique, "现金", "固定资产清理", "借", "贷", D, D, (-D));
                //插入第三条会计分录信息（包括分录头和分录明细）
                accountHeadService.addAccountHead(userUnique, happenTime, "采用现金交易方式出售厂房_3");
                accountDetailService.addAccountDetail(userUnique, "其他业务支出", "固定资产清理", "借", "贷", E, E, (-E));
                //增加现金表中的数值
                cashDao.addCash(userUnique, D);
            } else {
                //采用正常交易方式出售厂房
                int endTime = factoryBasic.getDelayTime() + happenTime;
                //插入第一条会计分录信息
                accountHeadService.addAccountHead(userUnique, happenTime, "采用正常交易方式出售厂房_1");
                accountDetailService.addAccountDetail(userUnique, "固定资产清理", "累计折旧", "厂房", "借", "借", "贷", A, B, (-C));
                //插入第二条会计分录明细信息
                accountHeadService.addAccountHead(userUnique, happenTime, "采用正常交易方式出售厂房_2");
                accountDetailService.addAccountDetail(userUnique, "应收账款", "固定资产清理", "借", "贷", A, A, (-A));
                //处理应收账款表
                WillReceive receive = WillReceive.builder()
                        .userUnique(userUnique)
                        .beginTime(happenTime)
                        .money(A)
                        .endTime(endTime)
                        .willReceiveDescription("出售厂房的应收账款")
                        .note("")
                        .build();
                willReceiveDao.insertWillReceive(receive);
            }
//        删除factory_Using表中的信息
            factoryUsingDao.deleteFactory(userUnique, factoryId);
            return ResultUtil.success();
    }

    private FactoryUsingVO getFactoryUsingVO(FactoryUsing factory, String userUnique) {
        List<ProductLineVO> productLines = productLineDao.listProductLineVOByUserUniqueAndFactoryId(userUnique, factory.getFactoryId());
        FactoryUsingVO factoryUsingVO = new FactoryUsingVO();
        BeanUtils.copyProperties(factory, factoryUsingVO);
        factoryUsingVO.setProductLines(productLines);
        FactoryBasic factoryBasic = factoryBasicDao.getFactoryBasic(factory.getFactoryType());
        factoryUsingVO.setProductLineCapacity(factoryBasic.getCapacity());
        return factoryUsingVO;

    }
}
