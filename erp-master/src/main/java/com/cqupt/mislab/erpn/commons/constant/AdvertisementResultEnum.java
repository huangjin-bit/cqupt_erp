package com.cqupt.mislab.erpn.commons.constant;


import lombok.Data;

/**
 * @author 张烈文
 */

public enum AdvertisementResultEnum {

    FINiSH_ADVERTISEMENT_SUCCESS(0,"投放成功"),
    FINISH_ADVERTISEMENT_EXIST(1,"已进行过广告投放"),
    FINISH_ADVERTISEMENT_FAIL(2,"投放失败,投钱太多啦!!");
    private Integer status;
    private String message;

    AdvertisementResultEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
