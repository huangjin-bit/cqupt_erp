package com.cqupt.mislab.erpn.model.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Summary returned after clearing all student and game business data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemResetSummary {
    private Map<String, Integer> deletedRows;

    public static SystemResetSummary empty() {
        return new SystemResetSummary(new LinkedHashMap<>());
    }

    public void addTable(String tableName, int deletedCount) {
        deletedRows.put(tableName, deletedCount);
    }
}
