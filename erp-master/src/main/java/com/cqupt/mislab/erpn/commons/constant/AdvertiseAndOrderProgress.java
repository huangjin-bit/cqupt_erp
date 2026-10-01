package com.cqupt.mislab.erpn.commons.constant;

import lombok.Data;

/**
 * @author 张烈文
 */

public enum AdvertiseAndOrderProgress {
    UN_PUT_INTO_MONEY(1, "未投放广告"),
    ALREADY_PUT_MONEY(2,"已投放广告"),
    WAIT_MEMBER_PUT(3,"投放广告完成，等待组员投放广告"),
    START_SELECT_ORDER(4,"开始选择订单"),
    ALREADY_FINISH_ORDER(5,"已经完成选择订单")
    ;
    private Integer progress;
    private String message;

    AdvertiseAndOrderProgress(Integer progress, String message) {
        this.progress = progress;
        this.message = message;
    }

    public Integer getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }
}
