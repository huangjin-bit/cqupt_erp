package com.cqupt.mislab.erpn.commons.constant;

import io.swagger.models.auth.In;
import lombok.Getter;

/**
 * 生成报告时的一些状态码
 *
 * @author 张烈文
 */
@Getter
public enum ReportEnum {
    STILL_MEMBER_PLAYING(11001,"仍有组员在比赛"),
    GAME_NOT_STARTED(11002, "比赛还未开始"),
    TEACHER_NOT_CONFIRM(11003, "教师未确认排名"),
    GENERATE_REPORT(11000, "正常");
    private Integer code;
    private String message;

    ReportEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
