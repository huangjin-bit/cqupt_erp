package com.cqupt.mislab.erpn.model.entity.gameGroup;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/10/7 12:48
 */
@Data
public class GameGroupMemberDetail {
    private String userUnique;//为每个user的每个比赛设置的唯一识别号
    private String userId;//user的识别号
    private String studentNo;//成员学号
    private String studentName;//成员名
    private String title;//成员职务
    private Float contribution;
    private Boolean confirm;//是否确认 确认过为1，没确认为0
    private String grade;//最终成绩
    private String groupName;  //组名
    private Float score;  //分数

}
