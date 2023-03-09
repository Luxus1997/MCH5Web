package com.hujiang.mch5web.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hujiang.mch5web.app.MyAPP;
import com.hujiang.mch5web.config.CommWebLibConfig;
import com.hujiang.mch5web.interf.WebClientListener;
import com.hujiang.mch5web.preload.CacheExtensionConfig;
import com.hujiang.mch5web.preload.PreWebCommEngine;
import com.hujiang.mch5web.ui.SplashActivity;
import com.hujiang.mch5web.utils.FileUtil;
import com.hujiang.mch5web.utils.LogUtil;
import com.hujiang.mch5web.utils.MimeTypeMapUtil;
import com.hujiang.mch5web.utils.NetworkUtil;
import com.hujiang.mch5web.utils.TimeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.hujiang.mch5web.config.CommWebLibConfig.ASSETS_FOLDER_PATH;
import static com.hujiang.mch5web.config.CommWebLibConfig.CACHE_NAME;

/**
 * 缓存加载核心策略
 * Created by wangxun on 2019/1/10.
 */
public class McH5WebviewClient extends WebViewClient {

    private WebClientListener webClientListener;

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogUtil.d("McH5WebviewClient onPageStarted : " + TimeUtil.costTime());
        if (webClientListener != null) {
            webClientListener.onPageStarted();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.d("McH5WebviewClient onPageFinished : " + TimeUtil.costTime());
        TimeUtil.clear();
        if (webClientListener != null) {
            webClientListener.onPageFinished(url);
        }
        if (NetworkUtil.isNetworkAvailable(MyAPP.getInstance()) && url.equals(SplashActivity.TEST_URL)) {//有网的时候才去下载资源，这里只下载html,只缓存主页
            PreWebCommEngine.getInstance().start(MyAPP.getInstance(), url);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        LogUtil.d("McH5WebviewClient onReceivedError M : " + error.getDescription() + " code : " + error.getErrorCode());
        if (webClientListener != null) {
            webClientListener.onReceivedError();
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LogUtil.d("McH5WebviewClient onReceivedError : " + description + " code : " + errorCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        LogUtil.d("shouldInterceptRequest url : " + request.getUrl().toString());
        if (NetworkUtil.isNetworkAvailable(MyAPP.getInstance())) {//有网的时候才去下载资源
            //zepto.min.js这个文件不管有没有网络，都优先在本地里取
            PreWebCommEngine.getInstance().preDownloadRes(request.getUrl().toString());
            if (request.getUrl().toString() != null && request.getUrl().toString().contains("zepto.min.js")) {
                WebResourceResponse webResourceResponse = getLocalAssetsResource(request.getUrl().toString(), ASSETS_FOLDER_PATH);
                if (webResourceResponse != null) {
                    return webResourceResponse;
                }
            }
        } else {
            //无网情况下线走本地加载逻辑
            //无网情况下主页HTML本地替换
            if (request.getUrl().toString().equals(SplashActivity.TEST_URL)) {
                WebResourceResponse webResourceResponse = getLocalIndexResource(request.getUrl().toString(), Environment.getExternalStorageDirectory() + "/mch5web/index.html");
                if (webResourceResponse != null) {
                    return webResourceResponse;
                }
            }
            //本地有缓存目录的话，证明不是第一次安装，这种情况下去缓存目录下找本地缓存文件
            if (FileUtil.fileIsExists(Environment.getExternalStorageDirectory() + "/" + CACHE_NAME)) {
                WebResourceResponse webResourceResponse = getLocalResource(request.getUrl().toString(), Environment.getExternalStorageDirectory() + "/" + CACHE_NAME);
                if (webResourceResponse != null) {
                    return webResourceResponse;
                }
            } else {
                //第一次安装又无网的情况，只能去assets目录下去找离线文件加载
                WebResourceResponse webResourceResponse = getLocalAssetsResource(request.getUrl().toString(), ASSETS_FOLDER_PATH);
                if (webResourceResponse != null) {
                    return webResourceResponse;
                }
            }
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return super.shouldInterceptRequest(view, url);
    }

    /**
     * 从本地SD卡中找资源
     *
     * @param url
     * @param filePath
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private WebResourceResponse getLocalResource(String url, String filePath) {
        String[] namelist = url.split("/");
        String fileName = namelist[namelist.length - 1];
        if (fileName != null && fileName.contains(CacheExtensionConfig.API_EXT)) {
            fileName = MimeTypeMapUtil.getKeyApiName(url) + ".keyapi";
        }
        LogUtil.d("getLocalRes cancache : " + PreWebCommEngine.getInstance().getCacheExtensionConfig().canCache(MimeTypeMapUtil.getFileExtensionFromUrl(url)) + " fileName : " + fileName);
        if (PreWebCommEngine.getInstance().getCacheExtensionConfig().canCache(MimeTypeMapUtil.getFileExtensionFromUrl(url)) || fileName.contains(".keyapi")) {
            InputStream in = null;

            if (CommWebLibConfig.getInstance().isEnableSDCache()) {
                //以下为SDcard cache
                boolean isExits = FileUtil.fileIsExists(filePath + "/" + fileName);
                LogUtil.d("getLocalRes fileIsExists : " + filePath + "/" + fileName + " isExits: " + isExits);
                if (isExits) {
                    LogUtil.d("getLocalRes fileName " + fileName + " exits");
                    File fileRes = new File(filePath + "/", fileName);
                    try {
                        in = new FileInputStream(fileRes);

                        WebResourceResponse response;
                        String[] fileNameStrs = fileName.split("\\.");
                        if (fileName.contains(".keyapi") && fileNameStrs.length >= 2 && !fileNameStrs[1].contains("css") && !fileNameStrs[1].contains("js")) {
                            LogUtil.d("getLocalRes fileNameStrs fileName : " + fileName);
                            Map<String, String> headerMap = new HashMap<>();
                            headerMap.put("Access-Control-Allow-Origin", "*");
                            headerMap.put("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
                            headerMap.put("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token");
                            response = new WebResourceResponse(
                                    "application/json;charset=UTF-8", "UTF-8", 200, "OK", headerMap, in);
                        } else {
                            response = new WebResourceResponse(
                                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))
                                    , "UTF-8", in);
                        }
                        LogUtil.d("getLocalRes fileName " + fileName + " done");
                        return response;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 无网时首页使用本地index.html替换
     * @param url
     * @param filePath
     * @return
     */
    private WebResourceResponse getLocalIndexResource(String url, String filePath) {
        String[] namelist = url.split("/");
        String fileName = namelist[namelist.length - 1];
        if (fileName != null && fileName.contains(CacheExtensionConfig.API_EXT)) {
            fileName = MimeTypeMapUtil.getKeyApiName(url) + ".keyapi";
        }
        LogUtil.d("getLocalRes cancache : " + PreWebCommEngine.getInstance().getCacheExtensionConfig().canCache(MimeTypeMapUtil.getFileExtensionFromUrl(url)) + " fileName : " + fileName);
        InputStream in = null;

        if (CommWebLibConfig.getInstance().isEnableSDCache()) {
            //以下为SDcard cache
            boolean isExits = FileUtil.fileIsExists(filePath);
            LogUtil.d("getLocalRes fileIsExists : " + filePath + "/" + fileName + " isExits: " + isExits);
            if (isExits) {
                LogUtil.d("getLocalRes fileName " + fileName + " exits");
                File fileRes = new File(filePath);
                try {
                    in = new FileInputStream(fileRes);
                    WebResourceResponse response = new WebResourceResponse(
                            MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))
                            , "UTF-8", in);
                    LogUtil.d("getLocalRes fileName " + fileName + " done");
                    return response;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 从本地assert目录里面找资源
     *
     * @param url
     * @param filePath
     * @return
     */
    private WebResourceResponse getLocalAssetsResource(String url, String filePath) {
        String[] namelist = url.split("/");
        String fileName = namelist[namelist.length - 1];
        LogUtil.d("getLocalAssetsResource cancache : " + PreWebCommEngine.getInstance().getCacheExtensionConfig().canCache(MimeTypeMapUtil.getFileExtensionFromUrl(url)));
        if (PreWebCommEngine.getInstance().getCacheExtensionConfig().canCache(MimeTypeMapUtil.getFileExtensionFromUrl(url))) {
            InputStream in = null;

            LogUtil.d("getLocalAssetsResource fileIsExists : " + filePath + "/" + fileName);
            try {
                in = MyAPP.getInstance().getResources().getAssets().open(filePath + "/" + fileName);
                WebResourceResponse response = new WebResourceResponse(
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))
                        , "UTF-8", in);
                LogUtil.d("getLocalAssetsResource fileName " + fileName + " done");
                return response;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setWebClientListener(WebClientListener webClientListener) {
        this.webClientListener = webClientListener;
    }
}
