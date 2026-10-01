package com.cqupt.mislab.erpn.dao.prediction;

import com.cqupt.mislab.erpn.model.entity.prediction.PredictionOfGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.dao
 * @description:
 * @author: keji0
 * @date: 2020/12/22 11:33
 */
@Repository
@Mapper
public interface PredictionOfGroupDao {

//    //添加预测信息
    Boolean addPredictionOfGroup(PredictionOfGroup predictionOfGroup);

    /**
     * 取出游戏组所有的预测信息
     * @param groupName
     * @return
     */
    List<PredictionOfGroup> findAllPredictionOfGroup(String groupName);

    /**
     * 批量添加某游戏组的预测信息到数据库
     * @param list
     * @return
     */
    Boolean addPredictionOfGroups(@Param("list") List<PredictionOfGroup> list);
//
//    List<PredictionOfGroupVO> findPredictionOfGroupByMarketName(String marketName, String groupName);

}
