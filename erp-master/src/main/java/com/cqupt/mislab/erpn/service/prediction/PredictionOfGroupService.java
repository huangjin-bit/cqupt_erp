package com.cqupt.mislab.erpn.service.prediction;

import com.cqupt.mislab.erpn.model.entity.prediction.PredictionOfGroup;

import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.service
 * @description:
 * @author: keji0
 * @date: 2020/12/22 11:29
 */
public interface PredictionOfGroupService {

    /**
     * 添加市场预测信息
     * @param predictionOfGroup
     * @return
     */
    Boolean addPredictionOfGroup(PredictionOfGroup predictionOfGroup);

    Boolean addPredictionOfGroups(List<PredictionOfGroup> predictionOfGroups);

    List<PredictionOfGroup> findAllPredictionOfGroup(String groupName);
}
