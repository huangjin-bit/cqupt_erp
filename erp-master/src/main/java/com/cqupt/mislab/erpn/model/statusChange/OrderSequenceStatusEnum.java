package com.cqupt.mislab.erpn.model.statusChange;

/**
 * @Author xhx
 * @Date 2021/7/12 12:04
 */
public enum OrderSequenceStatusEnum {
    USER_CHOOSING(1,"等待当前用户选择该产品的订单"),
    WAITING_CHOOSE(2,"为轮到该用户选择该产品的订单"),
    ISO_VERIFY_FAILED(3,"ISO未研发成功，选单失败"),
    MARKET_VERIFY_FAILED(4,"该市场没有开拓完成"),
    ORDER_STATUS_EXCEPTION(5,"订单状态异常，不能选择");


    private Integer statusCode;
    private String message;

    OrderSequenceStatusEnum(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
