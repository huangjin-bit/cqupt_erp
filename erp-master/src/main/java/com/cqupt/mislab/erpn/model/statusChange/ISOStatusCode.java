package com.cqupt.mislab.erpn.model.statusChange;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * @Author xhx
 * @Date 2020/11/20 21:11
 */
public enum ISOStatusCode {
    //研发中status=1，停止状态为=0；
    ISOBasic_Developing(1,"研发中"),
    ISOBasic_Stopped(0,"研发暂停");
    private Integer StatusCode;
    private String message;

    ISOStatusCode(int index,String message) {
            this.StatusCode = index;
            this.message = message;
    }
    @JsonValue
    public Integer getIndex() {
        return StatusCode;
    }

    public String getMessage() {
        return message;
    }
}

