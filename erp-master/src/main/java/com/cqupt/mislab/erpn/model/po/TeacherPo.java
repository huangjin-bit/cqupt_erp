package com.cqupt.mislab.erpn.model.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.po
 * @description:
 * @author: keji0
 * @date: 2020/8/16 13:51
 */
@Data
@NoArgsConstructor
@ToString
@Slf4j
public class TeacherPo {
    private String adminId;
    private String adminName;
    private String position;
    private String email;
    private String tel;
    private String rem1;
    private String role;
}
