package com.cqupt.mislab.erpn.commons.constant;

import lombok.Data;
import lombok.Getter;

/**
 * @author 张烈文
 */

@Getter
public enum DownLoadInfoEnum {

    MEMBER_STILL_PLAY_GAME(11000, "还有玩家没有结束游戏或游戏未开始"),
    TEACHER_NOT_CONFIRM(11001, "教师还未确认贡献度");
    private Integer code;
    private String message;

    DownLoadInfoEnum(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
