package com.hujiang.mch5web.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hujiang.mch5web.R;
import com.hujiang.mch5web.app.MyAPP;
import com.hujiang.mch5web.interf.WebClientListener;
import com.hujiang.mch5web.utils.LogUtil;
import com.hujiang.mch5web.utils.NetworkUtil;
import com.hujiang.mch5web.utils.TimeUtil;
import com.hujiang.mch5web.web.McH5Webview;
import com.hujiang.mch5web.web.WebviewFactory;

/**
 * Created by wangxun on 2019/1/10.
 */

public class McH5WebFragment extends Fragment implements WebClientListener {
    public static final String URL = "fragment_web_view_url";

    private HJWebFragmentInitedListener hJWebFragmentInitedListener;

    private McH5Webview mcH5Webview;
    private TextView mainLoadTv;
    private String url;

    private View view;
    private boolean addViewFlag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mcH5Webview = WebviewFactory.getMcH5Webview(getContext());
        mcH5Webview.setWebClientListener(this);
        if (hJWebFragmentInitedListener != null) {
            hJWebFragmentInitedListener.onFragmentInited(this);
        }

        view = inflater.inflate(R.layout.fragment_mch5web, null);
        mainLoadTv = view.findViewById(R.id.main_load_text);

        final ViewGroup parent = (ViewGroup) mcH5Webview.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
//        ((RelativeLayout) view).addView(mcH5Webview);
//        mainLoadTv.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ((RelativeLayout) view).addView(mcH5Webview);
//                LogUtil.d("McH5WebFragment postDelayed : " + TimeUtil.costTime());
//            }
//        }, 100);
        ((RelativeLayout) view).addView(mcH5Webview);
        LogUtil.d("McH5WebFragment onCreateView : " + TimeUtil.costTime());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            url = getArguments().getString(URL);
        }
        LogUtil.d("McH5WebFragment onCreate : " + TimeUtil.costTime());
    }

    public McH5Webview getMcH5Webview() {
        return mcH5Webview;
    }

    public void loadUrl(String url) {
        LogUtil.d("McH5WebFragment loadUrl : " + url + "time : " + TimeUtil.costTime());
        mcH5Webview.loadUrl(url);
    }

    public static McH5WebFragment newInstance(String url) {
        McH5WebFragment mcH5WebFragment = new McH5WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        mcH5WebFragment.setArguments(bundle);
        return mcH5WebFragment;
    }

    @Override
    public void onReceivedError() {
//        mainLoadTv.setText("出错啦~~~~");
//        mcH5Webview.setVisibility(View.GONE);
//        if(!NetworkUtil.isNetworkAvailable(MyAPP.getInstance())){
//            String url = ASSETS_FOLDER + "/error.html";
//            mcH5Webview.loadUrl(url);
//        }
    }

    @Override
    public void onPageStarted() {
//        mainLoadTv.setVisibility(View.GONE);
//        mcH5Webview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(String url) {
//        if (url != null && (SplashActivity.TEST_URL.equals(url) || url.endsWith("index.html")) && !addViewFlag) {//只有主页才会add
//            ((RelativeLayout) view).addView(mcH5Webview);
//            addViewFlag = true;
//        }
//        mainLoadTv.setVisibility(View.GONE);
        if (!NetworkUtil.isNetworkAvailable(MyAPP.getInstance())) {
            ((McH5WebActivity) getActivity()).onChangedNetWorkState(false);
        }
    }

    @Override
    public void onProgressChanged(int newProgress) {
        if (newProgress >= 70) {
            if (url != null && (SplashActivity.TEST_URL.equals(url) || url.endsWith("index.html")) && !addViewFlag) {//只有主页才会add
                ((RelativeLayout) view).addView(mcH5Webview);
                addViewFlag = true;
            }
        }
    }

    public interface HJWebFragmentInitedListener {
        void onFragmentInited(McH5WebFragment hJWebViewFragment);
    }

    public void setHJWebFragmentInitedListener(HJWebFragmentInitedListener hJWebFragmentInitedListener) {
        this.hJWebFragmentInitedListener = hJWebFragmentInitedListener;
    }

}
