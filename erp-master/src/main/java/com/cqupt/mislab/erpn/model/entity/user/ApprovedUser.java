package com.cqupt.mislab.erpn.model.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.entity
 * @description:
 * @author: keji0
 * @date: 2020/7/27 14:53
 */
@Data
@ToString
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedUser extends BasicUser{
    private  String  rem2;
}
