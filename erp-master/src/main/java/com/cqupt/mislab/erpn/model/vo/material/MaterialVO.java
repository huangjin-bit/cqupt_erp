package com.cqupt.mislab.erpn.model.vo.material;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/7/16 0:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialVO {

    private String materialName;//原材料名称
    private Float price;//原材料价格
    private Integer delayTime;//延迟入库时间
    private Integer mNumber;
}
