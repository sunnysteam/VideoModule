package com.robot.tuling.videomodule.View;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.robot.common.lib.actions.PlayVideoAction;
import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.tuling.videomodule.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoPlayerActivity extends BaseActivity {

    private String videoUrl;
    private String videoTitle;
    private JCVideoPlayerStandard jcVideoPlayer;
    private static final int LISTEN_VISIBILITY = 925;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LISTEN_VISIBILITY:
                    if (jcVideoPlayer.startButton.getVisibility() != View.VISIBLE) {
                        jcVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    }

                    Message message = obtainMessage();
                    message.what = LISTEN_VISIBILITY;
                    sendMessageDelayed(message, 100);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_player);
        Bundle extras = getIntent().getExtras();
        videoUrl = extras.getString("videoUrl");
        videoTitle = extras.getString("videoTitle");
        jcVideoPlayer = ((JCVideoPlayerStandard) findViewById(R.id.jc_video));
        //        jcVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        jcVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        jcVideoPlayer.setUp(videoUrl, JCVideoPlayer.SCREEN_WINDOW_FULLSCREEN, videoTitle);
        jcVideoPlayer.backButton.setVisibility(View.INVISIBLE);
        jcVideoPlayer.startButton.performClick();
        handler.sendMessage(handler.obtainMessage(LISTEN_VISIBILITY));
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
        finish();
    }

    @Override
    protected void onDestroy() {
        handler = null;
        super.onDestroy();
    }

    @Override
    public void notifySpeechText(String text) {
        if (PlayVideoAction.None.name().equals(text)) {

        } else if (PlayVideoAction.Play.name().equals(text)) {
            jcVideoPlayer.startButton.performClick();//模拟用户点击开始按钮，NORMAL状态下点击开始播放视频，播放中点击暂停视频
        } else if (PlayVideoAction.Pause.name().equals(text)) {
            jcVideoPlayer.startButton.performClick();
        } else if (PlayVideoAction.Resume.name().equals(text)) {
            jcVideoPlayer.startButton.performClick();
        } else if (PlayVideoAction.Back.name().equals(text)) {
            jcVideoPlayer.backButton.performClick();
        }
    }

    @Override
    public void notifySpeechStatus(String status) {

    }
}
