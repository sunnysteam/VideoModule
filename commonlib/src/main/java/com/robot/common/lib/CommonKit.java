package com.robot.common.lib;

import android.app.Application;
import android.content.Context;

import com.robot.common.lib.log.crash.CrashHandler;
import com.robot.common.lib.share.PreferencesUtil;


/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/24
 *      version:
 *      desc   : 上下文注入，必须要在{@link Application#onCreate()}中执行{@link CommonKit#init(Context)}
 * </pre>
 */
public class CommonKit {

    private static Context context;

    public static void init(Context context) {
        CommonKit.context = context;

        CrashHandler.init(context);     // 初始化奔溃日志保存
        PreferencesUtil.init(context);        // 初始化偏好存储
    }

    public static Context getContext() {
        return context;
    }



}
