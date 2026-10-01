package com.cqupt.mislab.erpn.commons.constant;

import lombok.Getter;

/**
 * @author 张烈文
 */

@Getter
public enum GradeEnum {
    EXCELLENT(90f, "优"),
    GOOD(80f, "良"),
    MEAN(70f, "中"),
    PASSED(60f, "及格"),
    FAILED(50f,"不及格");
    private Float score;
    private String grade;

    GradeEnum(Float score, String grade) {
        this.grade = grade;
        this.score = score;
    }
}
