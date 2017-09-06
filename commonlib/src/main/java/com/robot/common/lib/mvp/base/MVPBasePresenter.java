package com.robot.common.lib.mvp.base;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;

import com.robot.common.lib.permission.MPermission;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/28
 *      version:
 *      desc   : MVP Presenter顶层基类，用于存放model和view对象
 * </pre>
 */
public abstract class MVPBasePresenter<V extends MVPView, M extends MVPModel> {

    private WeakReference<V> mWeakReference;// 弱引用
    private M model;
    protected Handler mHandler = new Handler();

    public MVPBasePresenter() {
        model = createModel();
    }

    /**
     * 构造时调用创建model，子类presenter必须实现该方法
     *
     * @return 对应界面的model
     */
    protected abstract M createModel();

    /**
     * onCreate
     *
     * @param view 具体的Activity
     */
    public void attach(V view) {
        mWeakReference = new WeakReference<>(view);
    }


    /**
     * onDestroy
     */
    public void detach() {
        if (mWeakReference != null) {
            mWeakReference.clear();
            mWeakReference = null;
        }
    }

    protected V getView() {
        return mWeakReference == null ? null : mWeakReference.get();
    }

    protected M getModel() {
        return model;
    }

    /**
     * 检查基础权限中被拒绝的权限
     *
     * @param basePermissions 打开本model需要的基本权限
     * @return 被拒绝的权限条数
     */
    public int checkDeniedPermission(String[] basePermissions, int requestCode) {
        if (basePermissions == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return 0;
        List<String> deniedPermissions = MPermission.getDeniedPermissions((Activity) getView(), basePermissions);
        if (deniedPermissions.size() != 0) {
            ArrayList<String> requestPermissionList = new ArrayList<>();
            // 只申请未取得的权限
            for (String deniedPermission : deniedPermissions) {
                for (String basePermission : basePermissions) {
                    if (basePermission.equals(deniedPermission)) {
                        requestPermissionList.add(deniedPermission);
                        break;
                    }
                }
            }
            int size = requestPermissionList.size();
            requestBasicPermission(requestPermissionList.toArray(new String[size]), requestCode);
            return size;
        } else {
            // todo 所有权限已经获取
            return 0;
        }
    }


    /**
     * 基本权限管理
     */
    private void requestBasicPermission(String[] permissions, int requestCode) {
        MPermission.with((Activity) getView())
                .addRequestCode(requestCode)
                .permissions(permissions)
                .request();
    }
}
