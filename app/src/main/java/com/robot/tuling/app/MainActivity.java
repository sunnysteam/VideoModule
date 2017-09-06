package com.robot.tuling.app;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.robot.common.lib.CommonKit;
import com.robot.tuling.videomodule.UIControl.WeatherUIControl;
import com.robot.tuling.videomodule.View.CameraActivity;
import com.robot.tuling.videomodule.View.EducationActivity;
import com.robot.tuling.videomodule.View.MediaActivity;
import com.robot.tuling.videomodule.View.MovieAlbumActivity;
import com.robot.tuling.videomodule.View.MusicActivity;
import com.robot.tuling.videomodule.View.VideoActivity;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    private String[] permissions;
    private WeatherUIControl mWeatherUIControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        init();
    }

    private void init() {

        mWeatherUIControl = ((WeatherUIControl) findViewById(R.id.weather_ui_control));
        mWeatherUIControl.setCity("深圳");

        findViewById(R.id.video).setOnClickListener(this);
        findViewById(R.id.music).setOnClickListener(this);
        findViewById(R.id.photo).setOnClickListener(this);
        findViewById(R.id.movie).setOnClickListener(this);
        findViewById(R.id.record_video).setOnClickListener(this);
        findViewById(R.id.education).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video: {
                Intent intent = new Intent(this, MediaActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.music: {
                Intent intent = new Intent(CommonKit.getContext(), MusicActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.photo: {
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.record_video: {
                Intent intent = new Intent(this, VideoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.movie: {
                Intent intent = new Intent(this, MovieAlbumActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.education: {
                Intent intent = new Intent(this, EducationActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    //权限请求
    private void requestPermission() {
        permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
        };

        int ret = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (ret != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 100);
            }
        }
    }
}
