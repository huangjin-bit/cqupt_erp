package com.cqupt.mislab.erpn.model.dto.factory;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/7/14 16:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FactoryRentingDTO {

    private String factoryType;
    private String place;
    private Integer rentCost;
    private Integer needPeriod;

}
