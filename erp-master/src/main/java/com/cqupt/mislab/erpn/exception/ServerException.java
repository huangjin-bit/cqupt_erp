package com.cqupt.mislab.erpn.exception;

/**
 * @program: com.cqupt.mislab.erpn.exception
 * @description:
 * @author: keji0
 * @date: 2020/7/22 11:51
 */
public class ServerException extends RuntimeException {
    public ServerException(String msg){
        super(msg);
    }
}
