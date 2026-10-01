package com.cqupt.mislab.erpn.service.iso;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.iso.ISOUndevelop;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 15:16
 */
public interface UndevelopISOService {
    /**
     * 查看所有未开拓的iso
     * @author zy
     * @param userUnique
     * @return
     */
    Result findAllISOUndevelop(String userUnique);

    /**
     *查看单个未开拓的iso
     * @author zy
     * @param userUnique
     * @param isoName
     * @return
     */
    Result findOneUndevelop(String userUnique, String isoName);

}
