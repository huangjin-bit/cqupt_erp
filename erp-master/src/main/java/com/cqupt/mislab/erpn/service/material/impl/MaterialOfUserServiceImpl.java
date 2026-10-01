package com.cqupt.mislab.erpn.service.material.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.material.MaterialBasicDao;
import com.cqupt.mislab.erpn.dao.material.MaterialOfUserDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.material.MaterialBasic;
import com.cqupt.mislab.erpn.model.entity.material.MaterialOfUser;
import com.cqupt.mislab.erpn.model.vo.material.MaterialVO;
import com.cqupt.mislab.erpn.service.material.MaterialOfUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/16 0:51
 */
@Service
@Slf4j
public class MaterialOfUserServiceImpl implements MaterialOfUserService {

    @Autowired
    private MaterialOfUserDao materialOfUserDao;


    @Override
    public Result listMaterialVO(String userUnique) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("userUnique:{}", userUnique);
            return ResultUtil.error();
        }
        List<MaterialVO> materialVOS = materialOfUserDao.listMaterialVO(userUnique);
        return ResultUtil.success(materialVOS);

    }

    @Override
    public Result listMaterialsByUserUnique(String userUnique) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("userUnique:{}", userUnique);
            return ResultUtil.error();
        }
        return ResultUtil.success(materialOfUserDao.listMaterialByUserUnique(userUnique));
    }

}
