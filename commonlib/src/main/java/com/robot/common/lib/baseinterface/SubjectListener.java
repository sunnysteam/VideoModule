package com.robot.common.lib.baseinterface;

/**
 * Created by yuanzhaofeng
 * on 2017/7/26 10:06.
 * desc:
 * version:
 */
public interface SubjectListener {
    void add(ObserverListener observerListener);
    void notifyObserverSpeechText(String content);
    void notifyObserverSpeechStatus(String status);
    void remove(ObserverListener observerListener);
}
