package com.hujiang.mch5web.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.hujiang.mch5web.R;
import com.hujiang.mch5web.interf.NetWorkStateListener;
import com.hujiang.mch5web.receiver.NetWorkStateReceiver;
import com.hujiang.mch5web.utils.FileUtil;
import com.hujiang.mch5web.utils.LogUtil;
import com.hujiang.mch5web.utils.NetworkUtil;
import com.hujiang.mch5web.utils.TimeUtil;

import static com.hujiang.mch5web.config.CommWebLibConfig.ASSETS_FOLDER;
import static com.hujiang.mch5web.config.CommWebLibConfig.CACHE_NAME;

public class McH5WebActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NetWorkStateListener {

    private static final String URL = "activity_web_view_url";
    private static final int REFRESH_COMPLETE = 0X110;

    private SwipeRefreshLayout mSwipeLayout;
    private NetWorkStateReceiver netWorkStateReceiver;

    private McH5WebFragment mFragment;
    private String mUrl;

    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("McH5WebActivity onCreate : " + TimeUtil.costTime());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        // ??????????????????????????????????????????
        mSwipeLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return mFragment.getMcH5Webview().getScrollY() > 0;
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra(URL);
        }

        LogUtil.d("McH5WebActivity mUrl : " + mUrl);

        mFragment = McH5WebFragment.newInstance(mUrl);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.web_container, mFragment)
                .commitAllowingStateLoss();

        mFragment.setHJWebFragmentInitedListener(new McH5WebFragment.HJWebFragmentInitedListener() {
            @Override
            public void onFragmentInited(McH5WebFragment hJWebViewFragment) {
                loadUrl();
            }
        });
    }

    private void loadUrl() {
        if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {//??????
            LogUtil.d("????????????");
            mFragment.loadUrl(mUrl);
        } else {
            //???????????????????????????????????????????????????????????????
            if (FileUtil.fileIsExists(Environment.getExternalStorageDirectory() + "/" + CACHE_NAME)) {
                LogUtil.d("????????????????????????????????????");
                String url = "file:///" + Environment.getExternalStorageDirectory() + "/" + CACHE_NAME + "/index.html";
                //?????????????????????????????????????????????html??????
                mFragment.loadUrl(mUrl);
            } else {
                LogUtil.d("???????????????????????????");
                //???????????????????????????
                String url = ASSETS_FOLDER + "/error.html";
                mFragment.loadUrl(url);
            }
        }
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, McH5WebActivity.class);
        intent.putExtra(URL, url);

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        netWorkStateReceiver.setNetWorkStateListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        super.onPause();
    }

    //??????Webview???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //??????????????????????????????????????????keyCode ?????????????????????????????????????????????????????????Webview??????????????????WebView???????????????????????????mWebView.canGoBack()??????????????????Boolean????????????????????????????????????true
        if (keyCode == KeyEvent.KEYCODE_BACK && mFragment.getMcH5Webview().canGoBack()) {
            mFragment.getMcH5Webview().goBack();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    mSwipeLayout.setRefreshing(false);
//                    "javascript:if (typeof HJSDK !== \"undefined\") HJSDK.callbackFromNative('onPullToRefresh','')"
//                    mFragment.getMcH5Webview().onCallJS("javascript:if (typeof HJSDK !== \"undefined\") HJSDK.callbackFromNative('onPullToRefresh','null','true')");//"javascript:HJApp.onPullToRefresh();");
                    loadUrl();
                    Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
    }

    @Override
    public void onChangedNetWorkState(boolean isHasNet) {
        LogUtil.d("onChangedNetWorkState : " + isHasNet);
        if (!isHasNet) {
            mFragment.getMcH5Webview().onCallJS("javascript:if (typeof HJSDK !== \"undefined\") HJSDK.callbackFromNative('onWeakNetwork','null','true')");
        }
    }
}
