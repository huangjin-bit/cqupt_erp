package com.cqupt.mislab.erpn.model.entity.superadmin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author L
 */
@Data
@NoArgsConstructor
@ToString
@Slf4j
public class AddAdmin {
    String adminId;
    String adminPwd;
    String adminName;
}
