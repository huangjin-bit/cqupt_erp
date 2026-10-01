package com.cqupt.mislab.erpn.controller.restart;

import com.cqupt.mislab.erpn.commons.constant.ResultEnum;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.commons.utils.report.WordUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author 张烈文
 * @date 2021/10/7 12:02
 */
@RestController
@RequestMapping("/report")
public class ReportController {


    @Autowired
    private ReportService reportService;

    /**
     * 学生下载个人实验报告。
     * 成功：200 + 文件流；失败：4xx/5xx + Result 统一结构（status、message、data）。
     */
    @GetMapping("/downLoadDoc")
    public ResponseEntity<?> downloadPersonReport(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");

        // 1. 未登录 -> 401
        if (userUnique == null || userUnique.isEmpty()) {
            Result<Object> err = ResultUtil.error(
                    HttpStatus.UNAUTHORIZED.value(),
                    "未登录或会话已过期，请先登录"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        // 2. 调用服务获取报告路径
        Result<?> result = reportService.downLoadReport(userUnique);
        if (result.getStatus() == null || !ResultEnum.SUCCESS.getStatus().equals(result.getStatus())) {
            // 业务失败（如报告未生成、数据不完整等）-> 400，并返回服务端 message
            String msg = result.getMessage() != null ? result.getMessage() : "报告生成失败，请稍后重试";
            Result<Object> err = ResultUtil.error(HttpStatus.BAD_REQUEST.value(), msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        Object data = result.getData();
        if (data == null || !(data instanceof HashMap)) {
            Result<Object> err = ResultUtil.error(HttpStatus.NOT_FOUND.value(), "报告数据不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        @SuppressWarnings("unchecked")
        HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
        String filePaths = (String) dataMap.get("path");
        if (filePaths == null || filePaths.isEmpty()) {
            Result<Object> err = ResultUtil.error(HttpStatus.NOT_FOUND.value(), "报告路径不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        String[] split = filePaths.split("/");
        String fileName = split[split.length - 1];
        WordUtil wordUtil = new WordUtil();
        String basePath = wordUtil.getWordBasePath();
        String path = basePath + fileName;
        File file = new File(path);

        // 3. 文件不存在 -> 404
        if (!file.exists() || !file.isFile()) {
            Result<Object> err = ResultUtil.error(HttpStatus.NOT_FOUND.value(), "报告文件不存在或尚未生成");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        // 4. 封装文件资源并设置响应头
        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/msword"));
        headers.setContentLength(file.length());

        String downloadFileName = "报告" + fileName + ".doc";
        try {
            String encodedName = URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8.name());
            headers.setContentDispositionFormData("attachment", encodedName);
        } catch (UnsupportedEncodingException e) {
            headers.setContentDispositionFormData("attachment", downloadFileName);
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/viewReport")
    public Result viewReport(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return reportService.viewReport(userUnique);
    }

}
