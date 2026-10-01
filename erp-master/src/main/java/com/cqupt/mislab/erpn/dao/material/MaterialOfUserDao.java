package com.cqupt.mislab.erpn.dao.material;

import com.cqupt.mislab.erpn.model.entity.material.MaterialOfUser;
import com.cqupt.mislab.erpn.model.vo.material.MaterialVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/16 0:22
 */
@Repository
@Mapper
public interface MaterialOfUserDao {


    /**
     * 根据指定用户获取该用户的下原料库存信息及原料基本信息
     * @param userUnique
     * @return
     */
    List<MaterialVO> listMaterialVO(String userUnique);

    /**
     * 获取指定用户下某原料的库存
     * @param userUnique
     * @param materialName
     * @return
     */
    Integer findInventoryWithMaterialName(@Param("userUnique") String userUnique,@Param("materialName") String materialName);

    /**
     * 更新指定原料的库存信息
     * @param userUnique
     * @param materialName
     * @param materialNumber
     */
    void updateMaterialInventory(@Param("userUnique") String userUnique,@Param("materialName") String materialName,@Param("materialNumber") int materialNumber);

    /**
     * 获取用户所有原料库存信息
     * @param userUnique
     * @return
     */
    List<MaterialOfUser> listMaterialByUserUnique(String userUnique);
}
