package com.cqupt.mislab.erpn.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.dto
 * @description:
 * @author: keji0
 * @date: 2020/7/25 15:47
 */

@Data
@NoArgsConstructor
@ToString
@Slf4j
public class LoginForm {

    private String userId;
    private String password;
    private String serverCode;

}
