package com.cqupt.mislab.erpn.service.iso.impl;

import com.cqupt.mislab.erpn.dao.iso.UndevelopISODao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.iso.ISOUndevelop;
import com.cqupt.mislab.erpn.service.iso.UndevelopISOService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 16:31
 */
@Service
@Slf4j
public class UndevelopISOServiceImpl implements UndevelopISOService {
    @Autowired
    UndevelopISODao undevelopISODao;

    @Override
    public Result findAllISOUndevelop(String userUnique) {
        if(userUnique == null){
            log.error("【获取未研发ISO错误】userUnique=null");
            return ResultUtil.error();
        }
        List<ISOUndevelop> allIsoUndevelop = undevelopISODao.findAllIsoUndevelop(userUnique);
        return ResultUtil.success(allIsoUndevelop);
    }

    @Override
    public Result findOneUndevelop(String userUnique, String isoName) {
        if(userUnique == null||isoName ==  null){
            log.error("【获取指定未研发ISO错误】userUnique=null或者isoName=null");
            return ResultUtil.error();
        }
        ISOUndevelop oneIsoUndevelop = undevelopISODao.findOneIsoUndevelop(userUnique, isoName);
        return ResultUtil.success(oneIsoUndevelop);
    }
}
