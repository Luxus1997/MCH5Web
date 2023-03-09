package com.hujiang.mch5web.preload;

import android.content.Context;
import android.os.Environment;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hujiang.mch5web.config.CommWebLibConfig;
import com.hujiang.mch5web.utils.DownloadUtil;
import com.hujiang.mch5web.utils.FileUtil;
import com.hujiang.mch5web.utils.LogUtil;
import com.hujiang.mch5web.utils.MimeTypeMapUtil;
import com.hujiang.mch5web.web.McH5Webview;
import com.hujiang.mch5web.web.WebviewFactory;

import java.io.FileWriter;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.hujiang.mch5web.config.CommWebLibConfig.CACHE_NAME;

/**
 * 预加载引擎
 * Created by wangxun on 2019/1/10.
 */

public class PreWebCommEngine {

    private String curUrl;
    private static PreWebCommEngine instance;
    private static McH5Webview localWebview;

    protected CacheExtensionConfig mCacheExtensionConfig = new CacheExtensionConfig();

    public static PreWebCommEngine getInstance() {
        if (instance == null) {
            synchronized (PreWebCommEngine.class) {
                if (instance == null) {
                    instance = new PreWebCommEngine();
                }
            }
        }
        return instance;
    }

    public CacheExtensionConfig getCacheExtensionConfig() {
        return mCacheExtensionConfig;
    }

    public void start(Context context, String curUrl) {
        if (CommWebLibConfig.getInstance().isDownloadCache()) {
            this.curUrl = curUrl;
            preRequestData();
//            preDownloadRes(context);
        } else {
            LogUtil.d("当前是非预加载模式");
        }
    }

    /**
     * 下载HTML
     */
    private void preRequestData() {
        if (curUrl == null || curUrl.startsWith("file://")) {
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(curUrl)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LogUtil.d("预请求数据失败 " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                LogUtil.d("预请求数据成功 ");
                stringHtmlToLocal(response.body().string());
            }
        });
    }

    private static void stringHtmlToLocal(String str) {
        try {
            FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/" + CACHE_NAME + "/index.html");//SD卡中的路径
            fw.flush();
            fw.write(str);
            fw.close();
            LogUtil.d("html缓存本地成功 ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preDownloadRes(Context context, final String curUrl, final boolean isInterceptRes) {
        localWebview = WebviewFactory.getMcH5Webview(context);
        localWebview.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (isInterceptRes) {
                    String[] namelist = url.split("/");
                    String fileName = namelist[namelist.length - 1];
                    if (mCacheExtensionConfig.canCache(MimeTypeMapUtil.getFileExtensionFromUrl(url))) {
                        if (!FileUtil.fileIsExists(Environment.getExternalStorageDirectory() + "/" + CACHE_NAME + "/" + fileName)) {
                            DownloadUtil.getInstance().download(url, Environment.getExternalStorageDirectory() + "/" + CACHE_NAME, fileName, null);
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
        localWebview.loadUrl(curUrl);
    }

    /**
     * 下载资源
     *
     * @param url
     */
    public void preDownloadRes(String url) {
        String[] namelist = url.split("/");
        String fileName = namelist[namelist.length - 1];
        boolean keyapiFlag = false; // 如果是核心API，需要缓存
        String tempName = MimeTypeMapUtil.getKeyApiName(url);
        if (fileName != null && fileName.contains(CacheExtensionConfig.API_EXT)) {
            fileName = tempName + ".keyapi";
            keyapiFlag = true;
        }
        if (mCacheExtensionConfig.canCache(MimeTypeMapUtil.getFileExtensionFromUrl(url)) || keyapiFlag) {
            if (!FileUtil.fileIsExists(Environment.getExternalStorageDirectory() + "/" + CACHE_NAME + "/" + fileName)) {
                DownloadUtil.getInstance().download(url, Environment.getExternalStorageDirectory() + "/" + CACHE_NAME, fileName, null);
            }
        }
    }


}
