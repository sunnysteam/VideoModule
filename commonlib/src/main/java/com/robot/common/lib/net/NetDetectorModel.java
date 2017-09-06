package com.robot.common.lib.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.robot.common.lib.CommonKit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by zouweilin
 * on 2016/12/30.
 */
public class NetDetectorModel {

    private ExecutorService service = Executors.newSingleThreadExecutor();
    private NetDetectorCallback callback;
    private boolean isConnecting;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int netState = (int) msg.obj;
                    if (netState == NetworkUtil.NET_CNNT_BAIDU_OK) {
                        callback.onDetectorOK();// 连接外网成功
                    } else {
                        callback.onDetectorFailed(netState);// 连接外网失败
                    }
                    break;
            }
        }
    };

    /**
     * 通过连接请求判断网络是否正常，此处对百度进行连接
     * @param callback
     */
    public void startNetDetect(NetDetectorCallback callback) {
        if (isConnecting) return;
        isConnecting = true;
        this.callback = callback;
        service.execute(new ConnectRunnable());
    }

    private class ConnectRunnable implements Runnable {

        @Override
        public void run() {
            int netState = NetworkUtil.getNetState(CommonKit.getContext()); // 阻塞，直到成功或超时
            Message message = handler.obtainMessage();
            message.obj = netState;
            message.what = 1;
            handler.sendMessage(message);
            isConnecting = false;
        }
    }


}
