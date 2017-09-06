package com.robot.tuling.videomodule.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;

import com.robot.tuling.videomodule.BR;

/**
 * Created by sunnysteam on 2017/7/25.
 */

public class VideoModel extends BaseObservable {

    private Bitmap image;
    private String video;

    @Bindable
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable
    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
        notifyPropertyChanged(BR.video);
    }
}
