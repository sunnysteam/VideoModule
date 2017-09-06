package com.robot.common.lib.net;

import android.os.Handler;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/25
 *      version:
 *      desc   :
 * </pre>
 */
public class HttpUtil {

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static Handler handler = new Handler(Looper.myLooper());

    private HttpUtil(){

    }

    public static String downJson(final String url, final DownloadListener downloadListener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = getBytesByUrl(url);
                if (bytes == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadListener.downFail(url);
                        }
                    });
                } else {
                    final String json = new String(bytes);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadListener.downSuccess(url, json);
                        }
                    });
                }
            }
        });
        return null;
    }

    /**
     * 下载数据
     * @param path
     * @return
     */
    public static byte[] getBytesByUrl(String path) {
        InputStream inputStream = null;
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);

            inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024 * 2];
            int length = 0;
            while ((length = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
            }
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public interface DownloadListener {

        void downSuccess(String url, String json);

        void downFail(String url);
    }
}
