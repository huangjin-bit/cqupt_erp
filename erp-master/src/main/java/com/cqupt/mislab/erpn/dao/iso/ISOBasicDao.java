package com.cqupt.mislab.erpn.dao.iso;

import com.cqupt.mislab.erpn.model.entity.iso.ISOBasic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 14:02
 */
@Repository
@Mapper
public interface ISOBasicDao {
    /**
     * 获取所有的ISO基本信息
     * @return
     */
    List<ISOBasic> findAllBasicISO();

    /**
     * 获取一个指定的ISO
     * @param isoName
     * @return
     */
    ISOBasic findOneISOBasic(String isoName);

    /**
     * 查询ISO的数量
     * @return
     */
    Integer findISOBasicNum();

}
