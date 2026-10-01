package com.cqupt.mislab.erpn.model.dto.report;

import lombok.Data;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/10/29 14:23
 */
@Data
public class BusinessReport {

    private Integer year;

    private Integer period;
    private List<Row> rows;
}
