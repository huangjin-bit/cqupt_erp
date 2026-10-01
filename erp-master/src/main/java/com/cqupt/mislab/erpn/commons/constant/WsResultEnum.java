package com.cqupt.mislab.erpn.commons.constant;

/**
 * @author whl
 * @date 2020/12/17 - 23:27
 */
public enum WsResultEnum {
    UPDATE_GROUP(1,"更新分组"),
    UPDATE_MEMBER(2,"更新成员"),
    DELETE_GROUP(3,"解散分组"),
    KICK_MEMBER(4,"踢出成员"),
    START_GAME(5, "开始比赛"),
    ADVERTISE_COMPLETE(6, "投放广告完毕"),
    ALLMEMBERS_COMPLETE_ADVERTISE(7, "所有组员完成广告投放"),
    ORDER_CHOSEN(8,"订单选择成功"),
    BANKRUPT(9,"已破产");

    private Integer type;
    private String message;

    WsResultEnum(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
