package com.cqupt.mislab.erpn.service.report.Impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.report.ExperienceDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.report.Experience;
import com.cqupt.mislab.erpn.service.report.ExperienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 张烈文
 * @date 2021/10/6 22:16
 */
@Service
@Slf4j
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private ExperienceDao experienceDao;

    @Override
    public Result insertContext(Experience experience) {
        String userUnique = experience.getUserUnique();
        Experience experienceInData = experienceDao.selectOneByUserUnique(userUnique);
        log.info("experience:{}", experience);
        String context = experience.getContext().replaceAll("&nbsp;"," ");
//          1,如果数据库中没有该用户的实验心得，则插入
        try {
            if (experienceInData == null) {
                experienceDao.insertContext(userUnique, context);
            } else {
                experienceDao.modify(userUnique, context);
            }
        } catch (Exception e) {
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

    @Override
    public Result findContext(String userUnique) {
        Experience experience = experienceDao.selectOneByUserUnique(userUnique);
        return ResultUtil.success(experience);
    }
}
