package com.cqupt.mislab.erpn.commons.utils;

import com.cqupt.mislab.erpn.commons.constant.ResultEnum;
import com.cqupt.mislab.erpn.model.Result;

/**
 * @program: com.cqupt.mislab.erpn.commons.utils
 * @description:
 * @author: keji0
 * @date: 2020/7/22 12:03
 */

public class ResultUtil {


    public static Result<Object> success(Object object) {
        Result<Object> result = new Result<>();
        result.setStatus(ResultEnum.SUCCESS.getStatus());
        result.setMessage(ResultEnum.SUCCESS.getMsg());
        result.setData(object);
        return result;
    }

    public static Result<Object> success() {
        return success(null);
    }


    public static Result<Object> error(Object object) {
        Result<Object> result = new Result<>();
        result.setStatus(ResultEnum.ERROR.getStatus());
        result.setMessage(ResultEnum.ERROR.getMsg());
        result.setData(object);
        return result;
    }



    public static Result error() {
        return error(ResultEnum.ERROR.getStatus(), ResultEnum.ERROR.getMsg());
    }

    public static Result<Object> error(Integer status, String message) {
        Result result = new Result();
        result.setStatus(status);
        result.setMessage(message);
        return result;
    }

    public static Result<Object> error(Integer status, String message,Object data) {
        Result result = new Result();
        result.setStatus(status);
        result.setMessage(message);
        result.setData(data);

        return result;
    }

}
