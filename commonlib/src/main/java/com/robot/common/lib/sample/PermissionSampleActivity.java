package com.robot.common.lib.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.robot.common.lib.R;
import com.robot.common.lib.permission.MPermission;
import com.robot.common.lib.permission.annotation.OnMPermissionDenied;
import com.robot.common.lib.permission.annotation.OnMPermissionGranted;
import com.robot.common.lib.permission.annotation.OnMPermissionNeverAskAgain;

import java.util.List;


/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/24
 *      version:
 *      desc   : 调用权限申请示例Activity
 * </pre>
 */
public class PermissionSampleActivity extends AppCompatActivity implements View.OnClickListener {

    private View btnOpenCamere;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PermissionSampleActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takc_photo);
        btnOpenCamere = findViewById(R.id.btn);
        btnOpenCamere.setOnClickListener(this);
        btnOpenCamere.performClick();
    }


    /**
     * 申请权限结果，可以在此自行做处理或者调用{@link MPermission#onRequestPermissionsResult(Activity, int, String[], int[])}后，
     * 在标注有@OnMPermissionGranted(100)、 @OnMPermissionDenied(100)、@OnMPermissionNeverAskAgain(100)的地方返回结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermission.onRequestPermissionsResult(this, 100, permissions, grantResults);                  // 调用此方法在标注为OnMPermissionGranted、OnMPermissionDenied的方法中获取结果
    }

    @OnMPermissionGranted(100)
    public void allPermissionGranted() {
        Log.e("tag", "【TakePhotoActivity】类的方法：【allPermissionGranted】: " + "用户授权");
    }

    @OnMPermissionDenied(100)
    public void permissionDeny() {
        Log.e("tag", "【TakePhotoActivity】类的方法：【permissionDeny】: " + "用户拒绝");
    }

    @OnMPermissionNeverAskAgain(100)
    public void permissionNeverAsk() {
        Log.e("tag", "【TakePhotoActivity】类的方法：【permissionNeverAsk】: " + "用户点击了不再显示");
    }

    private void openCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    private String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    /**
     * 基本权限管理
     */
    private void requestBasicPermission() {
        MPermission.with(PermissionSampleActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> deniedPermissions = MPermission.getDeniedPermissions(this, permissions);
            if (deniedPermissions != null && deniedPermissions.size() != 0) {
                requestBasicPermission();
            } else {
                openCamera();
            }
        } else {
            openCamera();
        }
    }
}
