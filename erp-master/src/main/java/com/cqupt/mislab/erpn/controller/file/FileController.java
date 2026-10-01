package com.cqupt.mislab.erpn.controller.file;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/download")
public class FileController {
    @GetMapping("/template.csv")
    public ResponseEntity<Resource> downloadCsvTemplate() throws IOException {
        // 加载 CSV 文件
        Resource resource = new ClassPathResource("templates/template.csv");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template.csv");
        // 返回响应
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("text/csv; charset=GBK"))
                .body(resource);
    }

    @GetMapping("/templategame.csv")
    public ResponseEntity<Resource> downloadCsvTemplateGame() throws IOException {
        // 加载 CSV 文件
        Resource resource = new ClassPathResource("templates/templategame.csv");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=templategame.csv");
        // 返回响应
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("text/csv; charset=GBK"))
                .body(resource);
    }

    @GetMapping("/erp-rules.doc")
    public ResponseEntity<Resource> downloadDocFile() throws IOException {
        // 从类路径加载 DOC 文件
        Resource resource = new ClassPathResource("templates/ERP运营中的规则 .doc");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ERP运营中的规则 .doc");
        headers.setContentType(MediaType.parseMediaType("application/msword; charset=UTF-8"));
        // 返回响应
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
