package com.hujiang.mch5web.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.hujiang.mch5web.interf.NetWorkStateListener;

/**
 * 网络状态监听器
 * Created by wangxun on 2019/1/10.
 */

public class NetWorkStateReceiver extends BroadcastReceiver {
    private NetWorkStateListener netWorkStateListener;

    public void setNetWorkStateListener(NetWorkStateListener netWorkStateListener) {
        this.netWorkStateListener = netWorkStateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                if (netWorkStateListener != null) {
                    netWorkStateListener.onChangedNetWorkState(true);
                }
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                if (netWorkStateListener != null) {
                    netWorkStateListener.onChangedNetWorkState(true);
                }
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                if (netWorkStateListener != null) {
                    netWorkStateListener.onChangedNetWorkState(true);
                }
            } else {
                if (netWorkStateListener != null) {
                    netWorkStateListener.onChangedNetWorkState(false);
                }
            }
            //API大于23时使用下面的方式进行网络监听
        } else {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
                if (networkInfo.isConnected()) {
                    if (netWorkStateListener != null) {
                        netWorkStateListener.onChangedNetWorkState(true);
                        return;
                    }
                }
            }
            if (TextUtils.isEmpty(sb.toString()) || sb.toString().endsWith("false")) {
                if (netWorkStateListener != null) {
                    netWorkStateListener.onChangedNetWorkState(false);
                }
            }
        }
    }
}
