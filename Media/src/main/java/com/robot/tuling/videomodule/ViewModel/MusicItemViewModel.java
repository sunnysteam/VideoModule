package com.robot.tuling.videomodule.ViewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Color;
import android.view.View;

import com.robot.common.lib.animation.ObjectAnimationUtil;
import com.robot.tuling.videomodule.BR;
import com.robot.tuling.videomodule.Interface.ICallback;
import com.robot.tuling.videomodule.Model.MusicModel;

/**
 * Created by sunnysteam on 2017/8/8.
 */

public class MusicItemViewModel extends BaseObservable {

    @Bindable
    private MusicModel musicModel;
    @Bindable
    private int backColor = Color.TRANSPARENT;
    @Bindable
    private int textColor = Color.WHITE;

    private ICallback callback;

    MusicItemViewModel(MusicModel item) {
        musicModel = item;
    }

    public MusicModel getMusicModel() {
        return musicModel;
    }

    private void setMusicModel(MusicModel musicModel) {
        this.musicModel = musicModel;
        notifyPropertyChanged(BR.musicModel);
    }

    public int getBackColor() {
        return backColor;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
        notifyPropertyChanged(BR.backColor);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        notifyPropertyChanged(BR.textColor);
    }

    public void onClickCommand(final View view) {
//        view.post(new Runnable() {
//            @Override
//            public void run() {
//                ObjectAnimationUtil.alphaAnimation(view, 2000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
//            }
//        });
        ObjectAnimationUtil.alphaAnimation(view, 1000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
        ObjectAnimationUtil.shrinkAnimation(view, 500, 1.0f, 0.9f, 1.0f);
        callback.postExec(this);
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }

}
