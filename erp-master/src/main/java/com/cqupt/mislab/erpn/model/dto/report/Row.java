package com.cqupt.mislab.erpn.model.dto.report;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/10/29 14:20
 */
@Data
public class Row {
    /**
     * 这里的每一个col都有一个默认值,防止生成报告时报错
     */
    private String DEFAULT_VALUE = "";
    private String col1=DEFAULT_VALUE;
    private String col2=DEFAULT_VALUE;
    private String col3=DEFAULT_VALUE;
    private String col4=DEFAULT_VALUE;
    private String col5=DEFAULT_VALUE;
    private String col6=DEFAULT_VALUE;
}
