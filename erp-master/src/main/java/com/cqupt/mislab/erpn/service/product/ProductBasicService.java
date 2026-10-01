package com.cqupt.mislab.erpn.service.product;


import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.product.ProductOfUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 张烈文
 */

public interface ProductBasicService {

    /**
     * 显示用户所有产品的库存信息
     *
     * @param userUnique
     * @return
     */
    Result findProductInventories(String userUnique);

    /**
     * 查看所有产品的名称
     *
     * @return
     */
    Result findProductName();

    /**
     * 查看产品的原料组成
     * @return
     */
    Result findProductDetail();



    /**
     * 获取产品成本价
     * @param productName
     * @return
     */
    Float findProductPrice(String productName);

    /**
     * 减少用户库存的产品，此参数对象与model描述可能不符，主要是其中的PNumber是要减少的数量，而不是该用户拥有的产品数量！
     * @author lx
     * @param productOfUser
     * @return
     */
     boolean updateDecreasePNumber(ProductOfUser productOfUser);
}
