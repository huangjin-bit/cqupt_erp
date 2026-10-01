package com.cqupt.mislab.erpn.model.entity.material;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/7/15 21:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyingMaterial {
    private String userUnique;//用户在竞赛中的唯一标识
    private String materialName;//原材料名称
    private int mNumber;//原材料数量
    private String wareHouseName;//存放仓库名称
    private String orderId;//订单编号
    private int happenTime;//购买时间
    private int endTime;//入库时间
}
