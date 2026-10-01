package com.cqupt.mislab.erpn.model.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultRow {
    private int rowNumber;
    private String userName;
    private String studentId;
    private String userId;
    private String status;
    private String reason;

    public static ImportResultRow success(int rowNumber, String userName, String studentId, String userId) {
        return new ImportResultRow(rowNumber, userName, studentId, userId, "成功", "");
    }

    public static ImportResultRow failure(int rowNumber, String userName, String studentId, String userId, String reason) {
        return new ImportResultRow(rowNumber, userName, studentId, userId, "失败", reason);
    }
}
