package com.cqupt.mislab.erpn.service.material;

import com.cqupt.mislab.erpn.model.Result;

/**
 * @author 张烈文
 * @date 2021/7/16 0:52
 */
public interface MaterialOfUserService {


    /**
     * 根据userUnique查询并封装为MaterialVO
     * @param userUnique
     * @return
     */
    Result listMaterialVO(String userUnique);

    /**
     * 获取用户所有原料库存
     * @param userUnique
     * @return
     */
    Result listMaterialsByUserUnique(String userUnique);

}
