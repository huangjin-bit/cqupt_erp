package com.cqupt.mislab.erpn.dao.material;

import com.cqupt.mislab.erpn.model.entity.material.MaterialBasic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/16 0:24
 */
@Repository
@Mapper
public interface MaterialBasicDao {
    /**
     * 获取所有材料的基本信息
     * @return
     */
    List<MaterialBasic> listMaterialBasic();

    MaterialBasic getMaterialBasicByName(@Param("materialName") String materialName);
}
