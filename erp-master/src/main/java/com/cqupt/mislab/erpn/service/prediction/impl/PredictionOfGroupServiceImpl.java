package com.cqupt.mislab.erpn.service.prediction.impl;

import com.cqupt.mislab.erpn.dao.prediction.PredictionOfGroupDao;
import com.cqupt.mislab.erpn.model.entity.prediction.PredictionOfGroup;
import com.cqupt.mislab.erpn.service.prediction.PredictionOfGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.service.impl
 * @description:
 * @author: keji0
 * @date: 2020/12/22 11:31
 */

@Slf4j
@Service
public class PredictionOfGroupServiceImpl implements PredictionOfGroupService {
    @Autowired
    PredictionOfGroupDao predictionOfGroupDao;

    @Override
    public Boolean addPredictionOfGroup(PredictionOfGroup predictionOfGroup) {
        return predictionOfGroupDao.addPredictionOfGroup(predictionOfGroup);
    }

    @Override
    public Boolean addPredictionOfGroups(List<PredictionOfGroup> predictionOfGroups) {
//        Boolean add = false;
//        for (PredictionOfGroup prediction : predictionOfGroups){
//            add = addPredictionOfGroup(prediction);
//            if (!add){
//                return false;
//            }
//        }
//        return true;
        return predictionOfGroupDao.addPredictionOfGroups(predictionOfGroups);
    }

    @Override
    public List<PredictionOfGroup> findAllPredictionOfGroup(String groupName) {
        return predictionOfGroupDao.findAllPredictionOfGroup(groupName);
    }
}
