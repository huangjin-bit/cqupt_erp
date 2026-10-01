package com.cqupt.mislab.erpn.service.iso.impl;

import com.cqupt.mislab.erpn.dao.iso.ISOBasicDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.iso.ISOBasic;
import com.cqupt.mislab.erpn.service.iso.BasicISOService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 15:35
 */
@Service
@Slf4j
public class BasicISOServiceImpl implements BasicISOService {
    @Autowired
    ISOBasicDao isoBasicDao;

    @Override
    public Result findAllISOBasic() {
        List<ISOBasic> allBasicISO = isoBasicDao.findAllBasicISO();
        return ResultUtil.success(allBasicISO);
    }

    @Override
    public Result findISOBasicNum() {
        Integer isoBasicNum = isoBasicDao.findISOBasicNum();
        return ResultUtil.success(isoBasicNum);
    }
}
