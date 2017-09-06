package com.robot.common.lib.baseinterface;

/**
 * Created by yuanzhaofeng
 * on 2017/7/26 10:04.
 * desc: 语音识别更新接口
 * version:
 */
public interface ObserverListener {
    void notifySpeechText(String text);//刷新内容
    void notifySpeechStatus(String status);//刷新状态
}


