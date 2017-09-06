package com.robot.tuling.videomodule.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.robot.common.lib.actions.RecordVideoAction;
import com.robot.common.lib.constant.ConstantFileType;
import com.robot.common.lib.file.FileStore;
import com.robot.tuling.videomodule.BR;
import com.robot.tuling.videomodule.Interface.ICallback;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.Service.EventCallback;
import com.robot.tuling.videomodule.View.VideoPlayerActivity;

import java.io.File;
import java.util.LinkedList;

import me.tatarka.bindingcollectionadapter.ItemView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.robot.tuling.videomodule.BR.videoAlbumItemViewModel;

/**
 * Created by sunnysteam on 2017/8/23.
 */

public class VideoAlbumViewModel extends BaseObservable {

    public ItemView itemView = ItemView.of(videoAlbumItemViewModel, R.layout.item_video_album);
    public ObservableList<VideoAlbumItemViewModel> videoAlbumItemViewModels = new ObservableArrayList<>();
    private int currentPosition = -1;

    /**
     * 绑定当前位置
     *
     * @param recyclerView
     * @param position
     */
    @BindingAdapter({"position"})
    public static void bindPosition(RecyclerView recyclerView, int position) {
        recyclerView.scrollToPosition(position);
    }

    @Bindable
    private int isRecycleViewVisible = VISIBLE;
    @Bindable
    private int isVideoViewVisible = GONE;
    @Bindable
    private int position = -1;

    private Context context;

    public int getIsRecycleViewVisible() {
        return isRecycleViewVisible;
    }

    public void setIsRecycleViewVisible(int isRecycleViewVisible) {
        this.isRecycleViewVisible = isRecycleViewVisible;
        notifyPropertyChanged(BR.isRecycleViewVisible);
    }

    public int getIsVideoViewVisible() {
        return isVideoViewVisible;
    }

    public void setIsVideoViewVisible(int isVideoViewVisible) {
        this.isVideoViewVisible = isVideoViewVisible;
        notifyPropertyChanged(BR.isVideoViewVisible);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyPropertyChanged(BR.position);
    }

    /**
     * 构造方法
     *
     * @param context
     */
    public VideoAlbumViewModel(Context context) {
        this.context = context;
        updateVideoList(FileStore.getPathByType(ConstantFileType.RECORD_VIDEO) + File.separator + "videos");
    }

    /**
     * 语音接口
     *
     * @param text
     */
    public void speechNotification(String text) {
        if (RecordVideoAction.None.name().equals(text)) {

        } else if (RecordVideoAction.Close.name().equals(text)) {
            ((Activity) context).finish();
        } else if (RecordVideoAction.Delete.name().equals(text)) {
            deleteItem(currentPosition);
        } else if (RecordVideoAction.Send.name().equals(text)) {

        } else if (RecordVideoAction.Next.name().equals(text)) {
            currentPosition = currentPosition + 1 >= videoAlbumItemViewModels.size() - 1 ? videoAlbumItemViewModels.size() - 1 : currentPosition + 1;
            VideoAlbumItemViewModel videoAlbumItemViewModel = videoAlbumItemViewModels.get(currentPosition);
            if (!videoAlbumItemViewModel.getClicked()) {
                videoAlbumItemViewModel.setIsClicked(true);
            }
        } else if (RecordVideoAction.Previous.name().equals(text)) {
            currentPosition = currentPosition - 1 >= 0 ? currentPosition - 1 : 0;
            VideoAlbumItemViewModel videoAlbumItemViewModel = videoAlbumItemViewModels.get(currentPosition);
            if (!videoAlbumItemViewModel.getClicked()) {
                videoAlbumItemViewModel.setIsClicked(true);
            }
        } else if (RecordVideoAction.First.name().equals(text)) {
            currentPosition = 0;
            VideoAlbumItemViewModel videoAlbumItemViewModel = videoAlbumItemViewModels.get(currentPosition);
            if (!videoAlbumItemViewModel.getClicked()) {
                videoAlbumItemViewModel.setIsClicked(true);
            }
        } else if (RecordVideoAction.Last.name().equals(text)) {
            currentPosition = videoAlbumItemViewModels.size() - 1;
            VideoAlbumItemViewModel videoAlbumItemViewModel = videoAlbumItemViewModels.get(currentPosition);
            if (!videoAlbumItemViewModel.getClicked()) {
                videoAlbumItemViewModel.setIsClicked(true);
            }
        }
    }

    /**
     * 扫描图片
     */
    private void updateVideoList(final String url) {
        new AsyncTask<Void, VideoAlbumItemViewModel, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listLinkedFiles(url, new EventCallback<VideoAlbumItemViewModel>() {
                    @Override
                    public void onEvent(VideoAlbumItemViewModel videoAlbumItemViewModel) {
                        publishProgress(videoAlbumItemViewModel);
                    }
                });
                return null;
            }

