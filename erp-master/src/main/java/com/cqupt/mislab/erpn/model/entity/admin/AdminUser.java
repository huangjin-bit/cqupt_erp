package com.cqupt.mislab.erpn.model.entity.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@ToString
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {
	private String adminId;
	private String adminName;
	private String password;
	private String position;
	private String email;
	private String tel;
	private String rem1;
	private String role;
}
