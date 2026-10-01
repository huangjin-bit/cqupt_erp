package com.cqupt.mislab.erpn.dao.report;

import com.cqupt.mislab.erpn.model.entity.report.Experience;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 张烈文
 * @date 2021/10/6 21:53
 */
@Mapper
public interface ExperienceDao {


    /**
     * 增加实验心得
     * @param userUnique
     * @param context
     * @return
     */
    boolean insertContext(@Param("userUnique") String userUnique, @Param("context") String context);

    /**
     * 修改实验心得
     *
     * @param userUnique
     * @param context
     * @return
     */
    boolean modify(@Param("userUnique") String userUnique, @Param("context") String context);

    /**
     * 查询实验心得
     * @param userUnique
     * @return
     */
    Experience selectOneByUserUnique(String userUnique);

}
