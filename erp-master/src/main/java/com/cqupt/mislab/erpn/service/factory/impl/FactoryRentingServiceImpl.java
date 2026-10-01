package com.cqupt.mislab.erpn.service.factory.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.factory.FactoryBasicDao;
import com.cqupt.mislab.erpn.dao.factory.FactoryRentingDao;
import com.cqupt.mislab.erpn.dao.productline.ProductLineDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.factory.FactoryRentingDTO;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryBasic;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryRenting;
import com.cqupt.mislab.erpn.model.entity.productline.ProductLine;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryRentingVO;
import com.cqupt.mislab.erpn.model.vo.productline.ProductLineVO;
import com.cqupt.mislab.erpn.service.factory.FactoryRentingService;
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
 * @date 2021/7/12 20:41
 */
@Service
@Slf4j
public class FactoryRentingServiceImpl implements FactoryRentingService {

    @Autowired
    private FactoryRentingDao factoryRentingDao;

    @Autowired
    private ProductLineDao productLineDao;

    @Autowired
    private FactoryBasicDao factoryBasicDao;
    @Autowired
    private CommonDao commonDao;
    @Override
    public List<FactoryRentingVO> listFactoryRentingByUserUnique(String userUnique) {

        if (StringUtils.isEmpty(userUnique)) {
            log.error("userUnique:{}", userUnique);
            return null;
        }
        List<FactoryRenting> factoryRentings = factoryRentingDao.listFactoryRentingsByUserUnique(userUnique);
        List<FactoryRentingVO> factoryRentingVOS = new ArrayList<>();
        for (FactoryRenting factory:factoryRentings) {
            FactoryRentingVO factoryRentingVO = packgeFactoryRentingVO(userUnique, factory);
            factoryRentingVOS.add(factoryRentingVO);
        }

        return factoryRentingVOS;

    }

    @Override
    public Result listFactoryRentingByUserUniqueAndPlace(String userUnique, String place) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(place)) {
            log.warn("userUnique:{},place:{}", userUnique, place);
            return ResultUtil.error(HttpServletResponse.SC_FORBIDDEN);
        }
        List<FactoryRenting> factoryRentings = factoryRentingDao.listFactoryRentingsByPlace(userUnique, place);
        List<FactoryRentingVO> factoryRentingVOS = new ArrayList<>();
        for (FactoryRenting factory:factoryRentings) {
            FactoryRentingVO factoryRentingVO = packgeFactoryRentingVO(userUnique, factory);
            factoryRentingVOS.add(factoryRentingVO);
        }

        return ResultUtil.success(factoryRentingVOS);

    }

    @Override
    public Result insertRentingFactory(String userUnique, FactoryRentingDTO factoryRentingDTO) {
        FactoryRenting factoryRenting = new FactoryRenting();
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        BeanUtils.copyProperties(factoryRentingDTO, factoryRenting);
        factoryRenting.setUserUnique(userUnique);
        factoryRenting.setBeginTime(currentPeriod);
        if (factoryRentingDao.insertFactoryRenting(factoryRenting)) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @Override
    public Result removeRentingFactory(String userUnique, Integer factoryId) {
        if (factoryRentingDao.deleteFactoryRenting(userUnique, factoryId)) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    private FactoryRentingVO packgeFactoryRentingVO(String userUnique, FactoryRenting factory) {
        FactoryRentingVO factoryRentingVO = new FactoryRentingVO();
        List<ProductLineVO> productLines = productLineDao.listProductLineVOByUserUniqueAndFactoryId(userUnique, factory.getFactoryId());
        BeanUtils.copyProperties(factory, factoryRentingVO);
        factoryRentingVO.setProductLines(productLines);
        FactoryBasic factoryBasic = factoryBasicDao.getFactoryBasic(factory.getFactoryType());
        factoryRentingVO.setProductLineCapacity(factoryBasic.getCapacity());
        return factoryRentingVO;

    }
}
