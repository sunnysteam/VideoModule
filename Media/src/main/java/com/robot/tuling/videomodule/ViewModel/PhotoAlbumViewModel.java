package com.robot.tuling.videomodule.ViewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.robot.common.lib.actions.TakePhotoAciton;
import com.robot.common.lib.animation.ObjectAnimationUtil;
import com.robot.common.lib.constant.ConstantFileType;
import com.robot.common.lib.file.FileStore;
import com.robot.tuling.videomodule.BR;
import com.robot.tuling.videomodule.Interface.ICallback;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.Service.EventCallback;
import com.robot.tuling.videomodule.UIControl.PinchImageView;

import java.io.File;
import java.util.LinkedList;

import me.tatarka.bindingcollectionadapter.ItemView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by sunnysteam on 2017/8/21.
 */

public class PhotoAlbumViewModel extends BaseObservable {

    /**
     * 绑定可变换大小的图片
     * @param imageView
     * @param url
     */
    @BindingAdapter({"photoUrl"})
    public static void bindPhoto(PinchImageView imageView, String url) {
        if (url != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(null);
        }
    }

    /**
     * 绑定viewpager的当前项
     * @param viewPager
     * @param position
     */
    @BindingAdapter({"currentItem"})
    public static void bindCurrentItem(ViewPager viewPager, int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }

