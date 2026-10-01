package com.cqupt.mislab.erpn.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author whl
 * @date 2020/12/19 - 19:08
 */
@Data
@AllArgsConstructor
public class UserMessage<T> {
    Integer status;
    String message;
    T data;
}
