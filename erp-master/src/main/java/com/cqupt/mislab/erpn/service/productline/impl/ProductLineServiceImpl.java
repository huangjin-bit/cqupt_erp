package com.cqupt.mislab.erpn.service.productline.impl;

import com.cqupt.mislab.erpn.commons.constant.MaterialEnum;
import com.cqupt.mislab.erpn.commons.constant.ProductLineTypeEnum;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.finance.will.WillReceiveDao;
import com.cqupt.mislab.erpn.dao.material.MaterialBasicDao;
import com.cqupt.mislab.erpn.dao.material.MaterialOfUserDao;
import com.cqupt.mislab.erpn.dao.product.ProductBasicDao;
import com.cqupt.mislab.erpn.dao.productline.ProductLineBasicDao;
import com.cqupt.mislab.erpn.dao.productline.ProductLineDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.productline.ProductLineDTO;
import com.cqupt.mislab.erpn.model.entity.finance.will.WillReceive;
import com.cqupt.mislab.erpn.model.entity.material.MaterialBasic;
import com.cqupt.mislab.erpn.model.entity.productline.ProductLine;
import com.cqupt.mislab.erpn.model.entity.productline.ProductLineBasic;
import com.cqupt.mislab.erpn.model.vo.productline.ProductLineVO;
import com.cqupt.mislab.erpn.service.factory.FactoryCommonService;
import com.cqupt.mislab.erpn.service.finance.account.AccountDetailService;
import com.cqupt.mislab.erpn.service.finance.account.AccountHeadService;
import com.cqupt.mislab.erpn.service.productline.ProductLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 张烈文
 * @date 2021/7/15 18:09
 */
@Service
@Slf4j
public class ProductLineServiceImpl implements ProductLineService {


    @Autowired
    private CommonDao commonDao;
    @Autowired
    private ProductLineDao productLineDao;

    @Autowired
    private ProductLineBasicDao productLineBasicDao;
    @Autowired
    private MaterialOfUserDao materialOfUserDao;
    @Autowired
    private ProductBasicDao productBasicDao;
    @Autowired
    private MaterialBasicDao materialBasicDao;

    @Autowired
    private AccountHeadService accountHeadService;
    @Autowired
    private AccountDetailService accountDetailService;

    @Autowired
    private WillReceiveDao willReceiveDao;


    @Override
    public Result insertProductLine(String userUnique, ProductLineDTO productLineDTO) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("userUnique:{}", userUnique);
            return ResultUtil.error();
        }
