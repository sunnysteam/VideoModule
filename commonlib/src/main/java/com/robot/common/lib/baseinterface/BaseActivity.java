package com.robot.common.lib.baseinterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public abstract class BaseActivity extends AppCompatActivity implements ObserverListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ObserverManager.getInstance().add(this);
        AppManager.getInstance().addActivity(this);
//        if (activityStack==null){
//            activityStack=new Stack<>();
//        }
//        activityStack.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObserverManager.getInstance().remove(this);
        AppManager.getInstance().finishActivity(this);
    }

//    private static Stack<Activity> activityStack;
//    @Override
//    public void notifySpeechText(String text) {
//        if (text.equals("")){
//
//        }
//    }
//
//    @Override
//    public void notifySpeechStatus(String status) {
//
//    }
}
