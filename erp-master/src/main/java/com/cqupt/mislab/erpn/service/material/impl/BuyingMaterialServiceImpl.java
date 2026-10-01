package com.cqupt.mislab.erpn.service.material.impl;


import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.material.BuyingMaterialDao;
import com.cqupt.mislab.erpn.dao.material.MaterialBasicDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.material.BuyingMaterial;
import com.cqupt.mislab.erpn.model.entity.material.MaterialBasic;
import com.cqupt.mislab.erpn.service.material.BuyingMaterialService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/16 0:52
 */
@Service
@Slf4j
public class BuyingMaterialServiceImpl implements BuyingMaterialService {

    @Autowired
    private BuyingMaterialDao buyingMaterialDao;
    @Autowired
    private CommonDao commonDao;

    @Autowired
    private MaterialBasicDao materialBasicDao;

    @Override
    public Result listBuyingMaterialUnReach(String userUnique, Integer pageIndex, Integer pageSize) {

        HashMap<String, Object> ans = new HashMap<>();
        if (StringUtils.isEmpty(userUnique)) {
            log.error("userUnique:{}", userUnique);
            return ResultUtil.error();
        }
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        PageHelper.startPage(pageIndex, pageSize);
        List<BuyingMaterial> buyingMaterials = buyingMaterialDao.listBuyingMaterial(userUnique, currentPeriod);
        PageInfo materialUnReach = new PageInfo(buyingMaterials);
        ans.put("materialUnReach", materialUnReach.getList());
        ans.put("pages", materialUnReach.getPages());
        ans.put("curPage", materialUnReach.getPageNum());
        ans.put("count", materialUnReach.getList().size());
        return ResultUtil.success(ans);
    }

    //TODO 这里指定了放在一号仓库下
    @Override
    public Result insertBuyingMaterial(String userUnique, String materialName, Integer mNumber) {

        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        MaterialBasic materialBasicByName = materialBasicDao.getMaterialBasicByName(materialName);
        int endTime = currentPeriod + materialBasicByName.getDelayTime();
        BuyingMaterial buyingMaterial = new BuyingMaterial();
        buyingMaterial.setMaterialName(materialName);
        buyingMaterial.setMNumber(mNumber);
        buyingMaterial.setUserUnique(userUnique);
        buyingMaterial.setWareHouseName("1号仓库");
        buyingMaterial.setHappenTime(currentPeriod);
        buyingMaterial.setEndTime(endTime);
        buyingMaterialDao.insertBuyingMaterial(buyingMaterial);
        return ResultUtil.success();
    }

}
