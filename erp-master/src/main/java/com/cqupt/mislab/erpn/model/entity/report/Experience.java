package com.cqupt.mislab.erpn.model.entity.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/10/6 21:51
 */
@Data
@AllArgsConstructor
@Builder
public class Experience {

    private String userUnique;


    private String context = "";

}
