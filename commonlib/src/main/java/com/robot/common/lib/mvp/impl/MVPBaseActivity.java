package com.robot.common.lib.mvp.impl;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.common.lib.mvp.base.MVPBasePresenter;
import com.robot.common.lib.mvp.base.MVPView;

/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/28
 *      version:
 *      desc   :
 * </pre>
 */
public abstract class MVPBaseActivity<P extends MVPBasePresenter> extends BaseActivity {

    protected P presenter;
    protected int mDeniedSize;

    /**
     * @return 创建中间管理者，如果没有可写null
     */
    protected abstract P createPresenter();

    /**
     * @return 打开model需要的基础权限数组
     */
    protected abstract String[] mBasePermission();

    /**权限请求码
     * @return 返回基础权限被拒绝的数量
     */
    protected abstract int REQUEST_BASE_PERMISSIONS_CODE();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.attach((MVPView) this);
        mDeniedSize = presenter.checkDeniedPermission(mBasePermission(), REQUEST_BASE_PERMISSIONS_CODE());
    }

    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BASE_PERMISSIONS_CODE()) {
            boolean denied = false;
            for (int grantResult : grantResults) {
                if (denied = grantResult == PackageManager.PERMISSION_DENIED) break;
            }
            if (denied) {
                presenter.checkDeniedPermission(mBasePermission(), REQUEST_BASE_PERMISSIONS_CODE());
            } else {
                recreate();
            }
        }
    }
}
