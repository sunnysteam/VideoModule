package com.robot.common.lib.log.crash;

import android.content.Context;
import android.util.Log;

/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/06
 *      version:
 *      desc   : 奔溃初始化帮助类，可在此处对奔溃的堆栈信息进行处理
 * </pre>
 */
public class CrashHandler {

    private Context context;
    private static CrashHandler crashHandler;
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    /**
     * 单例模式保证只写入一次奔溃日志
     *
     * @param context 上下文
     * @return {@link CrashHandler}
     */
    public static CrashHandler init(Context context) {
        if (crashHandler == null) {
            crashHandler = new CrashHandler(context);
        }
        return crashHandler;
    }

    private CrashHandler(Context context) {
        this.context = context;

        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("tag", "【CrashHandler】类的方法：【uncaughtException】: " + "发生了什么事？！惊动到我啦 ~\\(≧▽≦)/~啦啦啦");
                // 1.save crash traceTack
                saveCrashLog(e, true);
                defaultUncaughtExceptionHandler.uncaughtException(t, e);
            }
        });
    }

    private void saveCrashLog(Throwable e, boolean uncaught) {
        CrashFileManager.save(context, e, uncaught);
    }

    public void destroy() {
        crashHandler = null;
    }

}
