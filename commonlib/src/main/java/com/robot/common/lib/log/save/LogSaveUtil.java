package com.robot.common.lib.log.save;

import android.util.Log;

import com.robot.common.lib.constant.ConstantAppInfo;
import com.robot.common.lib.file.FileStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/08/07
 *      version:
 *      desc   : 打印log，同时把重要信息保存到本地
 * </pre>
 */
public class LogSaveUtil {

    private static final String RUNTIME_LOG_FILE_NAME = "runtime_log.txt";

    public static void e(Class clazz, String method, String content) {
        content = "【" + clazz.getSimpleName() + "】类的方法：【" + method + "】: " + content;
        Log.e("tag", content);
        FileStore.save(ConstantAppInfo.RUNTIME_LOG_FILE_PATH + File.separator + RUNTIME_LOG_FILE_NAME, addTime(content));
    }

    private static String addTime(String content) {
        return new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒", Locale.CHINA).format(new Date()) + "\t" + content + "\n";
    }
}
