package com.cqupt.mislab.erpn.dao.iso;

import com.cqupt.mislab.erpn.model.entity.iso.ISOUndevelop;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 14:02
 */
@Repository
@Mapper
public interface UndevelopISODao {
    /**
     * 获取所有的未研发ISO信息
     * @param userUnique
     * @return
     */
    List<ISOUndevelop> findAllIsoUndevelop(String userUnique);

    /**
     * 找到一个指定的未研发ISO
     * @param userUnique
     * @param isoName
     * @return
     */
    ISOUndevelop findOneIsoUndevelop(String userUnique,String isoName);

    /**
     * 删除一天未研发ISO记录
     * @param userUnique
     * @param isoName
     * @return
     */
    Integer deleteIsoUndevelop(String userUnique,String isoName);
}
