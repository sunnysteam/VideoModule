package com.robot.tuling.videomodule.View;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.ViewModel.MovieAlbumViewModel;
import com.robot.tuling.videomodule.databinding.ActivityMovieAlbumBinding;

public class MovieAlbumActivity extends BaseActivity {


    private ActivityMovieAlbumBinding activityMovieAlbumBinding;
    private MovieAlbumViewModel movieAlbumViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMovieAlbumBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_album);
        movieAlbumViewModel = new MovieAlbumViewModel(this, "movie");
        activityMovieAlbumBinding.setMovieAlbumViewModel(movieAlbumViewModel);
    }

    @Override
    protected void onDestroy() {
        activityMovieAlbumBinding.unbind();
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
