package com.cqupt.mislab.erpn.commons.utils.report;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.index.PathBasedRedisIndexDefinition;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 生成实验报告的工具类
 *
 * @author 张烈文
 * @date 2021/10/29 15:09
 */
@Slf4j
@Component
public class WordUtil {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    /**
      *获取系统的分隔符，windows用的\  而linux用的/，考虑到可能会部署到Linux系统上
     */
    public  String DELIMITER = System.getProperty("file.separator");
    public  final String FREE_MARKER_VERSION = "2.3.1.RELEASE";
    public  final String ENCODING = "UTF-8";
    public  final String TEMPLATES_PATH = "templates";
    public  final String TEMPLATE_NAME = "erpword.ftl";
    public  final String SUFFIX = ".doc";
    public  final String PREFIX = "personal/report/";

    /**
     *
     * @param dataMap  数据
     * @param userUnique  文件名
     * @throws Exception
     */
    public  String generateWord(HashMap<String, Object> dataMap, String userUnique) throws Exception {
        // 设置FreeMarker的版本和编码格式
        Writer out = null;
        String fileName = userUnique + SUFFIX;
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            configuration.setDefaultEncoding(ENCODING);
            // 设置FreeMarker生成Word文档所需要的模板的路径

            // 设置FreeMarker生成Word文档所需要的模板
            Template t = configuration.getTemplate(TEMPLATE_NAME, ENCODING);
            // 创建一个Word文档的输出流
            String wordBasePath = getWordBasePath();
            log.info("创建文件:{}", wordBasePath);
            File file = new File(wordBasePath, fileName);
//            if (!file.exists()) {
//                file.getParentFile().mkdirs();
//                file.createNewFile();
//            }else{
//                return PREFIX + fileName;
//            }
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            //FreeMarker使用Word模板和数据生成Word文档
            t.process(dataMap, out);
        } catch (Exception e) {
            log.error("生成文件错误：{}", e.getMessage());
            return "";
        } finally {
            if (out != null) {
                out.flush();
            }
            if (out != null) {
                out.close();

            }
        }
        //TODO:这里的端口最好是动态获取的，等待有缘人来修改,客户端发请求给nginx服务器，由nginx代理发送请求，这里无法获取nginx暴露的端口。暂时由前端提供ip+port也可以
//        String url = getPublicIP() + ":9000/erpn/" + PREFIX + fileName;
        String url = PREFIX + fileName;
        System.out.println("------->"+url);
        return url;
    }

    public  String generateWordTeacher(HashMap<String, Object> dataMap, String userUnique) throws Exception {
        // 设置FreeMarker的版本和编码格式
        Writer out = null;
        String fileName = userUnique + SUFFIX;
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            configuration.setDefaultEncoding(ENCODING);
            // 设置FreeMarker生成Word文档所需要的模板的路径

            // 设置FreeMarker生成Word文档所需要的模板
            Template t = configuration.getTemplate("teacherWord.ftl", ENCODING);
            // 创建一个Word文档的输出流
            String wordBasePath = getWordBasePath();
            log.info("创建文件:{}", wordBasePath);
            File file = new File(wordBasePath, fileName);
//            if (!file.exists()) {
//                file.getParentFile().mkdirs();
//                file.createNewFile();
//            }else{
//                return PREFIX + fileName;
//            }
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            //FreeMarker使用Word模板和数据生成Word文档
            t.process(dataMap, out);
        } catch (Exception e) {
            log.error("生成文件错误：{}", e.getMessage());
            return "";
        } finally {
            if (out != null) {
                out.flush();
            }
            if (out != null) {
                out.close();

            }
        }
        return PREFIX + fileName;
    }


    /**
     * 获取存放word报告的基本路径
     * @return
     */
    public  String getWordBasePath() {
//       获取操作系统的名称
        String os = System.getProperty("os.name");
        String basePath = "";
        if (os.toLowerCase().startsWith("win")) {
            basePath = "C:/erpnReport/document/";
            File filePath = new File(basePath);
            // 确认路径是否存在，如果不存在则创建
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
        }else{
            basePath = "/home/erpnReport/document/";
        }
        log.info("basePath:{}", basePath);

        basePath = basePath.replace("/", DELIMITER);
        return basePath;
    }

    /***
     * @description  获取服务器ip地址
     * @author  liufan
     * @date    2023/3/29 20:05
     * @param
     * @return  java.lang.String
    */
    public static String getMachineIP() throws UnknownHostException {
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress inetAddress;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
                        .nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    inetAddress = (InetAddress) addresses.nextElement();
                    if (inetAddress != null && inetAddress.isSiteLocalAddress()) {
                        ipList.add(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException ignore) {
        }
        return ipList.size() > 0 ? ipList.get(0) : InetAddress.getLocalHost().getHostAddress();
    }

    /***
     * @description  获取服务器公网IP，如果不存在则返回服务器IP
     * @author  liufan
     * @date    2023/3/31 11:33
     * @param
     * @return  java.lang.String
    */
    public static String getPublicIP() throws SocketException {
        Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();

        while (nics.hasMoreElements()) {
            NetworkInterface nic = nics.nextElement();

            Enumeration<InetAddress> addresses = nic.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();

                if (!address.isLinkLocalAddress() && !address.isLoopbackAddress()
                        && address.isSiteLocalAddress()) {
                    return address.getHostAddress();
                }
            }
        }
        return getLocalIP();
    }

    public static String getLocalIP() {
        String ip = "127.0.0.1";

        try {
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

}
