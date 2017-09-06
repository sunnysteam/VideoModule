package com.robot.common.lib.model;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * <pre>
 *      author : zouweilin
 *      e-mail : zwl9517@hotmail.com
 *      time   : 2017/07/25
 *      version:
 *      desc   : 测试
 * </pre>
 */
public class SampleOCModel {

    private Context context;

    public SampleOCModel(Context context) {
        this.context = context;
    }

    public void openCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        context.startActivity(intent);
    }
}
