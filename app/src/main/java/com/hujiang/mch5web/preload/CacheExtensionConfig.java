package com.hujiang.mch5web.preload;

import android.text.TextUtils;

import com.hujiang.mch5web.utils.LogUtil;

import java.util.HashSet;

/**
 * 设置需要缓存文件资源的后缀名，可以动态添加与删除
 * Created by wangxun on 2019/1/10.
 */

public class CacheExtensionConfig {

    public static final String API_EXT = "isKey="; //API中的参数只要有这个参数，那么该API的内容也会缓存

    private static HashSet STATIC = new HashSet() {
        {
            add("html");
            add("htm");
            add("js");
            add("ico");
            add("css");
            add("png");
            add("jpg");
            add("jpeg");
            add("gif");
            add("bmp");
            add("ttf");
            add("woff");
            add("woff2");
            add("otf");
            add("eot");
            add("svg");
            add("xml");
            add("swf");
            add("txt");
            add("text");
            add("conf");
            add("webp");
            add("keyapi");//如果是核心API请求也要缓存
        }
    };

    private HashSet statics = new HashSet(STATIC);

    public static void addGlobalExtension(String extension) {
        add(STATIC, extension);
    }

    public static void removeGlobalExtension(String extension) {
        remove(STATIC, extension);
    }

    private static void add(HashSet set, String extension) {
        if (TextUtils.isEmpty(extension)) {
            return;
        }
        set.add(extension.replace(".", "").toLowerCase().trim());
    }

    private static void remove(HashSet set, String extension) {
        if (TextUtils.isEmpty(extension)) {
            return;
        }
        set.remove(extension.replace(".", "").toLowerCase().trim());
    }

    /**
     * 根据网页的后缀名对比预设的，满足则缓存
     *
     * @param extension
     * @return
     */
    public boolean canCache(String extension) {
        LogUtil.d("canCache extension : " + extension);
        if (TextUtils.isEmpty(extension)) {
            return false;
        }
        //如果属于核心API接口，直接放行缓存
        if (extension.contains(".keyapi")){
            return true;
        }
        extension = extension.toLowerCase().trim();
        if (STATIC.contains(extension)) {
            return true;
        }
        return statics.contains(extension);
    }
}
