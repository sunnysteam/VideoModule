package com.robot.tuling.videomodule.View;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.ViewModel.CameraViewModel;
import com.robot.tuling.videomodule.databinding.ActivityCameraBinding;

public class CameraActivity extends BaseActivity {

    private CameraViewModel cameraViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCameraBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        cameraViewModel = new CameraViewModel(this, "camera");
        binding.setCameraViewModel(cameraViewModel);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cameraViewModel.backFormPhotoAlbum();
    }

    @Override
    public void notifySpeechText(String text) {
        cameraViewModel.speechNotification(text);
    }

    @Override
    public void notifySpeechStatus(String status) {

    }
}
