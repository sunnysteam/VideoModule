package com.robot.tuling.videomodule.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.TimeZone;

import me.tatarka.bindingcollectionadapter.ItemView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by sunnysteam on 2017/8/29.
 */

public class MovieAlbumViewModel extends BaseObservable {

    public ItemView itemView = ItemView.of(BR.movieAlbumItemViewModel, R.layout.item_movie_album);
    public ObservableList<MovieAlbumItemViewModel> movieAlbumItemViewModels = new ObservableArrayList<>();
    private int currentPosition = -1;

    /**
     * 绑定当前选择的位置
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
    public MovieAlbumViewModel(Context context, String type) {
        this.context = context;
        String fileDir = "";
        if (type.equals("movie")) {
            fileDir = FileStore.getPathByType(ConstantFileType.RECORD_VIDEO) + File.separator + "movies";
        } else if (type.equals("education")) {
            fileDir = FileStore.getPathByType(ConstantFileType.RECORD_VIDEO) + File.separator + "education";
        }
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        updateVideoList(fileDir);
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
            currentPosition = currentPosition + 1 >= movieAlbumItemViewModels.size() - 1 ? movieAlbumItemViewModels.size() - 1 : currentPosition + 1;
            MovieAlbumItemViewModel movieAlbumItemViewModel = movieAlbumItemViewModels.get(currentPosition);
            if (!movieAlbumItemViewModel.getClicked()) {
                movieAlbumItemViewModel.setIsClicked(true);
            }
        } else if (RecordVideoAction.Previous.name().equals(text)) {
            currentPosition = currentPosition - 1 >= 0 ? currentPosition - 1 : 0;
            MovieAlbumItemViewModel movieAlbumItemViewModel = movieAlbumItemViewModels.get(currentPosition);
            if (!movieAlbumItemViewModel.getClicked()) {
                movieAlbumItemViewModel.setIsClicked(true);
            }
        } else if (RecordVideoAction.First.name().equals(text)) {
            currentPosition = 0;
            MovieAlbumItemViewModel movieAlbumItemViewModel = movieAlbumItemViewModels.get(currentPosition);
            if (!movieAlbumItemViewModel.getClicked()) {
                movieAlbumItemViewModel.setIsClicked(true);
            }
        } else if (RecordVideoAction.Last.name().equals(text)) {
            currentPosition = movieAlbumItemViewModels.size() - 1;
            MovieAlbumItemViewModel movieAlbumItemViewModel = movieAlbumItemViewModels.get(currentPosition);
            if (!movieAlbumItemViewModel.getClicked()) {
                movieAlbumItemViewModel.setIsClicked(true);
            }
        }
    }

    /**
     * 扫描图片
     */
    private void updateVideoList(final String url) {
        new AsyncTask<Void, MovieAlbumItemViewModel, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listLinkedFiles(url, new EventCallback<MovieAlbumItemViewModel>() {
                    @Override
                    public void onEvent(MovieAlbumItemViewModel movieAlbumItemViewModel) {
                        publishProgress(movieAlbumItemViewModel);
                    }
                });
                return null;
            }

