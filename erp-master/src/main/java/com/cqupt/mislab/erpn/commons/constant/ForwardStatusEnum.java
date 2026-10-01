package com.cqupt.mislab.erpn.commons.constant;

import lombok.Getter;

/**
 * @author 张烈文
 */

@Getter
public enum ForwardStatusEnum {
    BANKRUPTCY("用户已破产", 0),
    RECOMMEND_A_CYCLE("推进下一周期", 1),
    ADVERTISEMENT_AND_ORDER_NOT_YET("请完成广告费投放和选订单", 2),
    GAME_OVER("游戏已结束", 3);

    private String message;
    private int status;

    ForwardStatusEnum(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
