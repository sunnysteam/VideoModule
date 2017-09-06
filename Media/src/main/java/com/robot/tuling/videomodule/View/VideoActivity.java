package com.robot.tuling.videomodule.View;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.ViewModel.CameraViewModel;
import com.robot.tuling.videomodule.databinding.ActivityVideoBinding;

public class VideoActivity extends BaseActivity {

    private CameraViewModel cameraViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityVideoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_video);
        cameraViewModel = new CameraViewModel(this, "video");
        binding.setCameraViewModel(cameraViewModel);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cameraViewModel.backFormVideoAlbum();
    }

    @Override
    public void notifySpeechText(String text) {
        cameraViewModel.speechNotification(text);
    }

    @Override
    public void notifySpeechStatus(String status) {

    }
}
