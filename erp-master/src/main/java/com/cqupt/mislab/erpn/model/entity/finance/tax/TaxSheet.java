package com.cqupt.mislab.erpn.model.entity.finance.tax;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxSheet {
    private String userUnique;
    private Double Tax;
}
