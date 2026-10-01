package com.cqupt.mislab.erpn.service.iso.impl;

import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.iso.DevelopingISODao;
import com.cqupt.mislab.erpn.dao.iso.ISOBasicDao;
import com.cqupt.mislab.erpn.dao.iso.UndevelopISODao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.iso.ISOBasic;
import com.cqupt.mislab.erpn.model.entity.iso.ISODeveloping;
import com.cqupt.mislab.erpn.model.entity.iso.ISOUndevelop;
import com.cqupt.mislab.erpn.service.iso.DevelopingISOService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 16:13
 */
@Service
@Slf4j
public class DevelopingISOServiceImpl implements DevelopingISOService {
    @Autowired
    DevelopingISODao developingISODao;
    @Autowired
    ISOBasicDao isoBasicDao;
    @Autowired
    CommonDao commonDao;
    @Autowired
    UndevelopISODao undevelopISODao;
    @Override
    public Result findAllISODeveloping(String userUnique) {
        if (userUnique == null) {
            log.error("【获取已研发ISO错误】userUnique=null");
            return ResultUtil.error();
        }
        List<ISODeveloping> allDevelopingIso = developingISODao.findAllDevelopingIso(userUnique);
        return ResultUtil.success(allDevelopingIso);
    }

    @Override
    public Result updateISODevelopingStatusToOne(String userUnique, String isoName) {
        if (userUnique == null || isoName == null) {
            log.error("【暂停研发一个ISO失败】userUnique=null或isoName=null");
            return ResultUtil.error();
        }
        Integer result = developingISODao.updateIsoDevelopingStatus(userUnique, isoName, 1);
        if(result ==  0){
            log.error("【继续维护一个研发完成的ISO失败】");
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

    @Override
    public Result updateISODevelopingStatusToZero(String userUnique, String isoName) {
        if (userUnique == null || isoName == null) {
            log.error("【继续研发一个ISO错误】userUnique=null或isoName=null");
            return ResultUtil.error();
        }
        Integer result = developingISODao.updateIsoDevelopingStatus(userUnique, isoName, 1);
        if(result ==  0){
            log.error("【继续维护一个研发完成的ISO失败】");
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

    @Override
    public Result addISOToISODeveloping(String userUnique,String isoName) {
        if (userUnique == null || isoName == null) {
            log.error("【开始研发一个ISO错误】userUnique=null或isoName=null");
            return ResultUtil.error();
        }
        ISOBasic basic = isoBasicDao.findOneISOBasic(isoName);
        ISODeveloping newISODeveloping = new ISODeveloping(userUnique,
                isoName,
                basic.getResearchPeriod(),
                basic.getResearchCost(),0,commonDao.findCurrentPeriod(userUnique),0);
        Integer result = developingISODao.addIsoToDeveloping(newISODeveloping);
        if(result ==  0){
            log.error("【添加一个研发中的ISO失败】");
            return ResultUtil.error();
        }
        Integer integer = undevelopISODao.deleteIsoUndevelop(userUnique, isoName);
        return ResultUtil.success();
    }
}