            @Override
            protected void onProgressUpdate(VideoAlbumItemViewModel... values) {
                videoAlbumItemViewModels.add(values[0]);
            }
        }.execute();
    }

    /**
     * 遍历文件夹下面的图片
     *
     * @param strPath
     */
    private void listLinkedFiles(String strPath, EventCallback<VideoAlbumItemViewModel> callback) {
        LinkedList<File> list = new LinkedList<>();
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                list.add(file);
            else {
                String thumbnailPath = getThumbnailPath(file.getAbsolutePath());
                VideoAlbumItemViewModel videoAlbumItemViewModel = new VideoAlbumItemViewModel();
                videoAlbumItemViewModel.setVideoUrl(file.getPath());
                videoAlbumItemViewModel.setThumbnailUrl(thumbnailPath);
                videoAlbumItemViewModel.setCallback(getCallback());
                callback.onEvent(videoAlbumItemViewModel);
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
                        String thumbnailPath = getThumbnailPath(file.getAbsolutePath());
                        VideoAlbumItemViewModel videoAlbumItemViewModel = new VideoAlbumItemViewModel();
                        videoAlbumItemViewModel.setVideoUrl(file.getPath());
                        videoAlbumItemViewModel.setThumbnailUrl(thumbnailPath);
                        videoAlbumItemViewModel.setCallback(getCallback());
                        callback.onEvent(videoAlbumItemViewModel);
                    }
                }
            } else {
                String thumbnailPath = getThumbnailPath(tmp.getAbsolutePath());
                VideoAlbumItemViewModel videoAlbumItemViewModel = new VideoAlbumItemViewModel();
                videoAlbumItemViewModel.setVideoUrl(tmp.getPath());
                videoAlbumItemViewModel.setThumbnailUrl(thumbnailPath);
                videoAlbumItemViewModel.setCallback(getCallback());
                callback.onEvent(videoAlbumItemViewModel);
            }
        }
    }

    /**
     * 获取视频缩略图
     *
     * @param videoPath
     * @return
     */
    private static String getThumbnailPath(String videoPath) {
        File file = new File(videoPath);
        File mediaStorageDir = new File(file.getParent());
        String parentDir = mediaStorageDir.getParent();
        File mediaThumbnailsDir = new File(parentDir, "video_thumbnails");
        String thumbnailPath = mediaThumbnailsDir.getPath() + File.separator + file.getName();
        thumbnailPath = thumbnailPath.replace(".mp4", ".png");
        return thumbnailPath;
    }

    /**
     * 回调接口
     *
     * @return
     */
    @NonNull
    private ICallback getCallback() {
        return new ICallback() {
            /**
             * 点击播放回调
             * @param object
             */
            @Override
            public void postExec(Object object) {
                VideoAlbumItemViewModel videoAlbumItemViewModel = (VideoAlbumItemViewModel) object;
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("videoUrl", videoAlbumItemViewModel.getVideoUrl());
                intent.putExtra("videoTitle", getVideoTitle(videoAlbumItemViewModel.getVideoUrl()));
                context.startActivity(intent);
            }

            @Override
            public void postExec(Object object, String state) {
                if (state.equals("select")) {//选择
                    for (VideoAlbumItemViewModel videoAlbumItemViewModel : videoAlbumItemViewModels) {
                        if (videoAlbumItemViewModel != object) {
                            //其他项
                            videoAlbumItemViewModel.reSet();
                        } else {
                            //当前项
                            currentPosition = videoAlbumItemViewModels.indexOf(videoAlbumItemViewModel);
                            setPosition(currentPosition);
                        }
                    }
                } else if (state.equals("delete_surface")) { //删除
                    VideoAlbumItemViewModel videoAlbumItemViewModel = (VideoAlbumItemViewModel) object;
                    //删除该视频和其缩略图
                    File file1 = new File(videoAlbumItemViewModel.getVideoUrl());
                    file1.delete();
                    File file2 = new File(videoAlbumItemViewModel.getThumbnailUrl());
                    file2.delete();
                    videoAlbumItemViewModels.remove(object);
                }
            }
        };
    }

    /**
     * 获取视频标题
     *
     * @param url
     * @return
     */
    private String getVideoTitle(String url) {
        File file = new File(url);
        String name = file.getName();
        name = name.substring(0, name.lastIndexOf("."));
        return name;
    }

    /**
     * 删除项目
     *
     * @param position
     */
    private void deleteItem(int position) {
        if (videoAlbumItemViewModels.size() > 0 && position >= 0) {
            VideoAlbumItemViewModel model = videoAlbumItemViewModels.get(position);
            File file1 = new File(model.getVideoUrl());
            file1.delete();
            File file2 = new File(model.getThumbnailUrl());
            file2.delete();
            videoAlbumItemViewModels.remove(position);
            if (position <= videoAlbumItemViewModels.size() - 1) {
                currentPosition = position;
            } else {
                currentPosition = videoAlbumItemViewModels.size() - 1;
            }
        }
    }
}
