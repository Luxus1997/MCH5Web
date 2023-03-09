package com.hujiang.mch5web.web;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.hujiang.mch5web.interf.WebClientListener;
import com.hujiang.mch5web.utils.LogUtil;

/**
 * Created by wangxun on 2019/1/11.
 */

public class McH5WebChromeClient extends WebChromeClient{

    private WebClientListener webClientListener;

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        LogUtil.d("McH5WebChromeClient onProgressChanged newProgress:" + newProgress);
        if (webClientListener != null) {
            webClientListener.onProgressChanged(newProgress);
        }
    }

    public void setWebClientListener(WebClientListener webClientListener) {
        this.webClientListener = webClientListener;
    }
}
