package com.cqupt.mislab.erpn.service.iso;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.iso.ISODeveloping;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 15:17
 */
public interface DevelopingISOService {
    /**
     * 查看所有开发中的iso
     * @author zy
     * @param userUnique
     * @return
     */
    Result findAllISODeveloping(String userUnique);

    /**
     * 恢复开发某个iso认证
     * @author zy
     * @param userUnique
     * @param isoName
     * @return
     */
    Result updateISODevelopingStatusToOne(String userUnique,String isoName);

    /**
     * 停止开发某个iso认证
     * @author zy
     * @param userUnique
     * @return
     */
    Result updateISODevelopingStatusToZero(String userUnique,String isoName);


    /**
     * 向已开拓iso中增加一条记录
     * @author xhx
     * @param
     * @return
     */
    Result addISOToISODeveloping(String userUnique,String isoName);
}
