package com.robot.tuling.app;

import android.app.Application;

import com.robot.common.lib.CommonKit;
import com.robot.tuling.videomodule.Application.AppCache;

/**
 * Created by sunnysteam on 2017/7/27.
 */

public class MyAPP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CommonKit.init(getApplicationContext());
        AppCache.init(this);
    }
}
