package com.robot.common.lib.constant;

import android.os.Environment;

import com.robot.common.lib.CommonKit;

import java.io.File;


/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/07
 *      version:
 *      desc   :
 * </pre>
 */
public class ConstantAppInfo {

    // about crash string
    public static final String CRASH_LOG_FILE_NAME = "crash_log";           // 保存奔溃日志的文件夹名称
    public static final String PER_CRASH_LOG_END_TAG = ">>>end";            // 每一条log结束的标志
    public static final String CRASH_LOG_FILE_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator
            + CommonKit.getContext().getPackageName() + File.separator
            + CRASH_LOG_FILE_NAME;

	// about runtime important log
    public static final String RUNTIME_LOG_FILE_NAME = "runtime_log";
    public static final String RUNTIME_LOG_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + CommonKit.getContext().getPackageName() + File.separator
            + RUNTIME_LOG_FILE_NAME;
}
