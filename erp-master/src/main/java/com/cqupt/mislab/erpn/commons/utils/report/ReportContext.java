package com.cqupt.mislab.erpn.commons.utils.report;

import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 张烈文
 * @date 2021/12/3 20:56
 */
@Component
public class ReportContext {

    @Qualifier("rayConcreteAlgorithm")
    @Autowired
    ReportAlgorithmStrategy concrete;


    public List<GameGroupMemberDetail> getScores(List < GameGroupMemberInfo > memberByGameGroup, Float maxScore, Float minScore) {
        return concrete.produceScore(memberByGameGroup, maxScore, minScore);
    }

}
