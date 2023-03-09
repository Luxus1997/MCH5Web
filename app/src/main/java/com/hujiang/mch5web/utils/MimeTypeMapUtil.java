package com.hujiang.mch5web.utils;

import android.text.TextUtils;

import com.hujiang.mch5web.preload.CacheExtensionConfig;

/**
 * Created by wangxun on 2019/1/10.
 */

public class MimeTypeMapUtil {
    public static String getFileExtensionFromUrl(String url) {
        url = url.toLowerCase();
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            if (url.contains("png")) {
                int query = url.lastIndexOf('?');
                if (query > 0) {
                    url = url.substring(0, query);
                }
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty()) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }
        return getUrlDownloadedApiKey(url);
    }

    /**
     * 某些API也需要缓存到本地，这样的API后面有isKey=的参数
     *
     * @param url
     */
    public static String getUrlDownloadedApiKey(String url) {
        if (url == null || !url.contains(CacheExtensionConfig.API_EXT)) {
            return "";
        }
        url = url.toLowerCase();
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            if (url.contains("png")) {
                int query = url.lastIndexOf('?');
                if (query > 0) {
                    url = url.substring(0, query);
                }
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            return "keyapi";
        }

        return "";
    }

    public static String getKeyApiName(String url) {
        if (url == null || !url.contains(CacheExtensionConfig.API_EXT)) {
            return "";
        }
        url = url.toLowerCase();
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            if (url.contains("png")) {
                int query = url.lastIndexOf('?');
                if (query > 0) {
                    url = url.substring(0, query);
                }
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            return filename;
        }

        return "";
    }
}
