package com.cqupt.mislab.erpn.commons.constant;

/**
 * @program: com.cqupt.mislab.erpn.commons.constant
 * @description:
 * @author: keji0
 * @date: 2020/7/22 9:47
 */

public enum ResultEnum {
    /**
     * 状态码
     * 0----成功
     * 1----失败
     * 2----待审核
     * 3----已被授权，未加入分组
     * 4----已创建分组
     * 5----已加入分组
     * 6----比赛中
     * 7----用户名已注册
     * */
    SUCCESS(0,"成功"),
    ERROR(1,"失败"),
    NOREGISTER(1,"没有此注册用户"),
    NOAPPROVE(2,"待审核"),
    NOTINGROUP(3,"已授权，未加入分组"),
    CREATEGROUP(4,"已创建分组"),
    JOINGROUP(5,"已加入分组"),
    INGAME(6,"游戏中"),
    HASEXIST(7,"用户名已注册"),
    NULLUSERID(8,"用户名为空");

    private Integer status;
    private String msg;

    ResultEnum(int status,String msg){
        this.status=status;
        this.msg=msg;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

}
