package com.cqupt.mislab.erpn.model.entity.material;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/7/15 21:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialBasic {

    private String materialName;//原材料名称
    private Integer price;//原材料价格
    private Integer delayTime;//延迟入库时间
}
