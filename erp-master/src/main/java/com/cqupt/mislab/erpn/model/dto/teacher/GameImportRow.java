package com.cqupt.mislab.erpn.model.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameImportRow {
    private int rowNumber;
    private String userId;
    private String error;
}
