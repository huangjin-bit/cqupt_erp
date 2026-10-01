package com.cqupt.mislab.erpn.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.dto
 * @description:
 * @author: keji0
 * @date: 2020/7/20 11:16
 */
@NoArgsConstructor
@ToString
@Slf4j
@Data
public class UserStudentInfoRegisterDto {
    private String userId;
    private String userName;
    private String password;
    private String sId;
    private String major;
    private String sClass;
    private String email;
    private String tel;
    private String gender;
}
