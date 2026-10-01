package com.cqupt.mislab.erpn.service.iso;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.iso.ISOBasic;

import java.util.List;

/**
 * @Author xhx
 * @Date 2020/11/28 15:21
 */
public interface BasicISOService {
    /**
     * 取出所有ISO认证基础信息
     * @author LX
     * @return
     */
     Result findAllISOBasic();
    /**
     * 取出基础ISO认证的条数
     * @author LX
     * @return
     */
    Result findISOBasicNum();
}
