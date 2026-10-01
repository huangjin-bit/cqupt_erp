package com.cqupt.mislab.erpn.dao.iso;

import com.cqupt.mislab.erpn.model.entity.iso.ISODeveloped;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 14:01
 */
@Repository
@Mapper
public interface DevelopedISODao {
    /**
     * 获取所有研发完成的ISO
     * @return
     */
    List<ISODeveloped> findAllISODeveloped(String userUnique);

    /**
     * 改变已认证ISO的状态值
     * @param userUnique
     * @param isoName
     * @param status
     * @return
     */
    Integer updateISODevelopedStatus(String userUnique,String isoName,int status);

    /**
     * 改变已开发认证的lastStatus
     * @param userUnique
     * @param isoName
     * @param status
     * @return
     */
    Integer updateIsoDevelopedLaststatus(String userUnique,String isoName,int status);

    /**
     * 向以开拓市场增加一个ISO
     * @param isoDeveloped
     * @return
     */
    Integer addIsoToDeveloped(ISODeveloped isoDeveloped);

    /**
     * 找到是否有该iso认证
     * @param userUnique
     * @param isoName
     * @return
     */
    Integer findDevelopedIso(String userUnique,String isoName);
}
