package com.cqupt.mislab.erpn.model.entity.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author xhx
 * @Date 2021/4/16 20:56
 */
@Slf4j
@Data
@NoArgsConstructor
@ToString
public class BasicUser {
    private  String  userId;
    private  String  userName;
    private  String  password;
    private  String  major; //专业
    private  String  sClass;//班级名称
    private  String  sId;
    private  String  email;
    private  String  tel;
    private String gender; //添加性别字段
}
