package com.hujiang.mch5web.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.hujiang.mch5web.interf.JSCallback;
import com.hujiang.mch5web.interf.WebClientListener;
import com.hujiang.mch5web.utils.LogUtil;

/**
 * Created by wangxun on 2019/1/10.
 */

public class McH5Webview extends WebView implements JSCallback {

    public static final String DATA_DATA = "/data/data/";
    public static final String CACHE = "/cache";
    public static final int APP_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    public static final String DATABASES_DIRS = "databases";

    private WebClientListener webClientListener;

    public McH5Webview(Context context) {
        super(context);
        init(context);
    }

    public McH5Webview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public McH5Webview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setAppCacheEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        getSettings().setAppCachePath(DATA_DATA + context.getPackageName() + CACHE);
        getSettings().setAppCacheMaxSize(APP_CACHE_MAX_SIZE);
        String databasePath = context.getDir(DATABASES_DIRS, Context.MODE_PRIVATE).getPath();
        getSettings().setDatabasePath(databasePath);
        getSettings().setBuiltInZoomControls(false);
        getSettings().setDatabaseEnabled(true);
        getSettings().setPluginState(WebSettings.PluginState.ON);
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);

        //加入允许 JS 跨域访问 cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setAllowContentAccess(true);
            //跨文件域访问安全性问题，所以禁止
            getSettings().setAllowUniversalAccessFromFileURLs(false);
            getSettings().setAllowFileAccessFromFileURLs(false);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        //set webview debugable only app debuggable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        WebClientListener webClientListener2 = new WebClientListener() {
            @Override
            public void onReceivedError() {
                if (webClientListener != null) {
                    webClientListener.onReceivedError();
                }
            }

            @Override
            public void onPageStarted() {
                if (webClientListener != null) {
                    webClientListener.onPageStarted();
                }
            }

            @Override
            public void onPageFinished(String url) {
                if (webClientListener != null) {
                    webClientListener.onPageFinished(url);
                }
            }

            @Override
            public void onProgressChanged(int newProgress) {
                if (webClientListener != null) {
                    webClientListener.onProgressChanged(newProgress);
                }
            }
        };

        McH5WebviewClient mcH5WebviewClient = new McH5WebviewClient();
        mcH5WebviewClient.setWebClientListener(webClientListener2);
        setWebViewClient(mcH5WebviewClient);

        McH5WebChromeClient mcH5WebChromeClient = new McH5WebChromeClient();
        mcH5WebChromeClient.setWebClientListener(webClientListener2);
        setWebChromeClient(mcH5WebChromeClient);


        //2014年香港理工大学的研究人员Daoyuan Wu和Rocky Chang发现了两个新的攻击向量存在于android/webkit/AccessibilityInjector.java中，
        // 分别是"accessibility" 和"accessibilityTraversal" ，调用了此组件的应用在开启辅助功能选项中第三方服务的安卓系统中会造成远程代码执行漏洞。
        // 该漏洞公布于CVE-2014-7224, 此漏洞原理与searchBoxJavaBridge_接口远程代码执行相似，均为未移除不安全的默认接口，建议开发者通过以下方式移除该JavaScript接口
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
    }


    @Override
    public void onCallJS(final String url) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                LogUtil.d("McH5Webview onCallJS url :" + url);
                if (!TextUtils.isEmpty(url)) {
                    try {
                        loadUrl(url);
                    } catch (Exception e) {
                        LogUtil.d("web view load url:" + url + "," + e.getMessage());
                    }
                }
            }
        });
    }

    public void setWebClientListener(WebClientListener webClientListener) {
        this.webClientListener = webClientListener;
    }
}
