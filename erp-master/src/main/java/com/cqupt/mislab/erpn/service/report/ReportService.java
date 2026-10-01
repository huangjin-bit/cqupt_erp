package com.cqupt.mislab.erpn.service.report;

import com.cqupt.mislab.erpn.model.Result;

/**
 * @author 张烈文
 * @date 2021/10/7 12:09
 */
public interface ReportService {


    /**
     * 下载实验报告
     *
     * @param userUnique
     * @return
     */
    Result downLoadReport(String userUnique);

    /**
     * 老师下载汇总报告
     * @param userUnique
     * @return
     */
    Result TeacherDownLoadReport(String userUnique) throws Exception;


    /**
     * 预览报告，返回pdf格式文件
     * @param userUnique
     * @return
     */
    Result viewReport(String userUnique);


    Result viewReportByTeacher(String groupName);
}
