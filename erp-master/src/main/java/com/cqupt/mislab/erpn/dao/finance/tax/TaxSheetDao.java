package com.cqupt.mislab.erpn.dao.finance.tax;

import com.cqupt.mislab.erpn.model.entity.finance.tax.TaxSheet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 张烈文
 */
@Mapper
@Repository
public interface TaxSheetDao {


    /**
     * 找到用户的税金
     * @param userUnique
     * @return
     */
    TaxSheet findTaxSheetByUserUnique(String userUnique);

    /**
     * 查找营业税率
     *
     * @return
     */
    Double findTaxRate();

    /**
     * 增加税金表数值
     * @param userUnique
     */
    void updateTaxSheet(@Param("userUnique") String userUnique,@Param("tax") Double tax);
}
