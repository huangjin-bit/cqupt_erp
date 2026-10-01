package com.cqupt.mislab.erpn.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author xhx
 * @Date 2021/4/16 21:00
 */
@Data
@ToString
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserVO {
    private  String  userId;
    private  String  userName;
    private  String  password;
    private  String  major; //专业
    private  String  sClass;//班级名称
    private  String  sId;
    private  String  email;
    private  String  tel;
}
