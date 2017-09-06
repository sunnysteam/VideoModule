package com.robot.common.lib.baseinterface;

/**
 * Created by yuanzhaofeng
 * on 2017/8/7 14:51.
 * desc:
 * version:
 */
public interface BaiduSubjectListener {
    void add(BaiduObserverListener observerListener);
    void recognizeEnd();
    void remove(BaiduObserverListener observerListener);
}
