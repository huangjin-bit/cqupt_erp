package com.cqupt.mislab.erpn.model.vo.gamegroup;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.vo
 * @description:
 * @author: keji0
 * @date: 2020/8/18 15:02
 */
@ToString
@Data
@Slf4j
@NoArgsConstructor
public class GroupDetailInfoVo {
    private  String  groupName;//竞赛分组名称
    private  String  groupCreatorId;//创建竞赛分组的用户ID
    private  String  teacherId;//用户参与某个竞赛分组后，为该用户产生的唯一标识符
    private  int  userNumbers;//竞赛分组可以容纳的人数，约束条件“>0”，默认值为6
    private int  years;//总的比赛年数
    private  int  periodsOfOneYear;//每年所包含的周期数，约束条件为”>=2”,默认值为4
    private  int  currentPeriod;//该分组当前正处于什么周期。初始值为0（表示竞赛尚未开始）。以表GAMEGROUPMEMBER中组内成员最小的CurrentPeriod值作为该字段的值。
    private int  autoPeriodFresh;//隔多长时间自动将竞赛推向下一个周期
    private int  atuoYearFresh;//隔多长时间自动将竞赛推向下一年
    private String  enableAutoPeriodFresh;//是否让自动刷新周期生效
    private String  enableAutoYearFresh;//是否让自动刷新年生效
    private int joinNumber;//当前已经加入的人数,剩余的人数
    private int remandNumber;
    private String[] userIds;

}
