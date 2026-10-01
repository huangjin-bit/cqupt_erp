package com.cqupt.mislab.erpn.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model
 * @description:
 * @author: keji0
 * @date: 2020/7/20 10:01
 */
@ToString
@Data
@Slf4j
@ApiModel
@NoArgsConstructor
public class VerifyCode {
    private String code;

    private byte[] imgBytes;

    private long expireTime;
}
