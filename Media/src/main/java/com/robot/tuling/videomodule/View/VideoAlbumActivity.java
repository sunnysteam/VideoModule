package com.robot.tuling.videomodule.View;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.ViewModel.VideoAlbumViewModel;
import com.robot.tuling.videomodule.databinding.ActivityVideoAlbumBinding;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class VideoAlbumActivity extends BaseActivity {

    private ActivityVideoAlbumBinding activityVideoAlbumBinding;
    private VideoAlbumViewModel videoAlbumViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoAlbumViewModel = new VideoAlbumViewModel(this);
        activityVideoAlbumBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_album);
        activityVideoAlbumBinding.setVideoAlbumViewModel(videoAlbumViewModel);
    }

    @Override
    protected void onDestroy() {
        activityVideoAlbumBinding.unbind();
        videoAlbumViewModel = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void notifySpeechText(String text) {
        videoAlbumViewModel.speechNotification(text);
    }

    @Override
    public void notifySpeechStatus(String status) {

    }
}
