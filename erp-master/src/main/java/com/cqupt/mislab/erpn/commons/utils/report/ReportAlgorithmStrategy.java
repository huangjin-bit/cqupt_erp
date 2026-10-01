package com.cqupt.mislab.erpn.commons.utils.report;

import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;

import java.util.List;

/**
 * 打分算法的公共接口
 * @author 张烈文
 * @date 2021/12/3 19:34
 */
public interface ReportAlgorithmStrategy {

    /**
     * 方法：提供给后来改变打分算法的使用者使用
     * @param memberByGameGroup
     * @param maxScore
     * @param minScore
     * @return
     */
    List<GameGroupMemberDetail> produceScore(List<GameGroupMemberInfo> memberByGameGroup, Float maxScore, Float minScore);
}
