package com.cqupt.mislab.erpn.commons.constant;

/**
 * @Author xhx
 * @Date 2021/10/5 10:02
 */
public enum TeacherImportGamesEnum {
    GROUPNAME_REAPTED(1,"分组名重复！"),
    PLAYER_IN_GAME(2,"玩家正在进行游戏"),
    USER_SESSION_EXPIRED(3,"未获取到用户信息"),
    ILLEGAL_ARGUMENT(4,"参数异常！"),
    TEACHER_ERRO(5,"登录已过期");
    private int status;
    private String message;
    TeacherImportGamesEnum(Integer i, String message) {
        this.status = i;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
