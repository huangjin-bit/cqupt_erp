package com.cqupt.mislab.erpn.model.dto.gamegroup;

import lombok.Data;


/**
 * @author 张烈文
 * @date 2021/10/28 11:32
 */
@Data
public class GameGroupMemberDTO {
    private String groupName;//竞赛分组名称

    private String userId;//参与该竞赛分组的用户ID
    private String userUnique;//用户参与某个竞赛分组后，为该用户产生的唯一标识符
    private Integer currentPeriod;//成员当前正处于什么周期。初始值为0表示竞赛尚未开始
    private Integer status;//用户状态—0表示用户已经破产，1表示用户仍然在经营.2表示完成竞赛。10是还没有开始比赛，但是已经进入分组了的。一开始的时候应该是null
    private String userName;
    private Double ownerBenifit; //所有者权益
    private Double cash;
    private Integer sort; //当前排名

}
