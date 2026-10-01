package com.cqupt.mislab.erpn.service.report.Impl;

import com.cqupt.mislab.erpn.commons.utils.report.WordUtil;
import com.cqupt.mislab.erpn.service.report.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReportServiceImplTest {

    @Autowired
    private ReportService reportService;


    @Autowired
    WordUtil wordUtil;
    @Test
    void downLoadReport() {
//        reportService.viewReport("2018210924163175764381131");
        //
//        String path = wordUtil.getWordBasePath()+"2018210924163175764381131.doc";
        reportService.downLoadReport("2018210917163175764381131");
    }

}