package com.cqupt.mislab.erpn.model.po;

import com.cqupt.mislab.erpn.commons.constant.LoginMessage;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.po
 * @description:
 * @author: keji0
 * @date: 2020/7/20 13:31
 */
@ToString
@Data
@Slf4j
@NoArgsConstructor

     /**
      *         1：已经  注册 && 未审核 的用户，并且 密码正确
      * 		2：已经 (注册 && 未审核) || 已经审核 的用户，但是 密码不正确
      * 		3：已经 登录 的用户，但是 没有加入分组
      * 		4：已经 加入游戏，但是已经 破产
      * 		5：已经 加入游戏，并且 正在比赛
      * 		6：已经 加入游戏，并且已经 比赛完毕
      * 		7：已经 加入一个分组，并且 是 游戏创建者
      * 		8：已经 加入一个分组，但是 不是 游戏创建者
      * 		-1：用户没有注册
      * 		-1000：非法状态
      */
public class UserPo {
    private  String  userId;
    private  String  userName;
    private  String  password;
    private  int status;
    private  String  role;
    private String gender;
    //TODO: -1和default(未注册和非法状态)是否要保留
    public void setRoleByStatus(int status){
        switch (status){
            case 1:
                setRole(LoginMessage.REGISTERUSER);
                break;
            case 2:
                setRole(LoginMessage.WRONG_PASSWORD);
                break;
            case 3:
                setRole(LoginMessage.NO_GAMEGROUP);
                break;
            case 4:
                setRole(LoginMessage.RUIN);
                break;
            case 5:
                setRole(LoginMessage.IN_GAME);
                break;
            case 6:
                setRole(LoginMessage.GAME_COMPLETE);
                break;
            case 7:
                setRole(LoginMessage.GAMEGROUP_CREATOR);
                break;
            case 8:
                setRole(LoginMessage.GAMEGROUP_MEMBER);
                break;
            case -1:
                setRole(LoginMessage.NOT_REGISTER);
                break;
            default:
                setRole(LoginMessage.ILLEGAL);

        }
    }
}
