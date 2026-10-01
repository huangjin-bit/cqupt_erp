package com.cqupt.mislab.erpn.model.entity.superadmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Lunaticer
 * @Create: 2021-11-05 8:30
 * @Tip: Keeping the eyes of the prize !
 * @Description: 超级管理员实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdmin {
    String superAdminId;
    String superAdminName;
    String superAdminPassword;
}