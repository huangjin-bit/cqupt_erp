package com.cqupt.mislab.erpn.commons.exception;

import lombok.Data;

/**
 * @Author xhx
 * @Date 2021/4/27 10:32
 */
@Data
public class RefuseConnectionException extends Exception{
    int code;

    public RefuseConnectionException(int code, String message) {
        super(message);
        this.code = code;
    }

    @Override
    public String toString() {
        return "MyException{" +
                "code=" + code +
                ", message='" + super.getMessage() + '\'' +
                '}';
    }

}
