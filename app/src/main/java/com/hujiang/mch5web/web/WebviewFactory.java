package com.hujiang.mch5web.web;

import android.content.Context;

/**
 * webview单例化
 * Created by wangxun on 2019/1/10.
 */

public class WebviewFactory {

    private static McH5Webview mcH5Webview;

    public static McH5Webview getMcH5Webview(Context context) {
//        if (mcH5Webview == null) {
//            synchronized (WebviewFactory.class) {
//                if (mcH5Webview == null) {
//                    mcH5Webview = new McH5Webview(context);
//                }
//            }
//        }
        return new McH5Webview(context);
    }
}
