package com.robot.tuling.videomodule.ViewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.robot.common.lib.animation.ObjectAnimationUtil;
import com.robot.tuling.videomodule.BR;
import com.robot.tuling.videomodule.Interface.ICallback;

/**
 * Created by sunnysteam on 2017/8/21.
 */

public class PhotoAlbumItemViewModel extends BaseObservable {

    /**
     * 绑定图片
     * @param imageView
     * @param url
     */
    @BindingAdapter({"photoSrc"})
    public static void bindPhotoItem(ImageView imageView, String url) {
        if (url != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(null);
        }
    }

    private ICallback callback;

    @Bindable
    private String photoItemUrl;

    public String getPhotoItemUrl() {
        return photoItemUrl;
    }

    public void setPhotoItemUrl(String photoItemUrl) {
        this.photoItemUrl = photoItemUrl;
        notifyPropertyChanged(BR.photoItemUrl);
    }

    public void onClickCommand(View view) {
        ObjectAnimationUtil.alphaAnimation(view, 1000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
        callback.postExec(this);
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }
}
