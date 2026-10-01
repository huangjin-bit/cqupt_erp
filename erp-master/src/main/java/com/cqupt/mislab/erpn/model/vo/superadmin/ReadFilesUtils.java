package com.cqupt.mislab.erpn.model.vo.superadmin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: Lunaticer
 * @Create: 2021-11-20 0:58
 * @Tip: Keeping the eyes of the prize !
 * @Description: 获取本地文件文件名工具类
 */
public class ReadFilesUtils {

    /**
     * 获取文件夹下所有文件的名称 + 模糊查询（当不需要模糊查询时，queryStr传空或null即可）
     * 1.当路径不存在时，map返回retType值为1
     * 2.当路径为文件路径时，map返回retType值为2，文件名fileName值为文件名
     * 3.当路径下有文件夹时，map返回retType值为3，文件名列表fileNameList，文件夹名列表folderNameList
     * @param folderPath 路径
     * @param queryStr 模糊查询字符串
     * @return
     */
    public static HashMap<String, Object> getFilesName(String folderPath, String queryStr) {
        HashMap<String, Object> map = new HashMap<>();
        //文件名列表
        List<String> fileNameList = new ArrayList<>();
        //文件夹名列表
        List<String> folderNameList = new ArrayList<>();
        File f = new File(folderPath);
        //路径不存在
        if (!f.exists()) {
            map.put("retType", "1");
        } else {
            boolean flag = f.isDirectory();
            if (flag == false) {
                //路径为文件
                map.put("retType", "2");
                map.put("fileName", f.getName());
            } else {
                //路径为文件夹
                map.put("retType", "3");
                File fa[] = f.listFiles();

                //若queryStr传入为null,则替换为空（indexOf匹配值不能为null）
                queryStr = queryStr == null ? "" : queryStr;
                for (int i = 0; i < fa.length; i++) {
                    File fs = fa[i];
                    if (fs.getName().indexOf(queryStr) != -1) {
                        if (fs.isDirectory()) {
                            folderNameList.add(fs.getName());
                        } else {
                            fileNameList.add(fs.getName());
                        }
                    }
                }
                map.put("fileNameList", fileNameList);
                map.put("folderNameList", folderNameList);
            }
        }
        return map;
    }
}
