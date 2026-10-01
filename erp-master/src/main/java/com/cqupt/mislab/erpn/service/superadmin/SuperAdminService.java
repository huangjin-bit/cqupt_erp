package com.cqupt.mislab.erpn.service.superadmin;

import com.cqupt.mislab.erpn.model.entity.admin.AdminUser;
import com.cqupt.mislab.erpn.model.entity.superadmin.SuperAdmin;
import com.cqupt.mislab.erpn.model.vo.superadmin.SuperAdminVO;

public interface SuperAdminService {

    SuperAdminVO loginSuperAdmin(SuperAdmin superAdmin);

    SuperAdminVO getAdminList();

    SuperAdminVO changPsw(String adminId);

    SuperAdminVO removeAdminById(String adminId);

    SuperAdminVO findAdminById(String adminId);

    SuperAdminVO addAdmin(AdminUser adminUser);

    SuperAdminVO updateAdmin(AdminUser adminUser);

    SuperAdminVO clearAll();

    SuperAdminVO getFileList();

    SuperAdminVO deleteFile(String fileName);
}
