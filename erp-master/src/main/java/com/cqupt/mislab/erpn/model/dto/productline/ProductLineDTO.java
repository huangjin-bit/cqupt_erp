package com.cqupt.mislab.erpn.model.dto.productline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/7/15 18:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductLineDTO {

    private String productLineType;
    private String productName;
    private String factoryId;
}
