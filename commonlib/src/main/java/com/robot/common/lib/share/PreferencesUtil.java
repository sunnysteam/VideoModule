package com.robot.common.lib.share;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/24
 *      version:
 *      desc   : 偏好存储,如果改偏好存储需要额外存储在一个文件里，可自行准备一个SharedPreferences，并在调用方法的时候传递进来
 * </pre>
 */
public class PreferencesUtil {

    private static SharedPreferences sharedPreferences;

    /**
     * 默认存储在common_sp下，无论调用者是谁，除非自己初始化一个sp传递进来
     *
     * @param context {@link Application#getApplicationContext()}
     */
    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("common_sp", Context.MODE_PRIVATE);
    }

    /**
     * 判断SharePreferences是否为传递进来的是否为空，为空则返回默认的SharedPreferences
     *
     * @return SharedPreferences
     */
    private static SharedPreferences getSharePreferences() {
        return PreferencesUtil.sharedPreferences;
    }

    /**
     * 存一个字符串
     *
     * @param key               键
     * @param value             要存的值
     */
    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharePreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        return getSharePreferences().getString(key, null);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getSharePreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        return getSharePreferences().getInt(key, -1);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharePreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        return getSharePreferences().getBoolean(key, false);
    }

    public static void putFloat(String key, float value) {
        SharedPreferences.Editor editor = getSharePreferences().edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(String key) {
        return getSharePreferences().getFloat(key, -1.0f);
    }
}
