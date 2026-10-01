package com.cqupt.mislab.erpn;

import com.cqupt.mislab.erpn.commons.utils.report.WordUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.cqupt.mislab.erpn.dao")
public class ErpnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErpnApplication.class, args);
    }
}