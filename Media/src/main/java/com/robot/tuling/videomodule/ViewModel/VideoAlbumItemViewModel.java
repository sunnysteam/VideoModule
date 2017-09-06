package com.robot.tuling.videomodule.ViewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.robot.common.lib.animation.ObjectAnimationUtil;
import com.robot.tuling.videomodule.BR;
import com.robot.tuling.videomodule.Interface.ICallback;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.utils.ImageUtils;

import java.io.File;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by sunnysteam on 2017/8/23.
 */

public class VideoAlbumItemViewModel extends BaseObservable {

    @Bindable
    private boolean isClicked = false;
    private boolean clicked = false;
    private String originalUrl;
    private boolean isclicking = false;

    /**
     * 绑定预览图
     *
     * @param view
     * @param url
     */
    @BindingAdapter({"videoSrc"})
    public static void bindVideoSrc(ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            File imageFile = new File(url);
            if (imageFile.exists()) {
                view.setAlpha(1f);
                view.setImageURI(Uri.parse(url));
            } else {
                // TODO: 2017/8/25 设置默认图片
            }
        } else {
            view.callOnClick();
        }
    }

    /**
     * 绑定缩略图的点击
     *
     * @param view
     * @param isClicked
     */
    @BindingAdapter({"thumbnailClick"})
    public static void bindThumbnailClick(ImageView view, boolean isClicked) {
        if (isClicked) {
            view.callOnClick();
        }
    }

    @Bindable
    private String thumbnailUrl;
    @Bindable
    private String videoUrl;
    @Bindable
    private int isControlVisible = GONE;
    @Bindable
    private int backColor = Color.TRANSPARENT;

    private ICallback callback;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        notifyPropertyChanged(BR.thumbnailUrl);
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        notifyPropertyChanged(BR.videoUrl);
    }

    public int getIsControlVisible() {
        return isControlVisible;
    }

    public void setIsControlVisible(int isControlVisible) {
        this.isControlVisible = isControlVisible;
        notifyPropertyChanged(BR.isControlVisible);
    }

    public int getBackColor() {
        return backColor;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
        notifyPropertyChanged(BR.backColor);
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }

    public boolean getIsClicked() {
        return isClicked;
    }

    public void setIsClicked(boolean clicked) {
        isClicked = clicked;
        notifyPropertyChanged(BR.isClicked);
    }

    public boolean getClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    /**
     * 点击命令
     *
     * @param view
     */
    public void onClickCommand(final View view) {
        if (view.getId() == R.id.thunbnail_image) {
            //选择
            if (!isclicking) {
                if (!clicked) {
                    //当前不是点击状态，置为点击状态
                    if (view.getWidth() > 0) {
                        clicked = true;
                        isclicking = true;
                        setIsControlVisible(VISIBLE);
                        originalUrl = getThumbnailUrl();
                        ObjectAnimationUtil.alphaAnimation(view, 500, 1.0f, 0.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = ImageUtils.loadBitmap(getThumbnailUrl(), view.getWidth() / 2);
                                bitmap = ImageUtils.blur(bitmap);
                                final Bitmap finalBitmap = bitmap;
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ImageView) view).setImageBitmap(finalBitmap);
                                        ObjectAnimationUtil.alphaAnimation(view, 500, 0.0f, 0.5f);
                                        isclicking = false;
                                    }
                                });
                            }
                        }).start();
                        callback.postExec(this, "select");
                    }
                } else {
                    //当前是点击状态，置为未点击状态
                    reSet();
                }
            }
        } else if (view.getId() == R.id.play_image) {
            ObjectAnimationUtil.alphaAnimation(view, 1000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
            ObjectAnimationUtil.shrinkAnimation(view, 1000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
            //播放
            callback.postExec(this);
        } else if (view.getId() == R.id.delete_image) {
            ObjectAnimationUtil.alphaAnimation(view, 1000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
            ObjectAnimationUtil.shrinkAnimation(view, 1000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
            //删除
            callback.postExec(this, "delete_surface");
        }
    }

    /**
     * 回复未点击状态
     */
    public void reSet() {
        if (clicked) {
            clicked = false;
            setIsControlVisible(GONE);
            setThumbnailUrl(originalUrl);
        }
    }

}
