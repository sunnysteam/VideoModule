package com.robot.tuling.videomodule.Interface;

/**
 * Created by sunnysteam on 2017/8/9.
 */

public interface ICallback {
    void postExec(Object object);

    void postExec(Object object, String state);
}
