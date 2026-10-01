package com.cqupt.mislab.erpn.model.dto.teacher;

import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentImportRow {
    private int rowNumber;
    private ApprovedUser user;
    private String error;
}
