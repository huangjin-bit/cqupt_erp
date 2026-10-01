package com.cqupt.mislab.erpn.model.entity.superadmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Lunaticer
 * @Create: 2021-12-01 11:22
 * @Tip: Keeping the eyes of the prize !
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class FileInfo {
    private String name;
    private long fileSize;
    private String createTime;
    private String fileType;
}