    /**
     * 绑定viewpager的内容
     * @param viewPager
     * @param photoAlbumItemViewModels
     */
    @BindingAdapter({"imageItems"})
    public static void bindViewPagerItems(final ViewPager viewPager, final ObservableList<PhotoAlbumItemViewModel> photoAlbumItemViewModels) {
        if (photoAlbumItemViewModels != null && photoAlbumItemViewModels.size() > 0) {
            viewPager.setAdapter(new PagerAdapter() {
                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == arg1;
                }

                @Override
                public int getCount() {
                    return photoAlbumItemViewModels.size();
                }

                @Override
                public void destroyItem(ViewGroup container, int position,
                                        Object object) {
                    container.removeView(((View) object));
                }

                @Override
                public int getItemPosition(Object object) {
                    return super.getItemPosition(object);
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    PinchImageView piv = new PinchImageView(container.getContext());
                    piv.setImageUrl(photoAlbumItemViewModels.get(position).getPhotoItemUrl());
                    container.addView(piv);
                    return piv;
                }

                @Override
                public void setPrimaryItem(ViewGroup container, int position, Object object) {
                    //滑动的当前项时再去设置图片
                    current_position = position;
                    PinchImageView pinchImageView = (PinchImageView) object;
                    Bitmap bitmap = BitmapFactory.decodeFile(pinchImageView.getImageUrl());
                    pinchImageView.setImageBitmap(bitmap);
                }
            });
        }
    }

    @Bindable
    private int isImageViewVisible = GONE;
    @Bindable
    private int isRecycleViewVisible = VISIBLE;
    @Bindable
    private String photoUrl;
    @Bindable
    private int position = 0;
    private static int current_position = 0;

    public ItemView itemView = ItemView.of(BR.photoAlbumItemViewModel, R.layout.item_photo_album);
    public ObservableList<PhotoAlbumItemViewModel> photoAlbumItemViewModels = new ObservableArrayList<>();

    public int getIsImageViewVisible() {
        return isImageViewVisible;
    }

    public void setIsImageViewVisible(int isImageViewVisible) {
        this.isImageViewVisible = isImageViewVisible;
        notifyPropertyChanged(BR.isImageViewVisible);
    }

    public int getIsRecycleViewVisible() {
        return isRecycleViewVisible;
    }

    public void setIsRecycleViewVisible(int isRecycleViewVisible) {
        this.isRecycleViewVisible = isRecycleViewVisible;
        notifyPropertyChanged(BR.isRecycleViewVisible);
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        notifyPropertyChanged(BR.photoUrl);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyPropertyChanged(BR.position);

    }

    public PhotoAlbumViewModel() {
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        updateImageList(FileStore.getPathByType(ConstantFileType.IMAGE));
    }

    /**
     * 语音入口
     * @param text
     */
    public void speechNotification(String text) {
        if (TakePhotoAciton.None.name().equals(text)) {

        } else if (TakePhotoAciton.Close.name().equals(text)) {
            setIsRecycleViewVisible(VISIBLE);
            setIsImageViewVisible(GONE);
        } else if (TakePhotoAciton.Delete.name().equals(text)) {
            deleteItem(current_position);
        } else if (TakePhotoAciton.Send.name().equals(text)) {

        } else if (TakePhotoAciton.Next.name().equals(text)) {
            current_position = current_position + 1 >= photoAlbumItemViewModels.size() - 1 ? photoAlbumItemViewModels.size() - 1 : current_position + 1;
            setPosition(current_position);
        } else if (TakePhotoAciton.Previous.name().equals(text)) {
            current_position = current_position - 1 >= 0 ? current_position - 1 : 0;
            setPosition(current_position);
        } else if (TakePhotoAciton.First.name().equals(text)) {
            setPosition(0);
        } else if (TakePhotoAciton.Last.name().equals(text)) {
            setPosition(photoAlbumItemViewModels.size() - 1);
        }
    }

    /**
     * 扫描图片
     */
    private void updateImageList(final String url) {
        new AsyncTask<Void, PhotoAlbumItemViewModel, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listLinkedFiles(url, new EventCallback<PhotoAlbumItemViewModel>() {
                    @Override
                    public void onEvent(PhotoAlbumItemViewModel photoAlbumItemViewModel) {
                        publishProgress(photoAlbumItemViewModel);
                    }
                });
                return null;
            }

            @Override
            protected void onProgressUpdate(PhotoAlbumItemViewModel... values) {
                photoAlbumItemViewModels.add(values[0]);
            }
        }.execute();
    }

    /**
     * 遍历文件夹下面的图片
     *
     * @param strPath
     */
    private void listLinkedFiles(String strPath, EventCallback<PhotoAlbumItemViewModel> callback) {
        LinkedList<File> list = new LinkedList<>();
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                list.add(file);
            else {
                PhotoAlbumItemViewModel photoAlbumItemViewModel = new PhotoAlbumItemViewModel();
                photoAlbumItemViewModel.setPhotoItemUrl(file.getAbsolutePath());
                photoAlbumItemViewModel.setCallback(getCallback());
                callback.onEvent(photoAlbumItemViewModel);
            }
        }
        File tmp;
        while (!list.isEmpty()) {
            tmp = list.removeFirst();
            if (tmp.isDirectory()) {
                files = tmp.listFiles();
                if (files == null)
                    continue;
                for (File file : files) {
                    if (file.isDirectory())
                        list.add(file);
                    else {
                        PhotoAlbumItemViewModel photoAlbumItemViewModel = new PhotoAlbumItemViewModel();
                        photoAlbumItemViewModel.setPhotoItemUrl(file.getAbsolutePath());
                        photoAlbumItemViewModel.setCallback(getCallback());
                        callback.onEvent(photoAlbumItemViewModel);
                    }
                }
            } else {
                PhotoAlbumItemViewModel photoAlbumItemViewModel = new PhotoAlbumItemViewModel();
                photoAlbumItemViewModel.setPhotoItemUrl(tmp.getAbsolutePath());
                photoAlbumItemViewModel.setCallback(getCallback());
                callback.onEvent(photoAlbumItemViewModel);
            }
        }
    }

    /**
     * 回调
     * @return
     */
    @NonNull
    private ICallback getCallback() {
        return new ICallback() {
            /**
             * 点击事件回调
             * @param object
             */
            @Override
            public void postExec(Object object) {
                setIsImageViewVisible(VISIBLE);
                setIsRecycleViewVisible(GONE);
                PhotoAlbumItemViewModel photoAlbumItemViewModel = (PhotoAlbumItemViewModel) object;
                final int position = photoAlbumItemViewModels.indexOf(photoAlbumItemViewModel);
                setPosition(position);
                setPhotoUrl(photoAlbumItemViewModel.getPhotoItemUrl());
            }

            @Override
            public void postExec(Object object, String state) {

            }
        };
    }

    /**
     * 点击命令
     * @param view
     */
    public void onClickCommand(View view) {
        ObjectAnimationUtil.alphaAnimation(view, 1000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
        ObjectAnimationUtil.shrinkAnimation(view, 1000, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
        if (view.getId() == R.id.btn_close) {
            setIsRecycleViewVisible(VISIBLE);
            setIsImageViewVisible(GONE);
        } else if (view.getId() == R.id.btn_delete) {
            deleteItem(current_position);
        }
    }

    /**
     * 删除项目
     * @param position
     */
    private void deleteItem(int position) {
        PhotoAlbumItemViewModel model = photoAlbumItemViewModels.get(position);
        File file = new File(model.getPhotoItemUrl());
        file.delete();
        photoAlbumItemViewModels.remove(position);
        if (position <= photoAlbumItemViewModels.size() - 1) {
            setPosition(position);
        } else {
            setPosition(photoAlbumItemViewModels.size() - 1);
        }
    }

}
