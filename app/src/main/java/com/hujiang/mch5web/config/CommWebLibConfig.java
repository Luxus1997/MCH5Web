package com.hujiang.mch5web.config;

import android.os.Environment;

import com.hujiang.mch5web.utils.FileUtil;
import com.hujiang.mch5web.utils.LogUtil;

import java.io.File;

/**
 * 整体配置类
 * Created by wangxun on 2019/1/10.
 */

public class CommWebLibConfig {

    private boolean isDownloadCache = true; //是否要打开预加载模式，是预加载的总开关
    private boolean isEnableSDCache = true;//是否开启SD卡缓存，默认开

    public static final String CACHE_NAME = "mch5web"; //默认cache文件夹名，SD卡中和diskcache都是这个名字
    private long cacheSize = 200 * 1024 * 1024;//默认cache大小200M，内存LRU默认是十分之一,Disk和sd卡缓存大小都是这么大
    public static final String ASSETS_FOLDER = "file:///android_asset/hybrid/1.0.0"; //固有离线文件存放路径
    public static final String ASSETS_FOLDER_PATH = "hybrid/1.0.0"; //固有离线文件存放路径

    private static CommWebLibConfig mCommWebLibConfig;

    public static CommWebLibConfig getInstance() {
        if (mCommWebLibConfig == null) {
            synchronized (CommWebLibConfig.class) {
                if (mCommWebLibConfig == null) {
                    mCommWebLibConfig = new CommWebLibConfig();
                }
            }
        }
        return mCommWebLibConfig;
    }

    /**
     * 初始化相关
     *
     * @return
     */
    public void init() {
        if (isDownloadCache) {
            checkSdCacheFull();
        }
    }

    public boolean isDownloadCache() {
        return isDownloadCache;
    }

    public boolean isEnableSDCache() {
        return isEnableSDCache;
    }

    public void setEnableSDCache(boolean enableSDCache) {
        isEnableSDCache = enableSDCache;
    }

    public void setDownloadCache(boolean downloadCache) {
        isDownloadCache = downloadCache;
    }

    /**
     * 初始化的时候检查sd卡下面的缓存文件夹是否大于预设，大于则删除
     */
    private void checkSdCacheFull() {
        File sdfile = new File(Environment.getExternalStorageDirectory() + "/" + CACHE_NAME);
        long size = FileUtil.getFolderSize(sdfile);
        if (size > cacheSize) {
            LogUtil.d("checkSdCacheFull容量满了，删除文件夹");
            FileUtil.deleteDirs(Environment.getExternalStorageDirectory() + "/" + CACHE_NAME, true);
        }
    }
}
