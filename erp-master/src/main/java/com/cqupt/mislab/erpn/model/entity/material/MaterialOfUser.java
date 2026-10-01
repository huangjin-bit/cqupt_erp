package com.cqupt.mislab.erpn.model.entity.material;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/7/15 21:54
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class MaterialOfUser {
    private String userUnique;//用户在竞赛中的唯一标识
    private String materialName;//原材料名称
    private int mNumber;//原材料数量
    private String wareHouseName;//存放在的仓库名称
}
