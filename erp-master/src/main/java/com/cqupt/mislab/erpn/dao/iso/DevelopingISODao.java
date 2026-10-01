package com.cqupt.mislab.erpn.dao.iso;

import com.cqupt.mislab.erpn.model.entity.iso.ISODeveloping;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 14:01
 */
@Repository
@Mapper
public interface DevelopingISODao {
    /**
     * 获取所有开发中ISO的信息
     * @param userUnique
     * @return
     */
    List<ISODeveloping> findAllDevelopingIso(String userUnique);

    /**
     * 获取指定的研发中ISO信息
     * @param userUnique
     * @param isoName
     * @return
     */
    ISODeveloping findOneDevelopingIso(String userUnique,String isoName);

    /**
     * 更新开发中ISO的状态信息
     * @param userUnique
     * @param isoName
     * @param status
     * @return
     */
    Integer updateIsoDevelopingStatus(String userUnique,String isoName,Integer status);

    /**
     *更新开发中ISO里FinshedPeriod字段信息
     * @param userUnique
     * @param isoName
     * @return
     */
    Integer updateIsoDevelopingFinishedPeriod(String userUnique,String isoName);

    /**
     * 删除一个开发中ISO的记录
     * @param userUnique
     * @param isoName
     * @return
     */
    Integer deleteOneIsoDevelopingRecord(String userUnique,String isoName);

    /**
     * 增加一个开发中ISO的记录
     * @param isoDeveloping
     * @return
     */
    Integer addIsoToDeveloping(ISODeveloping isoDeveloping);
}
