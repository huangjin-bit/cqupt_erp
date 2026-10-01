package com.cqupt.mislab.erpn.service.forwardquarter;

import com.cqupt.mislab.erpn.model.Result;

/**
 * @author 张烈文
 * @date 2021/9/8 13:25
 */
public interface ForwardQuaterService {
    /**
     * 推进下一周期前检测跳转状态，用int，forwardStatus来判断
     * 0：用户已经破产
     * 1：用户正在经营
     * 2：年初但未完成广告费投放和订单选取确认
     * 3：游戏已经结束
     * @param userUnique
     * @return
     */
    Result forwardStatus(String userUnique);
}
