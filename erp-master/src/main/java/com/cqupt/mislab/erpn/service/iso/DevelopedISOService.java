package com.cqupt.mislab.erpn.service.iso;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.iso.ISODeveloped;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 15:19
 */
public interface DevelopedISOService {
    /**
     *查看所有已开发的iso认证
     * @author zy
     * @param userUnique
     * @return
     */
    Result findAllISODeveloped(String userUnique);

    /**
     * 恢复对停止维护的已开发ISO的维护
     * @author zy
     * @param userUnique
     * @param isoName
     * @return
     */
    Result updateISODevelopedStatusToOne(String userUnique,String isoName);

    /**
     * 停止已开发ISO的维护
     * @author zy
     * @param userUnique
     * @return
     */
    Result updateISODevelopedStatusToZero(String userUnique,String isoName);
    /**
     * 开拓iso
     * @author xhx
     * @Param isDeveloped
     * @return
     */
    Result addISOToISODeveloped(ISODeveloped isoDeveloped);

    /**
     * 判断是否是已经开发的ISO
     * @param userUnique
     * @param ISOName
     * @return
     */
    Boolean isDevelopedISO(String userUnique,String ISOName);
}
