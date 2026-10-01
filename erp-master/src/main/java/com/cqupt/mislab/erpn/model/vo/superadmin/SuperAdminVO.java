package com.cqupt.mislab.erpn.model.vo.superadmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Lunaticer
 * @Create: 2021-11-05 8:31
 * @Tip: Keeping the eyes of the prize !
 * @Description: 封装结果集！
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdminVO {
    //状态码
    private String code;
    //提示信息
    private String msg;
    //响应信息
    private Object data;
}
