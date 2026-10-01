package com.cqupt.mislab.erpn.service.report;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.report.Experience;

/**
 * @author 张烈文
 * @date 2021/10/6 22:12
 */
public interface ExperienceService {


    /**
     * 插入实验心得
     * @param experience
     * @return
     */
    Result insertContext(Experience experience);


    /**
     * 查询实验心得
     * @param userUnique
     * @return
     */
    Result findContext(String userUnique);
}
