package com.robot.common.lib.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.robot.common.lib.permission.annotation.OnMPermissionDenied;
import com.robot.common.lib.permission.annotation.OnMPermissionGranted;
import com.robot.common.lib.permission.annotation.OnMPermissionNeverAskAgain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/06
 *      version:
 *      desc   :
 * </pre>
 */
public class MPermission extends BaseMPermission {

    /**
     * request permission that had not choose "Don`t ask again"
     * if user choose "Don`t ask again", this method would useless
     *
     * @param object      {@link Activity} or {@link Fragment}
     * @param permission
     * @param requestCode
     */
    /*@TargetApi(Build.VERSION_CODES.M)
    public static void requestDenyPermission(Object object, String permission, int requestCode) {
        if (!isOverMarshmallow()) return;
        if (object instanceof Activity) {
            ((Activity) object).requestPermissions(new String[]{permission}, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(new String[]{permission}, requestCode);
        }
    }*/

    private String[] permissions;
    private int requestCode;
    private Object object; // activity or fragment

    /**
     * ********************* util *********************
     */

    public static List<String> getDeniedPermissions(Activity activity, String[] permissions) {
        return getDeniedPermissions((Object) activity, permissions);
    }

    public static List<String> getDeniedPermissions(Fragment fragment, String[] permissions) {
        return getDeniedPermissions((Object) fragment, permissions);
    }

    /**
     * 查询被拒绝的权限，包括点击了不再显示，并拒绝
     *
     * @param activity
     * @param permissions
     * @return
     */
    private static List<String> getDeniedPermissions(Object activity, String[] permissions) {
        if (!isOverMarshmallow()) return null;
        if (permissions == null || permissions.length <= 0) {
            return null;
        }

        return MPermissionUtil.findDeniedPermissions(getActivity(activity), permissions);
    }

    public static List<String> getNeverAskAgainPermissions(Activity activity, String[] permissions) {
        return getNeverAskAgainPermissions((Object) activity, permissions);
    }

    public static List<String> getNeverAskAgainPermissions(Fragment fragment, String[] permissions) {
        return getNeverAskAgainPermissions((Object) fragment, permissions);
    }

    /**
     * 当用户拒绝授权且点击了不再显示时，该处调用集合才不会无数据
     * 如果只是拒绝，此处返回的集合的长度为0
     *
     * @param activity
     * @param permissions
     * @return
     */
    private static List<String> getNeverAskAgainPermissions(Object activity, String[] permissions) {
        if (!isOverMarshmallow()) return null;
        if (permissions == null || permissions.length <= 0) {
            return null;
        }

        return MPermissionUtil.findNeverAskAgainPermissions(getActivity(activity), permissions);
    }

    public static List<String> getDeniedPermissionsWithoutNeverAskAgain(Activity activity, String[] permissions) {
        return getDeniedPermissionsWithoutNeverAskAgain((Object) activity, permissions);
    }

    public static List<String> getDeniedPermissionsWithoutNeverAskAgain(Fragment fragment, String[] permissions) {
        return getDeniedPermissionsWithoutNeverAskAgain((Object) fragment, permissions);
    }

    /**
     * 检查
     *
     * @param activity
     * @param permissions
     * @return
     */
    private static List<String> getDeniedPermissionsWithoutNeverAskAgain(Object activity, String[] permissions) {
        if (permissions == null || permissions.length <= 0) {
            return null;
        }

        return MPermissionUtil.findDeniedPermissionWithoutNeverAskAgain(getActivity(activity), permissions);
    }

    /**
     * ********************* init *********************
     */

    /**
     * @param object Activity或Fragment
     */
    private MPermission(Object object) {
        this.object = object;
    }

    public static MPermission with(Activity activity) {
        return new MPermission(activity);
    }

    public static MPermission with(Fragment fragment) {
        return new MPermission(fragment);
    }

    public MPermission permissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public MPermission addRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    /**
     * ********************* request *********************
     */

    @TargetApi(value = Build.VERSION_CODES.M)
    public void request() {
        requestPermissions(object, requestCode, permissions);
    }

    public static void needPermission(Activity activity, int requestCode, String[] permissions) {
        requestPermissions(activity, requestCode, permissions);
    }

    public static void needPermission(Fragment fragment, int requestCode, String[] permissions) {
        requestPermissions(fragment, requestCode, permissions);
    }

    public static void needPermission(Activity activity, int requestCode, String permission) {
        needPermission(activity, requestCode, new String[]{permission});
    }

    public static void needPermission(Fragment fragment, int requestCode, String permission) {
        needPermission(fragment, requestCode, new String[]{permission});
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static void requestPermissions(Object object, int requestCode, String[] permissions) {
        if (!MPermissionUtil.isOverMarshmallow()) {
            doExecuteSuccess(object, requestCode);
            return;
        }
        List<String> deniedPermissions = MPermissionUtil.findDeniedPermissions(getActivity(object), permissions);

        if (deniedPermissions.size() > 0) {
            if (object instanceof Activity) {
                ((Activity) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else if (object instanceof Fragment) {
                ((Fragment) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else {
                throw new IllegalArgumentException(object.getClass().getName() + " is not supported");
            }
        } else {
            doExecuteSuccess(object, requestCode);
        }
    }

    /**
     * ********************* on result *********************
     */

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        requestResult(activity, requestCode, permissions, grantResults);
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions, int[] grantResults) {
        requestResult(fragment, requestCode, permissions, grantResults);
    }

    private static void requestResult(Object obj, int requestCode, String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (MPermissionUtil.hasNeverAskAgainPermission(getActivity(obj), deniedPermissions)) {
                doExecuteFailAsNeverAskAgain(obj, requestCode);
            } else {
                doExecuteFail(obj, requestCode);
            }
        } else {
            doExecuteSuccess(obj, requestCode);
        }
    }

    /**
     * ********************* reflect execute result *********************
     */

    private static void doExecuteSuccess(Object activity, int requestCode) {
        executeMethod(activity, MPermissionUtil.findMethodWithRequestCode(activity.getClass(), OnMPermissionGranted.class, requestCode));
    }

    private static void doExecuteFail(Object activity, int requestCode) {
        executeMethod(activity, MPermissionUtil.findMethodWithRequestCode(activity.getClass(), OnMPermissionDenied.class, requestCode));
    }

    private static void doExecuteFailAsNeverAskAgain(Object activity, int requestCode) {
        executeMethod(activity, MPermissionUtil.findMethodWithRequestCode(activity.getClass(), OnMPermissionNeverAskAgain.class, requestCode));
    }

    /**
     * ********************* reflect execute method *********************
     */

    private static void executeMethod(Object activity, Method executeMethod) {
        executeMethodWithParam(activity, executeMethod, new Object[]{});
    }

    private static void executeMethodWithParam(Object activity, Method executeMethod, Object... args) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) {
                    executeMethod.setAccessible(true);
                }
                executeMethod.invoke(activity, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
