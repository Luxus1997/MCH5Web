package com.hujiang.mch5web.app;

import android.app.Application;

import com.hujiang.mch5web.config.CommWebLibConfig;

/**
 * Created by wangxun on 2019/1/10.
 */

public class MyAPP extends Application {

    private static MyAPP instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //检查SD内缓存文件大小，超过阈值则删除
        CommWebLibConfig.getInstance().init();
    }

    public static MyAPP getInstance() {
        return instance;
    }
}
