package com.cqupt.mislab.erpn.model.dto.factory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/7/14 16:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FactoryMakingDTO {
    private String factoryType;
    private String place;
    private Integer payMode;
}
