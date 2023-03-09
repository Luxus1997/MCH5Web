package com.hujiang.mch5web.utils;

/**
 * Created by wangxun on 2019/1/10.
 */

public class TimeUtil {
    private static long time;

    public static long costTime() {
        long curTime = System.currentTimeMillis();
        if (time == 0) {
            time = curTime;
            return 0;
        }
        return curTime - time;
    }

    public static void clear(){
        time = 0;
    }
}
