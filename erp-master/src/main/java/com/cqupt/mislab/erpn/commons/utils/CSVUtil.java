package com.cqupt.mislab.erpn.commons.utils;

import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.dto.teacher.StudentImportRow;
import com.cqupt.mislab.erpn.model.dto.teacher.GameImportRow;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xhx
 * @Date 2021/4/27 21:19
 */
@Slf4j
public class CSVUtil {
    //Linux服务器
//    public static final String FILE_PATH = "/erp/hd/csv/";
//    Windows
    public static final String FILE_PATH = "C:\\erpnReport\\csv";
    //换行符
    public static final String SEPARATOR = "\n";

    public static File transforFile(MultipartFile multipartFile) throws IOException {

        String path = FILE_PATH + File.separator + multipartFile.getOriginalFilename();
        File file = new File(path);
        if(!file.getParentFile().exists()){
            file.mkdir();
        }
        log.info("将文件转储到本地！");
        multipartFile.transferTo(file);

        return file;
    }

    public static List<List<String>> readApprovedCSV(File csvFile,int columns) throws IOException {
        List<List<String>> results = new ArrayList<>();
        try(FileInputStream fileInputStream = new FileInputStream(csvFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ){
            CSVParser parser = CSVFormat.DEFAULT.parse(bufferedReader);

            int rowIndex = 0;
            for (CSVRecord record:parser.getRecords()) {
                if(record.size() < columns) {
                    throw new IOException();
                }
                if (rowIndex == 0){
                    rowIndex++;
                    continue;
                }
                List<String> result =  new ArrayList<>();
                for(int i = 0; i < columns;i++){
                    result.add(record.get(i));
                }
                results.add(result);
            }

        }catch (IOException e){
            throw new IOException("解析异常！");
        }
        return results;
    }

    public static List<ApprovedUser> uploadCSV(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空，请上传有效的 CSV 文件！");
        }

        List<ApprovedUser> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"));
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext(); // 读取表头

            if (!isStudentCsvHeader(headers)) {
                throw new IllegalArgumentException("CSV 文件表头格式不正确！正确表头为：姓名（必填）,学号（必填）,班级,专业,学院");
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if(line.length < 2 || line[0]==null||line[1]==null|| line[0].trim().isEmpty() ||line[1].trim().isEmpty()){
                    log.info("CSV批量上传解析错误，");
                    throw new IllegalArgumentException("姓名学号不能为空");
                }
                String userName = line[0].trim();
                String studentId = line[1].trim();
                ApprovedUser user = new ApprovedUser();
                user.setUserName(userName); // 姓名
                user.setSId(studentId);// 学号
                user.setUserId(studentId);//生成唯一id
                user.setPassword("123456");//默认密码
                user.setSClass(line.length > 2 ? line[2].trim() : "");  // 班级
                user.setMajor(line.length > 3 ? line[3].trim() : "");   // 专业
                user.setRem2(line.length > 4 ? line[4].trim() : "");    // 学院
                users.add(user);
            }

            if (users.isEmpty()) {
                throw new IllegalArgumentException("CSV 文件没有有效用户数据");
            }
            // 返回解析后的用户列表
            return users;

        } catch (Exception e) {
            throw new RuntimeException("文件解析失败：" + e.getMessage(), e);
        }
    }

    /**
     * Parses every student row independently so one malformed row can be reported
     * without hiding valid rows from the import result.
     */
    public static List<StudentImportRow> parseStudentImportRows(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件为空，请上传有效的 CSV 文件！");
        }

        List<StudentImportRow> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] headers = csvReader.readNext();
            if (!isStudentCsvHeader(headers)) {
                throw new IllegalArgumentException("CSV 文件表头格式不正确！正确表头为：姓名（必填）,学号（必填）,班级,专业,学院");
            }

            String[] line;
            int rowNumber = 2;
            while ((line = csvReader.readNext()) != null) {
                String userName = valueAt(line, 0);
                String studentId = valueAt(line, 1);
                String rowError = null;
                ApprovedUser user = null;

                if (userName.isEmpty()) {
                    rowError = "姓名不能为空";
                } else if (studentId.isEmpty()) {
                    rowError = "学号不能为空";
                } else {
                    user = new ApprovedUser();
                    user.setUserName(userName);
                    user.setSId(studentId);
                    user.setUserId(studentId);
                    user.setPassword("123456");
                    user.setSClass(valueAt(line, 2));
                    user.setMajor(valueAt(line, 3));
                    user.setRem2(valueAt(line, 4));
                }

                rows.add(new StudentImportRow(rowNumber, user, rowError));
                rowNumber++;
            }

            if (rows.isEmpty()) {
                throw new IllegalArgumentException("CSV 文件没有有效用户数据");
            }
            return rows;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("CSV 批量导入解析失败", e);
            throw new IllegalArgumentException("文件解析失败：" + e.getMessage(), e);
        }
    }

    private static String valueAt(String[] line, int index) {
        return line != null && line.length > index && line[index] != null ? line[index].trim() : "";
    }

    public static List<GameImportRow> parseGameImportRows(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件为空，请上传有效的 CSV 文件！");
        }
        List<GameImportRow> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] headers = csvReader.readNext();
            if (!isGameCsvHeader(headers)) {
                throw new IllegalArgumentException("CSV 文件表头格式不正确！正确表头为：用户ID（必填）");
            }
            String[] line;
            int rowNumber = 2;
            while ((line = csvReader.readNext()) != null) {
                String userId = valueAt(line, 0);
                rows.add(userId.isEmpty()
                        ? new GameImportRow(rowNumber, "", "用户 ID 不能为空")
                        : new GameImportRow(rowNumber, userId, null));
                rowNumber++;
            }
            if (rows.isEmpty()) throw new IllegalArgumentException("CSV 文件没有有效用户数据");
            return rows;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("比赛用户 CSV 解析失败", e);
            throw new IllegalArgumentException("文件解析失败：" + e.getMessage(), e);
        }
    }
    public static List<String> uploadGameCSV(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空，请上传有效的 CSV 文件！");
        }

        List<String> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"));
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext(); // 读取表头

            if (!isGameCsvHeader(headers)) {
                throw new IllegalArgumentException("CSV 文件表头格式不正确！正确表头为：用户ID（必填）");
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if(line.length < 1 || line[0]==null||line[0].trim().isEmpty()){
                    log.info("CSV批量上传解析错误，");
                    throw new IllegalArgumentException("用户id不能为空");
                }
                users.add(line[0].trim());
            }

            if (users.isEmpty()) {
                throw new IllegalArgumentException("CSV 文件没有有效用户数据");
            }
            // 返回解析后的用户列表
            return users;

        } catch (Exception e) {
            throw new RuntimeException("文件解析失败：" + e.getMessage(), e);
        }
    }

    private static boolean isStudentCsvHeader(String[] headers) {
        return headers != null && headers.length >= 5
                && matchesHeader(headers[0], "姓名（必填）")
                && matchesHeader(headers[1], "学号（必填）")
                && matchesHeader(headers[2], "班级")
                && matchesHeader(headers[3], "专业")
                && matchesHeader(headers[4], "学院");
    }

    private static boolean isGameCsvHeader(String[] headers) {
        return headers != null && headers.length >= 1
                && matchesHeader(headers[0], "用户ID（必填）");
    }

    private static boolean matchesHeader(String actual, String expectedHeader) {
        return normalizeHeader(actual).equals(expectedHeader);
    }

    private static String normalizeHeader(String header) {
        return header == null ? "" : header.replace("\uFEFF", "").trim();
    }
}



