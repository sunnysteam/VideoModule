package com.robot.tuling.videomodule.View;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.ViewModel.MediaViewModel;
import com.robot.tuling.videomodule.databinding.ActivityMediaBinding;


public class MediaActivity extends Activity{

    private MediaViewModel mediaViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.translucent);
        ActivityMediaBinding binding=DataBindingUtil.setContentView(this,R.layout.activity_media);
        mediaViewModel = new MediaViewModel(this);
        binding.setMediaViewModel(mediaViewModel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaViewModel.setData(requestCode, resultCode, data);
    }
}
