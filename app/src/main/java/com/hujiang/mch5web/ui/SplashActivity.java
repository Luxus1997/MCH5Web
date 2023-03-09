package com.hujiang.mch5web.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.Window;

import com.hujiang.mch5web.R;
import com.hujiang.mch5web.app.MyAPP;
import com.hujiang.mch5web.preload.PreWebCommEngine;
import com.hujiang.mch5web.utils.LogUtil;
import com.hujiang.mch5web.utils.MimeTypeMapUtil;
import com.hujiang.mch5web.utils.NetworkUtil;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by wangxun on 2019/1/10.
 */

public class SplashActivity extends Activity {

    public static final String TEST_URL = "https://mc.hujiang.com";

    private final int SPLASH_DISPLAY_LENGHT = 1000;
    private Handler handler;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static boolean permissionFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
        verifyStoragePermissions(this);

        handler = new Handler();
        // 延迟SPLASH_DISPLAY_LENGHT时间然后跳转到MainActivity
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (permissionFlag) {
                    McH5WebActivity.start(getApplicationContext(), TEST_URL);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGHT);

//        if (NetworkUtil.isNetworkAvailable(MyAPP.getInstance())) {//有网的时候才去下载资源
//            PreWebCommEngine.getInstance().preDownloadRes(getApplicationContext(), TEST_URL, true);
//        }else{//无网的时候不下载资源，但是还是预加载
//            PreWebCommEngine.getInstance().preDownloadRes(getApplicationContext(), TEST_URL, false);
//        }

    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                permissionFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        String requestPermissionsResult = "";
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    requestPermissionsResult += permissions[i] + " 申请成功\n";
                    if (i == 0) {
                        McH5WebActivity.start(getApplicationContext(), TEST_URL);
                        finish();
                    }
                } else {
                    requestPermissionsResult += permissions[i] + " 申请失败\n";
                }
            }
        }
    }
}
