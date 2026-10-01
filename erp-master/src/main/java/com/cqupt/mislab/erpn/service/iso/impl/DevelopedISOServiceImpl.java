package com.cqupt.mislab.erpn.service.iso.impl;

import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.iso.DevelopedISODao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.iso.ISODeveloped;
import com.cqupt.mislab.erpn.service.iso.DevelopedISOService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 15:49
 */
@Service
@Slf4j
public class DevelopedISOServiceImpl implements DevelopedISOService {
    @Autowired
    DevelopedISODao developedISODao;
    @Autowired
    CommonDao commonDao;

    @Override
    public Boolean isDevelopedISO(String userUnique, String ISOName){
        if(ISOName == null || "".equals(ISOName)){
            return true;
        }
        Integer res = developedISODao.findDevelopedIso(userUnique, ISOName);
        if(res == 1){
            return true;
        }
        return false;
    }
    @Override
    public Result findAllISODeveloped(String userUnique) {
        if (userUnique==null||StringUtils.isEmpty(userUnique)) {
            log.error("【获取已研发ISO错误】userUnique=null或为空");
            return ResultUtil.error();
        }
        List<ISODeveloped> allISODeveloped = developedISODao.findAllISODeveloped(userUnique);
        return ResultUtil.success(allISODeveloped);
    }

    @Override
    public Result updateISODevelopedStatusToOne(String userUnique, String isoName) {
        Integer result = developedISODao.updateISODevelopedStatus(userUnique, isoName, 1);
        if(result == 0){
            log.error("【暂停维护一个研发完成的ISO失败】");
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

    @Override
    public Result updateISODevelopedStatusToZero(String userUnique, String isoName) {
        Integer result = developedISODao.updateISODevelopedStatus(userUnique, isoName, 0);
        if(result == 0){
            log.error("【继续维护一个研发完成的ISO失败】");
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

    @Override
    public Result addISOToISODeveloped(ISODeveloped isoDeveloped) {
        if(isoDeveloped == null){
            log.error("【添加一个研发完成的ISO失败】isoDeveloped=null");
            return ResultUtil.error();
        }
        Integer integer = developedISODao.addIsoToDeveloped(isoDeveloped);
        if(integer == 0){
            log.error("【添加一个研发完成的ISO失败】");
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }
    // TODO update lastStatus?
}
