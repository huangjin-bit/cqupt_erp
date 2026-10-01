package com.cqupt.mislab.erpn.model.entity.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.entity
 * @description:
 * @author: keji0
 * @date: 2020/7/20 10:11
 */
@Slf4j
@Data
@NoArgsConstructor
@ToString
public class RegisterUser extends BasicUser{

    private  String  status = "新注册用户";//未获得审批的原因字段

    //private  String  rem1 = null; // 新加上的属性，用于存放学院属性（杨青 2016/12/6）
}