            @Override
            protected void onProgressUpdate(MovieAlbumItemViewModel... values) {
                movieAlbumItemViewModels.add(values[0]);
            }
        }.execute();
    }

    /**
     * 遍历文件夹下面的图片
     *
     * @param strPath
     */
    private void listLinkedFiles(String strPath, EventCallback<MovieAlbumItemViewModel> callback) {
        LinkedList<File> list = new LinkedList<>();
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                list.add(file);
            else {
                callback.onEvent(setFile(file));
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
                        callback.onEvent(setFile(file));
                    }
                }
            } else {
                callback.onEvent(setFile(tmp));
            }
        }
    }

    /**
     * 生成每一项的对象
     *
     * @param file
     * @return
     */
    private MovieAlbumItemViewModel setFile(File file) {
        String thumbnailPath = getThumbnailPath(file.getAbsolutePath());
        MovieAlbumItemViewModel movieAlbumItemViewModel = new MovieAlbumItemViewModel();
        movieAlbumItemViewModel.setVideoUrl(file.getPath());
        MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(file.getPath()));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String duration = sdf.format(mediaPlayer.getDuration());
        movieAlbumItemViewModel.setDuration(duration);
        movieAlbumItemViewModel.setFileName(file.getName().replace(".mp4", ""));
        mediaPlayer.release();
        movieAlbumItemViewModel.setThumbnailUrl(thumbnailPath);
        movieAlbumItemViewModel.setCallback(getCallback());
        return movieAlbumItemViewModel;
    }

    /**
     * 获取视频缩略图
     *
     * @param videoPath
     * @return
     */
    private String getThumbnailPath(String videoPath) {
        File file = new File(videoPath);
        File mediaStorageDir = new File(file.getParent());
        String parentDir = mediaStorageDir.getParent();
        File mediaThumbnailsDir = new File(parentDir, "video_thumbnails");
        if (!mediaThumbnailsDir.exists()) {
            mediaThumbnailsDir.mkdirs();
        }
        String thumbnailPath = mediaThumbnailsDir.getPath() + File.separator + file.getName();
        thumbnailPath = thumbnailPath.replace(".mp4", ".png");
        File thumbnailFile = new File(thumbnailPath);
        if (!thumbnailFile.exists()) {
            setVideoThumbnail(videoPath, thumbnailPath);
        }
        return thumbnailPath;
    }

    /**
     * 创建录制视频的缩略图
     */
    private void setVideoThumbnail(String videoPath, String thumbnailPath) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        saveData(thumbnailPath, bitmap);
    }

    /**
     * 保存图片
     *
     * @param uri
     * @param mBitmap
     */
    private void saveData(String uri, Bitmap mBitmap) {

        //创建file对象
        File f = new File(String.valueOf(uri));
        try {
            //创建
            f.createNewFile();
        } catch (IOException ignored) {

        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //原封不动的保存在内存卡上
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            if (fOut != null) {
                fOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fOut != null) {
                fOut.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                MovieAlbumItemViewModel movieAlbumItemViewModel = (MovieAlbumItemViewModel) object;
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("videoUrl", movieAlbumItemViewModel.getVideoUrl());
                intent.putExtra("videoTitle", getVideoTitle(movieAlbumItemViewModel.getVideoUrl()));
                context.startActivity(intent);
            }

            /**
             * 选择回调
             * @param object
             * @param state
             */
            @Override
            public void postExec(Object object, String state) {
                if (state.equals("select")) {
                    //选择
                    for (MovieAlbumItemViewModel movieAlbumItemViewModel : movieAlbumItemViewModels) {
                        if (movieAlbumItemViewModel != object) {
                            //选择当前项后将其他项置为未选择状态
                            movieAlbumItemViewModel.reSet();
                        } else {
                            //选择当前项
                            currentPosition = movieAlbumItemViewModels.indexOf(movieAlbumItemViewModel);
                            setPosition(currentPosition);
                        }
                    }
                } else if (state.equals("delete_surface")) {
                    //删除
                    MovieAlbumItemViewModel movieAlbumItemViewModel = (MovieAlbumItemViewModel) object;
                    File file1 = new File(movieAlbumItemViewModel.getVideoUrl());
                    file1.delete();
                    File file2 = new File(movieAlbumItemViewModel.getThumbnailUrl());
                    file2.delete();
                    movieAlbumItemViewModels.remove(object);
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
        if (movieAlbumItemViewModels.size() > 0 && position >= 0) {
            MovieAlbumItemViewModel model = movieAlbumItemViewModels.get(position);
            //删除该视频文件和它的缩略图
            File file1 = new File(model.getVideoUrl());
            file1.delete();
            File file2 = new File(model.getThumbnailUrl());
            file2.delete();
            movieAlbumItemViewModels.remove(position);
            if (position <= movieAlbumItemViewModels.size() - 1) {
                currentPosition = position;
            } else {
                currentPosition = movieAlbumItemViewModels.size() - 1;
            }
        }
    }
}
