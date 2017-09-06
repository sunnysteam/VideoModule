package com.robot.tuling.videomodule.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.robot.common.lib.baseinterface.ObserverManager;
import com.robot.tuling.videomodule.BR;
import com.robot.tuling.videomodule.Model.VideoModel;
import com.robot.tuling.videomodule.View.CameraActivity;
import com.robot.tuling.videomodule.View.VideoActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by sunnysteam on 2017/7/25.
 */

public class MediaViewModel extends BaseObservable {

    public static final int TYPE_TAKE_PHOTO = 1;
    public static final int TYPE_TAKE_VIDEO = 2;
    private static final int CODE_TAKE_PHOTO = 3;
    private static final int CODE_TAKE_VIDEO = 4;

    private Context context;
    private VideoModel video;

    @BindingAdapter("android:src")
    public static void bindImage(ImageView imageView, Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @BindingAdapter({"path"})
    public static void bindVideo(VideoView videoView, String url) {
        if (url != null) {
            videoView.setVideoPath(url);
            MediaController controller = new MediaController(videoView.getContext());
            videoView.setMediaController(controller);
        }
    }

    private int isImageVisible = GONE;
    private int isVideoVisible = GONE;

    public VideoModel getVideo() {
        return video;
    }

    public void setVideo(VideoModel video) {
        this.video = video;
    }

    @Bindable
    public int getIsImageVisible() {
        return isImageVisible;
    }

    private void setIsImageVisible(int isImageVisible) {
        this.isImageVisible = isImageVisible;
        notifyPropertyChanged(BR.isImageVisible);
    }

    @Bindable
    public int getIsVideoVisible() {
        return isVideoVisible;
    }

    private void setIsVideoVisible(int isVideoVisible) {
        this.isVideoVisible = isVideoVisible;
        notifyPropertyChanged(BR.isVideoVisible);
    }

    /**
     * 构造方法
     *
     * @param context
     */
    public MediaViewModel(Context context) {
        this.init();
        this.context = context;
    }

    /**
     * 初始化
     */
    private void init() {
        this.video = new VideoModel();
    }

    /**
     * 点击命令
     *
     * @param view
     */
    public void clickCommand(View view) {
        if (((Button) view).getText().equals("拍照")) {
            ObserverManager.getInstance().notifyObserverSpeechText("111");
            Intent intent = new Intent(context, CameraActivity.class);
            ((Activity) context).startActivityForResult(intent, CODE_TAKE_PHOTO);
        } else if (((Button) view).getText().equals("拍视频")) {
            Intent intent = new Intent(context, VideoActivity.class);
            ((Activity) context).startActivityForResult(intent, CODE_TAKE_VIDEO);
        }
    }

    /**
     * 设置图片/视频
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void setData(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_TAKE_PHOTO:
                setIsImageVisible(VISIBLE);
                setIsVideoVisible(GONE);
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.hasExtra("data")) {
                            Log.i("URI", "data is not null");
                            String path = data.getStringExtra("data");
                            try {
                                FileInputStream fis = new FileInputStream(path);
                                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                                video.setImage(bitmap);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case CODE_TAKE_VIDEO:
                setIsImageVisible(GONE);
                setIsVideoVisible(VISIBLE);
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.hasExtra("data")) {
                            Log.i("URI", "data is not null");
                            String path = data.getStringExtra("data");
                            video.setVideo(path);
                        }
                    }
                }
                break;
        }
    }
}
