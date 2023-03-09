package com.hujiang.mch5web.utils;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by wangxun on 2019/1/10.
 */

public class FileUtil {
    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void deleteDirs(String path, boolean isDeleteDir) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirs(file.getAbsolutePath(), isDeleteDir);
            } else {
                file.delete();
            }
        }
        if (isDeleteDir) {
            dir.delete();
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(java.io.File file) {
        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
