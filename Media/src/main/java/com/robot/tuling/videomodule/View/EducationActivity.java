package com.robot.tuling.videomodule.View;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.google.gson.Gson;
import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.common.lib.net.HttpUtil;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.ViewModel.MovieAlbumViewModel;
import com.robot.tuling.videomodule.databinding.ActivityEducationBinding;

public class EducationActivity extends BaseActivity {

    private ActivityEducationBinding activityEducationBinding;
    private MovieAlbumViewModel movieAlbumViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEducationBinding = DataBindingUtil.setContentView(this, R.layout.activity_education);
        movieAlbumViewModel = new MovieAlbumViewModel(this, "education");
        activityEducationBinding.setMovieAlbumViewModel(movieAlbumViewModel);

        final String url1 = "";

    }

    @Override
    protected void onDestroy() {
        activityEducationBinding.unbind();
        movieAlbumViewModel = null;
        super.onDestroy();
    }

    @Override
    public void notifySpeechText(String text) {
        movieAlbumViewModel.speechNotification(text);
    }

    @Override
    public void notifySpeechStatus(String status) {

    }
}
