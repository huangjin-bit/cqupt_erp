package com.cqupt.mislab.erpn.service.product.Impl;

import com.cqupt.mislab.erpn.commons.constant.ResultEnum;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.product.DevelopingProductDao;
import com.cqupt.mislab.erpn.dao.product.UnDevelopProductDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.product.DevelopingProduct;
import com.cqupt.mislab.erpn.model.entity.product.UnDevelopProduct;
import com.cqupt.mislab.erpn.service.product.UnDevelopProductService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author 张烈文
 */
@Service
@Slf4j
public class UnDevelopProductServiceImpl implements UnDevelopProductService {


    @Autowired
    private UnDevelopProductDao unDevelopProductDao;
    @Autowired
    private DevelopingProductDao developingProductDao;
    @Autowired
    private CommonDao commonDao;

    @Override
    public Result updateDevelopProduct(String userUnique, String productName) {
        //1.从未研发的产品表中取出要研发的产品
        UnDevelopProduct unDevelopProduct = unDevelopProductDao.findUnDevelopProductByUserUnique(userUnique, productName);
        //2.取出当前周期
        int beginTime = commonDao.findCurrentPeriod(userUnique);
        DevelopingProduct developingProduct = new DevelopingProduct(
                userUnique,
                productName,
                unDevelopProduct.getResearchPeriod(),
                unDevelopProduct.getResearchCost(),
                0, beginTime, 1);
        try {
        //3.将要研发的产品添加到developingProduct表中,如果添加失败则返回false，则不删除否则删除
            Boolean isOrAddSuccess=developingProductDao.addDevelopingProduct(developingProduct);
            if (isOrAddSuccess) {
                //4.删除未研发表中刚研发的产品
                unDevelopProductDao.deleteUnDevelopProduct(userUnique, productName);
            }
        } catch (Exception e) {
            log.error("【更新删除产品错误】error={}", e.getMessage());
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

    @Override
    public Result findUnDevelopProductByUserUnique(String userUnique) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("【获取未研发产品错误】userUnique不能为空");
            return ResultUtil.error();
        }
        List<UnDevelopProduct> unDevelopProducts = null;
        try {
            unDevelopProducts = unDevelopProductDao.findUnDevelopProductsByUserUnique(userUnique);
        }catch (Exception e){
            log.error("【获取未研发产品错误】error={}",e.getMessage());
            return ResultUtil.error(ResultEnum.ERROR.getMsg());
        }
        return ResultUtil.success(unDevelopProducts);
    }
//TODO 这个方法可以考虑省去
    @Override
    public Result findUnDevelopByUserUnique(String userUnique,String productName) {
        if (StringUtils.isEmpty(userUnique)||StringUtils.isEmpty(productName)) {
            log.error("【获取某个用户未研发产品失败】userUnique={},productName={}",userUnique,productName);
            return ResultUtil.error();
        }
        UnDevelopProduct unDevelopProduct = null;
        try {
            unDevelopProduct = unDevelopProductDao.findUnDevelopProductByUserUnique(userUnique,productName);
        } catch (Exception e) {
            log.error("【获取某个用户未研发产品失败】,error={}", e.getMessage());
           return ResultUtil.error();
        }
       return ResultUtil.success(unDevelopProduct);
    }

    @Override
    public Boolean deleteUnDevelopProduct(String userUnique,String productName) {
        if (StringUtils.isEmpty(userUnique)||StringUtils.isEmpty(productName)) {
            log.error("【删除用户某个未研发产品失败】,userUnique={},productName={}",userUnique,productName);
            return false;
        }
        try {
            unDevelopProductDao.deleteUnDevelopProduct(userUnique, productName);
        } catch (Exception e) {
            log.error("【删除用户某个未研发产品失败】error={}", e.getMessage());
            return false;
        }
        return true;
    }
}
