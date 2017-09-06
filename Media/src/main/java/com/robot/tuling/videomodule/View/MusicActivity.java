package com.robot.tuling.videomodule.View;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.tuling.videomodule.Application.AppCache;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.Service.PlayService;
import com.robot.tuling.videomodule.ViewModel.MusicViewModel;
import com.robot.tuling.videomodule.databinding.ActivityMusicBinding;

public class MusicActivity extends BaseActivity {

    private MusicViewModel musicViewModel;
    private ActivityMusicBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_music);
        musicViewModel = new MusicViewModel(this);
        binding.setMusicViewModel(musicViewModel);
    }

    @Override
    protected void onDestroy() {
        musicViewModel = binding.getMusicViewModel();
        PlayService service = AppCache.getPlayService();
        if (service != null) {
            service.setOnPlayEventListener(null);
            unbindService(musicViewModel.mPlayServiceConnection);
        }
        binding.unbind();
        musicViewModel = null;
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        musicViewModel.setCallbackMusicModel(null);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                musicViewModel.setCallbackMusicModel(AppCache.getPlayService().getPlayingMusic());
            }
        }, 500);
    }

    @Override
    public void notifySpeechText(String text) {
        musicViewModel.speechNotification(text);
    }

    @Override
    public void notifySpeechStatus(String status) {

    }
}
