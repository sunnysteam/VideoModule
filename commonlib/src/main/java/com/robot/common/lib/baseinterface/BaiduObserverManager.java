package com.robot.common.lib.baseinterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanzhaofeng
 * on 2017/8/7 14:52.
 * desc:
 * version:
 */
public class BaiduObserverManager implements BaiduSubjectListener{
    private static BaiduObserverManager baiduObserverManager;
    //观察者接口集合
    private List<BaiduObserverListener> list = new ArrayList<>();

    private BaiduObserverManager(){

    }
    /**
     * 单例
     */
    public static BaiduObserverManager getInstance(){
        if (null == baiduObserverManager){
            synchronized (BaiduObserverManager.class){
                if (null == baiduObserverManager){
                    baiduObserverManager = new BaiduObserverManager();
                }
            }
        }
        return baiduObserverManager;
    }

    /**
     * 加入监听队列
     */
    @Override
    public void add(BaiduObserverListener observerListener) {
        list.add(observerListener);
    }

    @Override
    public void recognizeEnd() {
        for (BaiduObserverListener observerListener : list){
            observerListener.recognizeEnd();
        }
    }


    /**
     * 监听队列中移除
     */
    @Override
    public void remove(BaiduObserverListener observerListener) {
        if (list.contains(observerListener)){
            list.remove(observerListener);
        }
    }
}