//      TODO 是否需要增加判断生产线数量已达最大值，遇到bug再改吧
        String factoryId = productLineDTO.getFactoryId();
        ProductLine productLine = new ProductLine();
        BeanUtils.copyProperties(productLineDTO, productLine);
        productLine.setUserUnique(userUnique);
        Boolean flag = productLineDao.insertProductLine(productLine);
        if (!flag) {
            ResultUtil.error();
        }

        List<ProductLineVO> productLines = productLineDao.listProductLineVOByUserUniqueAndFactoryId(userUnique, factoryId);
        log.info("ProductLineServiceImpl ---> insertProductLine  ---> productLines:{},userUnique:{},factoryId:{}", productLines, userUnique, factoryId);


        return ResultUtil.success(productLines);
}

    @Override
    public Result removeInstallProductLine(String userUnique, Integer productLineId) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId)) {
            log.error("userUnique:{},productLineId:{}", userUnique, productLineId);
            return ResultUtil.error();
        }
        productLineDao.deleteProductLine(userUnique, productLineId);
        return ResultUtil.success();
    }

    @Override
    public Result UpdateSuspendedInstallation(String userUnique, Integer productLineId) {


        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId)) {
            log.error("userUnique:{},productLineId:{}", userUnique, productLineId);
            return ResultUtil.error();
        }
        productLineDao.updateStatus(userUnique, productLineId, 1);
        ProductLine andProductLineId = productLineDao.getProductLineByUserUniqueAndProductLineId(userUnique, productLineId);
        return ResultUtil.success(andProductLineId);
    }

    @Override
    public Result updateBeginInstallation(String userUnique, Integer productLineId) {

        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId)) {
            log.error("userUnique:{},productLineId:{}", userUnique, productLineId);
            return ResultUtil.error();
        }
        productLineDao.updateStatus(userUnique, productLineId, 0);
        ProductLine andProductLineId = productLineDao.getProductLineByUserUniqueAndProductLineId(userUnique, productLineId);
        return ResultUtil.success(andProductLineId);
    }

    @Override
    public Result updateBeginProduct(String userUnique, Integer productLineId) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId)) {
            log.error("userUnique:{},productLineId:{}", userUnique, productLineId);
            return ResultUtil.error();
        }
        //根据用户上锁
        synchronized (userUnique){
        //判断当前生产线的状态，是否为待生产，如果是的话直接返回（防止越过前端进行访问）
        if(productLineDao.getProductLineByUserUniqueAndProductLineId(userUnique,productLineId).getStatus()!=4){
//            log.error("userUnique:{},productLineId:{},正在进行不规范访问", userUnique, productLineId);
            return ResultUtil.error();
        }

//        1.获取生产线上的产品类型
        String productName = productLineDao.findProductNameByProductLineId(userUnique, productLineId);
//        2.查看生产该产品所需的原料名称及数量，若库存不足则返回
        synchronized (userUnique) {
            List<String> materialNames = productBasicDao.findMaterialNameByProductName(productName);
            for (String materialName : materialNames) {
                int materialNumber = productBasicDao.findMaterialNumber(productName, materialName);
                int inventoryWithMaterialName = materialOfUserDao.findInventoryWithMaterialName(userUnique, materialName);
                if (inventoryWithMaterialName < materialNumber) {
                    HashMap<String, Object> ans = new HashMap<>();
                    ans.put("code", MaterialEnum.INSUFFICIENT_RAW_MATERIAL_INVENTORY.getCode());
                    ans.put("message", MaterialEnum.INSUFFICIENT_RAW_MATERIAL_INVENTORY.getMessage());
                    return ResultUtil.success(ans);
                }

            }

//        3.计算产品单位成本价,并更改库存
            double A = 0;
            for (String materialName : materialNames) {
                int materialNumber = productBasicDao.findMaterialNumber(productName, materialName);
                MaterialBasic materialBasicByName = materialBasicDao.getMaterialBasicByName(materialName);
                int price = materialBasicByName.getPrice();   // 这里老版本使用的double接收，但是新版中数据库price字段是int类型
                A += materialNumber * price;  // 会计分录里的A值
//          更改库存
                materialOfUserDao.updateMaterialInventory(userUnique, materialName, materialNumber);
            }

//        4.更改生产线的状态
            productLineDao.updateStatus(userUnique, productLineId, 2);

//        5.插入会计分录信息
            Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
            accountHeadService.addAccountHead(userUnique, currentPeriod, "生产产品");
            accountDetailService.addAccountDetail(userUnique, "在制品", "原材料", "借", "贷", A, A, (-A));
            return ResultUtil.success();
        }
        }
    }

    @Override
    public Result updateRecoverProduct(String userUnique, Integer productLineId) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId)) {
            log.error("userUnique:{},productLineId:{}", userUnique, productLineId);
            return ResultUtil.error();
        }
        productLineDao.updateStatus(userUnique, productLineId, 2);
        return ResultUtil.success();
    }

    @Override
    public Result updateSuspendedProduct(String userUnique, Integer productLineId) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId)) {
            log.error("userUnique:{},productLineId:{}", userUnique, productLineId);
            return ResultUtil.error();
        }
        productLineDao.updateStatus(userUnique, productLineId, 3);
        return ResultUtil.success();
    }

    @Override
    public Result updateBeginChangeProduct(String userUnique, Integer productLineId, String productName) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId) || StringUtils.isEmpty(productName)) {
            log.error("userUnique:{},productLineId:{},productName:{}", userUnique, productLineId, productName);
            return ResultUtil.error();
        }
        ProductLine ansProductLine = productLineDao.getProductLineByUserUniqueAndProductLineId(userUnique, productLineId);
        String productLineType = ansProductLine.getProductLineType();
        if (ProductLineTypeEnum.MANUAL_PRODUCT_LINE.getProductLineType().equals(productLineType) || ProductLineTypeEnum.FLEXIBLE_PRODUCT_LINE.getProductLineType().equals(productLineType)) {
            productLineDao.updateBeginChangeProductStatus(userUnique, productLineId, productName, 4);
        } else if (ProductLineTypeEnum.SEMI_AUTOMATIC_PRODUCT_LINE.getProductLineType().equals(productLineType)) {
            productLineDao.updateBeginChangeProductStatus(userUnique, productLineId, productName, 5);
        } else {
            productLineDao.updateBeginChangeProductStatus(userUnique, productLineId, productName, 5);
        }
        ansProductLine = productLineDao.getProductLineByUserUniqueAndProductLineId(userUnique, productLineId);
        List<ProductLineVO> productLineVOS = productLineDao.listProductLineVOByUserUniqueAndFactoryId(userUnique, ansProductLine.getFactoryId());
        ProductLineVO ansProductLineVO = null;
        for (ProductLineVO productLineVO : productLineVOS) {
            if (Integer.valueOf(productLineVO.getProductLineId()).equals(productLineId)) {
                ansProductLineVO = productLineVO;
            }
        }
        return ResultUtil.success(ansProductLineVO);
    }

    @Override
    public Result updateSuspendChangeProduct(String userUnique, Integer productLineId) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId)) {
            log.error("userUnique:{},productLineId:{}", userUnique, productLineId);
            return ResultUtil.error();
        }
        ProductLine andProductLineId = productLineDao.getProductLineByUserUniqueAndProductLineId(userUnique, productLineId);
        productLineDao.updateBeginChangeProductStatus(userUnique, productLineId, andProductLineId.getProductName(), 6);
        List<ProductLineVO> productLineVOS = productLineDao.listProductLineVOByUserUniqueAndFactoryId(userUnique, andProductLineId.getFactoryId());
        ProductLineVO ansProductLineVo = null;
        for (ProductLineVO productLineVO : productLineVOS) {
            if (Integer.valueOf(productLineVO.getProductLineId()).equals(productLineId)) {
                ansProductLineVo = productLineVO;
            }
        }
        return ResultUtil.success(ansProductLineVo);
    }

    @Override
    public Result updateRecoverChangeProduct(String userUnique, Integer productLineId) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(productLineId)) {
            log.error("userUnique:{},productLineId:{}", userUnique, productLineId);
            return ResultUtil.error();
        }
        ProductLine andProductLineId = productLineDao.getProductLineByUserUniqueAndProductLineId(userUnique, productLineId);
        productLineDao.updateBeginChangeProductStatus(userUnique, productLineId, andProductLineId.getProductName(), 5);
        List<ProductLineVO> productLineVOS = productLineDao.listProductLineVOByUserUniqueAndFactoryId(userUnique, andProductLineId.getFactoryId());
        ProductLineVO ansProductLineVo = null;
        for (ProductLineVO productLineVO : productLineVOS) {
            if (Integer.valueOf(productLineVO.getProductLineId()).equals(productLineId)) {
                ansProductLineVo = productLineVO;
            }
        }
        return ResultUtil.success(ansProductLineVo);
    }

    @Override
    public Result deleteSaleProductLine(String userUnique, Integer productLineId, String productLineType) {
        try {
            Double A = Double.valueOf(productLineDao.findSellPriceByProductLineId(userUnique, productLineId));
            ProductLineBasic productLineBasic = productLineBasicDao.getProductLineBasic(productLineType);
            Float tempC = productLineBasic.getSetupPeriod() * productLineBasic.getSetupPeriodPrice();
            Double C = Double.valueOf(tempC);
            Double B = C - A;
            int happenTime = commonDao.findCurrentPeriod(userUnique);
            int endTime = productLineBasic.getDelayTime() + happenTime;
            //插入第一条会计分录信息。
            accountHeadService.addAccountHead(userUnique, happenTime, "出售生产线_1");
            accountDetailService.addAccountDetail(userUnique, "固定资产清理", "累计折旧", "生产线", "借", "借", "贷", A, B, (-C));
            //插入第二条会计分录明细信息
            accountHeadService.addAccountHead(userUnique, happenTime, "出售生产线_2");
            accountDetailService.addAccountDetail(userUnique, "应收账款", "固定资产清理", "借", "贷", A, A, (-A));
            //处理应收账款表
            WillReceive receive = WillReceive.builder()
                    .userUnique(userUnique)
                    .beginTime(happenTime)
                    .money(A)
                    .endTime(endTime)
                    .willReceiveDescription("出售生产线的应收账款")
                    .note("")
                    .build();
            willReceiveDao.insertWillReceive(receive);
            //删除factory_Using表中的信息。
            productLineDao.deleteProductLine(userUnique, productLineId);
        } catch (Exception e) {
            log.error("出售生产线报错啦！！");
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

    @Override
    public Result basicProductLineVo() {

        List<ProductLineBasic> productLineVOS = productLineDao.listProductLineBasicVo();
        log.info("生产线基本信息：{}", productLineVOS);
        return ResultUtil.success(productLineVOS);
    }

}
