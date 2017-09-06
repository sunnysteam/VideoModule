package com.robot.common.lib.log.crash;

import android.content.Context;

import com.robot.common.lib.constant.ConstantAppInfo;
import com.robot.common.lib.file.FileUtil;
import com.robot.common.lib.string.StringUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/06
 *      version:
 *      desc   :
 * </pre>
 */
public class CrashFileManager {

    /**
     * 保存奔溃的堆栈信息到本地
     *
     * @param context  上下文
     * @param ex       异常
     * @param uncaught 是否被捕获
     */
    public static void save(Context context, Throwable ex, boolean uncaught) {
        String stackTrace = readStackTrace(ex);
        saveStackTrace(context, uncaught, stackTrace);
    }

    /**
     * 读出堆栈信息
     *
     * @param ex 抛出的异常
     * @return 堆栈信息字符串
     */
    private static String readStackTrace(Throwable ex) {
        Writer writer = null;
        PrintWriter printWriter = null;
        String stackTrace = "";
        try {
            writer = new StringWriter();
            printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            stackTrace = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (printWriter != null) {
                printWriter.close();
            }
        }
        return stackTrace;
    }

    /**
     * 保存堆栈信息到本地
     *
     * @param context    上下文
     * @param uncaught   未捕获的异常
     * @param stackTrace 堆栈信息
     */
    private static void saveStackTrace(Context context, boolean uncaught, String stackTrace) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String timestamp = sdf.format(date);
        String crashLogDate = timestamp.substring(0, 10);                       // file name start with yyyy-MM-dd
        BufferedWriter mBufferedWriter = null;
        try {
            File mFile = new File(ConstantAppInfo.CRASH_LOG_FILE_PATH + File.separator
                    + crashLogDate + "-CrashLog.txt");                          // file name: yyyy-MM-dd-CrashLog.txt
            File pFile = mFile.getParentFile();
            if (!pFile.exists())
                pFile.mkdirs();                                                 // create parent file
            int count = 1;
            if (mFile.exists())
                count = getTodayCrashCount(mFile) + 1;                          // add 1 means the new exception is happening
            mFile.createNewFile();
            mBufferedWriter = new BufferedWriter(new FileWriter(mFile, true));  // 追加模式写文件
            mBufferedWriter.append(CrashSnapshot.snapshot(context, uncaught, timestamp, stackTrace, count));
            mBufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mBufferedWriter != null) {
                try {
                    mBufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取奔溃日志里所有的日志条数
     *
     * @param file 今天的奔溃日志文件
     * @return 今天奔溃日志总数
     */
    private static int getTodayCrashCount(File file) {
        if (file == null || !file.exists()) return 0;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            /*StringBuilder buffer = new StringBuilder();
            char[] chars = new char[1024];
            int len = -1;
            while ((len = fileReader.read(chars)) != -1) {
                buffer.append(chars, 0, len);
            }
            String ex = buffer.toString();*/
            String ex = FileUtil.read(fileReader);
            return StringUtil.containsCount(ex, ConstantAppInfo.PER_CRASH_LOG_END_TAG);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 删除奔溃日志
     */
    public static void deleteAllCrashLog() {
        String crashLogFile = ConstantAppInfo.CRASH_LOG_FILE_PATH;
        File file = new File(crashLogFile);
        if (file.exists()) {
            FileUtil.deleteAllFilesInFolder(file);
        }
    }
}
