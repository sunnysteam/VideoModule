package com.robot.common.lib.greendao;

import android.content.Context;

import com.robot.common.lib.CommonKit;

/**
 * Created by yuanzhaofeng
 * on 2017/8/3 16:13.
 * desc:GreenDao单例管理
 * version:
 */
public class GreenDaoManager {
    private static GreenDaoManager greenDaoManager;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Context context;

    private GreenDaoManager() {
        context = CommonKit.getContext();
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "tuling-db", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
    }

    public static GreenDaoManager getInstance() {
        if (greenDaoManager == null) {
            greenDaoManager = new GreenDaoManager();
        }
        return greenDaoManager;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public DaoSession getNewDaoSession() {
        daoSession = daoMaster.newSession();
        return daoSession;
    }
}
