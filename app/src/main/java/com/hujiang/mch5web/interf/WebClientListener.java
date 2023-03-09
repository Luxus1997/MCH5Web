package com.hujiang.mch5web.interf;

/**
 * Created by wangxun on 2019/1/11.
 */

public interface WebClientListener {
    void onReceivedError();

    void onPageStarted();

    void onPageFinished(String url);

    void onProgressChanged(int newProgress);
}
