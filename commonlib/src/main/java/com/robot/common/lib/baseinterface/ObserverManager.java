package com.robot.common.lib.baseinterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanzhaofeng
 * on 2017/7/26 10:07.
 * desc:
 * version:
 */
public class ObserverManager implements SubjectListener {
    private static ObserverManager observerManager;
    //观察者接口集合
    private List<ObserverListener> list = new ArrayList<>();

    private ObserverManager(){

    }
    /**
     * 单例
     */
    public static ObserverManager getInstance(){
        if (null == observerManager){
            synchronized (ObserverManager.class){
                if (null == observerManager){
                    observerManager = new ObserverManager();
                }
            }
        }
        return observerManager;
    }

    /**
     * 加入监听队列
     */
    @Override
    public void add(ObserverListener observerListener) {
        list.add(observerListener);
    }

    /**
     * 通知观察者刷新数据
     */
    @Override
    public void notifyObserverSpeechText(String content) {
        for (ObserverListener observerListener : list){
            observerListener.notifySpeechText(content);
        }
    }

    @Override
    public void notifyObserverSpeechStatus(String status) {
        for (ObserverListener observerListener : list){
            observerListener.notifySpeechStatus(status);
        }
    }

    /**
     * 监听队列中移除
     */
    @Override
    public void remove(ObserverListener observerListener) {
        if (list.contains(observerListener)){
            list.remove(observerListener);
        }
    }
}
