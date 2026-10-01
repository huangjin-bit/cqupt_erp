package com.cqupt.mislab.erpn.model.entity.gameGroup;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.entity
 * @description:
 * @author: keji0
 * @date: 2020/7/27 14:27
 */
@Data
@ToString
@Slf4j
@NoArgsConstructor
public class GameGroupMemberInfo {

    private  String  groupName;//竞赛分组名称
    private  String  userId;//参与该竞赛分组的用户ID
    private  String  userUnique;//用户参与某个竞赛分组后，为该用户产生的唯一标识符
    private  int  currentPeriod;//成员当前正处于什么周期。初始值为0表示竞赛尚未开始
    private int status;//用户状态—0表示用户已经破产，1表示用户仍然在经营.2表示完成竞赛。10是还没有开始比赛，但是已经进入分组了的。一开始的时候应该是null

}
