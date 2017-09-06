package com.robot.tuling.videomodule.View;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.robot.common.lib.baseinterface.BaseActivity;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.ViewModel.PhotoAlbumViewModel;
import com.robot.tuling.videomodule.databinding.ActivityPhotoAlbumBinding;

public class PhotoAlbumActivity extends BaseActivity {

    private ActivityPhotoAlbumBinding activityPhotoAlbumBinding;
    private PhotoAlbumViewModel photoAlbumViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityPhotoAlbumBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_album);
        ActivityPhotoAlbumBinding binding = activityPhotoAlbumBinding;
        photoAlbumViewModel = new PhotoAlbumViewModel();
        binding.setPhotoAlbumViewModel(photoAlbumViewModel);
    }

    @Override
    protected void onDestroy() {
        activityPhotoAlbumBinding.unbind();
        photoAlbumViewModel = null;
        super.onDestroy();
    }

    @Override
    public void notifySpeechText(String text) {
        photoAlbumViewModel.speechNotification(text);
    }

    @Override
    public void notifySpeechStatus(String status) {

    }
}
