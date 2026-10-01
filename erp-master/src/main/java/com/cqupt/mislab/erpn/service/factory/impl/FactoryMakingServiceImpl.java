package com.cqupt.mislab.erpn.service.factory.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.factory.FactoryBasicDao;
import com.cqupt.mislab.erpn.dao.factory.FactoryMakingDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.factory.FactoryMakingDTO;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryBasic;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryMaking;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryMakingVO;
import com.cqupt.mislab.erpn.service.factory.FactoryMakingService;
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
 * @date 2021/7/12 21:55
 */
@Service
@Slf4j
public class FactoryMakingServiceImpl implements FactoryMakingService {

    @Autowired
    private FactoryMakingDao factoryMakingDao;

    @Autowired
    private FactoryBasicDao factoryBasicDao;

    @Autowired
    private CommonDao commonDao;
    @Override
    public List<FactoryMakingVO> listFactoryMakingByUserUnique(String userUnique) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("userUnique:{}", userUnique);
            return null;
        }
        List<FactoryMakingVO> factoryMakingVOS = new ArrayList<>();
        List<FactoryMaking> factoryMakings = factoryMakingDao.listFactoryMakings(userUnique);
        for (FactoryMaking factory: factoryMakings) {
            FactoryMakingVO factoryMakingVO = packagesFactoryMaking(factory);
            factoryMakingVOS.add(factoryMakingVO);
        }

        return factoryMakingVOS;

    }

    @Override
    public Result listFactoryMakingByUserUniqueAndPlace(String userUnique, String place) {
        if (StringUtils.isEmpty(userUnique) || StringUtils.isEmpty(place)) {
            log.warn("userUnique:{},place:{}", userUnique, place);
            return ResultUtil.error(HttpServletResponse.SC_FORBIDDEN);
        }
        List<FactoryMakingVO> factoryMakingVOS = new ArrayList<>();
        List<FactoryMaking> factoryMakings = factoryMakingDao.listFactoryMakingsByPlace(userUnique, place);
        for (FactoryMaking factory: factoryMakings) {
            FactoryMakingVO factoryMakingVO = packagesFactoryMaking(factory);
            factoryMakingVOS.add(factoryMakingVO);
        }

        return ResultUtil.success(factoryMakingVOS);

    }

    @Override
    public Result insertMakingFactory(String userUnique, FactoryMakingDTO factoryMakingDTO) {
        FactoryMaking factoryMaking = new FactoryMaking();
        BeanUtils.copyProperties(factoryMakingDTO, factoryMaking);
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        factoryMaking.setBeginTime(currentPeriod);

        factoryMaking.setFinishedPeriod(0);
        factoryMaking.setUserUnique(userUnique);
        factoryMaking.setStatus(1);

        if (factoryMakingDao.insertFactory(factoryMaking)) {
            return ResultUtil.success();
        }

        return ResultUtil.error();

    }

    @Override
    public Result stopMakingFactory(String userUnique, Integer factoryId) {
        if (factoryMakingDao.stopMakingFactory(userUnique, factoryId)) {
            return ResultUtil.success();
        }

        return ResultUtil.error();

    }

    @Override
    public Result keepMakingFactory(String userUnique, Integer factoryId) {
        if (factoryMakingDao.keepMakingFactory(userUnique, factoryId)) {
            return ResultUtil.success();
        }

        return ResultUtil.error();

    }

    private FactoryMakingVO packagesFactoryMaking(FactoryMaking factory) {
        FactoryMakingVO factoryMakingVO = new FactoryMakingVO();
        BeanUtils.copyProperties(factory, factoryMakingVO);
        FactoryBasic factoryBasic = factoryBasicDao.getFactoryBasic(factory.getFactoryType());
        factoryMakingVO.setMakePeriod(factoryBasic.getMakePeriod());
        factoryMakingVO.setProductLineCapacity(factoryBasic.getCapacity());


        return factoryMakingVO;

    }
}
