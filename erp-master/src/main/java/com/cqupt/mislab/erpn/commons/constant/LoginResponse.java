package com.cqupt.mislab.erpn.commons.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.commons.constant
 * @description:
 * @author: keji0
 * @date: 2020/7/28 9:33
 */
@AllArgsConstructor
@Slf4j
@ToString
public class LoginResponse<T> {
    private int status;
    private String message;
    private T data;

    public static LoginResponse newResponse(){
        return new LoginResponse<>(LoginCode.WRONG_PASSWORD,LoginMessage.TEACHER_WRONG_PASSWORD,null);
    }
    public static LoginResponse codeResponse(){
        return new LoginResponse<>(LoginCode.WRONG_VERIFYCODE,LoginMessage.WRONG_VERIFYCODE,null);
    }

    public int getStatus() {
        return status;
    }

    public LoginResponse<T> setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LoginResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public LoginResponse setData(T data) {
        this.data = data;
        return this;
    }
}
