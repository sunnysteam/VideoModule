package com.robot.common.lib.net;

/**
 * Created by zouweilin
 * on 2016/12/30.
 */
public interface NetDetectorCallback {

    void onDetectorOK();

    void onDetectorFailed(int error);
}
