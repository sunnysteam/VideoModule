package com.robot.tuling.videomodule.ViewModel;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.robot.common.lib.actions.PlayMusicAction;
import com.robot.common.lib.animation.ObjectAnimationUtil;
import com.robot.tuling.videomodule.Application.AppCache;
import com.robot.tuling.videomodule.BR;
import com.robot.tuling.videomodule.Interface.ICallback;
import com.robot.tuling.videomodule.Model.MusicModel;
import com.robot.tuling.videomodule.Premission.PermissionReq;
import com.robot.tuling.videomodule.Premission.PermissionResult;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.Service.EventCallback;
import com.robot.tuling.videomodule.Service.OnPlayerEventListener;
import com.robot.tuling.videomodule.Service.PlayService;
import com.robot.tuling.videomodule.UIControl.Lrc.LrcUIControl;
import com.robot.tuling.videomodule.UIControl.MusicUIControl;
import com.robot.tuling.videomodule.utils.ImageUtils;
import com.robot.tuling.videomodule.utils.SystemUtils;

import java.io.File;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by sunnysteam on 2017/8/8.
 */

public class MusicViewModel extends BaseObservable implements OnPlayerEventListener {

    private static boolean isTouchSeekBar = false;
    public ObservableList<MusicItemViewModel> musicItemViewModels = new ObservableArrayList<>();
    public ItemView itemView = ItemView.of(BR.musicitemviewmodel, R.layout.item_music);
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Context context;

    @Bindable
    private MusicModel callbackMusicModel;
    @Bindable
    private String playingState = "";
    @Bindable
    private String playProgress = "0";
    @Bindable
    private int seekProgress = 0;
    @Bindable
    private int seekMax = 0;
    @Bindable
    private String currentTime = "00:00";
    @Bindable
    private String totalTime = "00:00";
    @Bindable
    private int position = 0;

    private int mLastProgress = 0;
    public PlayServiceConnection mPlayServiceConnection;
    private boolean isScanCompleted = false;

    /**
     * 绑定播放状态
     *
     * @param imageView
     * @param isPlaying
     */
    @BindingAdapter({"isPlaying"})
    public static void bindPlayState(ImageView imageView, String isPlaying) {
        if (isPlaying.equals("true")) {
            imageView.setSelected(true);
        } else {
            imageView.setSelected(false);
        }
    }

    /**
     * 绑定播放状态
     *
     * @param musicUIControl
     * @param isPlaying
     */
    @BindingAdapter({"isPlaying"})
    public static void bindMusicUIState(MusicUIControl musicUIControl, String isPlaying) {
        if (isPlaying.equals("true")) {
            musicUIControl.start();
        } else if (isPlaying.equals("false")) {
            musicUIControl.pause();
        } else {
            musicUIControl.initNeedle(false);
        }
    }

    /**
     * 绑定歌词播放时间
     *
     * @param lrcUIControl
     * @param playProgress
     */
    @BindingAdapter({"progress"})
    public static void bindPlayProgress(LrcUIControl lrcUIControl, String playProgress) {
        if (Long.valueOf(playProgress) >= lrcUIControl.mLastTime) {
            lrcUIControl.updateTime(Long.valueOf(playProgress));
        } else {
            lrcUIControl.onDrag(Long.valueOf(playProgress));
        }
    }

    /**
     * 绑定背景图
     *
     * @param imageView
     * @param url
     */
    @BindingAdapter("android:src")
    public static void bindMusicSrc(final ImageView imageView, final String url) {
        if (url != null) {
            imageView.setAlpha(0f);
            new Thread() {
                @Override
                public void run() {
                    Bitmap bitmap = ImageUtils.loadBitmap(url, imageView.getWidth() / 2);
                    bitmap = ImageUtils.blur(bitmap);
                    final Bitmap finalBitmap = bitmap;
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(finalBitmap);
                            ObjectAnimationUtil.alphaAnimation(imageView, 200, 0, 1.0f);
                        }
                    });
                }
            }.start();
        } else {
            imageView.setImageBitmap(null);
        }
    }

    /**
     * 绑定进度条
     *
     * @param seekBar
     * @param playingState
     */
    @BindingAdapter({"isPlaying"})
    public static void bindSeekBar(SeekBar seekBar, String playingState) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouchSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (AppCache.getPlayService().isPlaying() || AppCache.getPlayService().isPausing()) {
                    int progress = seekBar.getProgress();
                    AppCache.getPlayService().seekTo(progress);
                } else {
                    seekBar.setProgress(0);
                }
                isTouchSeekBar = false;
            }
        });
    }

    /**
     * 绑定专辑封面
     *
     * @param musicUIControl
     * @param url
     */
    @BindingAdapter({"coverpath"})
    public static void bindAlbumCover(MusicUIControl musicUIControl, String url) {
        if (url != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            if (bitmap != musicUIControl.lastCoverBitmap) {
                musicUIControl.setCoverBitmap(bitmap);
            }
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.play_page_default_cover);
            if (bitmap != null) {
                musicUIControl.setCoverBitmap(bitmap);
            }
        }
    }

    /**
     * 绑定歌词
     *
     * @param lrcUIControl
     * @param url
     */
    @BindingAdapter({"lrcpath"})
    public static void bindMusicLrc(LrcUIControl lrcUIControl, String url) {
        if (url != null) {
            File file = new File(url);
            if (file.exists()) {
                lrcUIControl.loadLrc(file);
            } else {
                lrcUIControl.setLabel("无歌词");
            }
        }
    }

    /**
     * 绑定当前选定位置
     *
     * @param recyclerView
     * @param position
     */
    @BindingAdapter({"position"})
    public static void bindPosition(RecyclerView recyclerView, int position) {
        recyclerView.scrollToPosition(position);
    }

    public MusicModel getCallbackMusicModel() {
        return callbackMusicModel;
    }

    public void setCallbackMusicModel(MusicModel callbackMusicModel) {
        this.callbackMusicModel = callbackMusicModel;
        notifyPropertyChanged(BR.callbackMusicModel);
    }

    public String getPlayingState() {
        return playingState;
    }

    public void setPlayingState(String playingState) {
        this.playingState = playingState;
        notifyPropertyChanged(BR.playingState);
    }

    public String getPlayProgress() {
        return playProgress;
    }

    public void setPlayProgress(String playProgress) {
        this.playProgress = playProgress;
        notifyPropertyChanged(BR.playProgress);
    }

    public int getSeekProgress() {
        return seekProgress;
    }

    public void setSeekProgress(int seekProgress) {
        this.seekProgress = seekProgress;
        notifyPropertyChanged(BR.seekProgress);
    }

    public int getSeekMax() {
        return seekMax;
    }

    public void setSeekMax(int seekMax) {
        this.seekMax = seekMax;
        notifyPropertyChanged(BR.seekMax);
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
        notifyPropertyChanged(BR.currentTime);
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
        notifyPropertyChanged(BR.totalTime);
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
    public MusicViewModel(Context context) {
        this.context = context;
        checkService();

    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onClickCommand(View view) {
        ObjectAnimationUtil.alphaAnimation(view, 500, 1.0f, 0.8f, 0.6f, 0.8f, 1.0f);
        ObjectAnimationUtil.shrinkAnimation(view, 500, 1.0f, 0.8f, 1.0f);
        if (view.getId() == R.id.iv_play) {
            getPlayService().playPause();
        } else if (view.getId() == R.id.iv_next) {
            getPlayService().next();
        } else if (view.getId() == R.id.iv_prev) {
            getPlayService().prev();
        }
    }

    /**
     * 语音入口
     *
     * @param text
     */
    public void speechNotification(String text) {
        if (PlayMusicAction.None.name().equals(text)) {

        } else if (PlayMusicAction.Play.name().equals(text)) {
            getPlayService().playPause();
        } else if (PlayMusicAction.Pause.name().equals(text)) {
            getPlayService().playPause();
        } else if (PlayMusicAction.Resume.name().equals(text)) {
            getPlayService().playPause();
        } else if (PlayMusicAction.Next.name().equals(text)) {
            getPlayService().next();
        } else if (PlayMusicAction.Previous.name().equals(text)) {
            getPlayService().prev();
        } else if (PlayMusicAction.First.name().equals(text)) {
            final MusicItemViewModel musicModel = musicItemViewModels.get(0);
            setPlayingFirstOrLast(musicModel);
        } else if (PlayMusicAction.Last.name().equals(text)) {
            final MusicItemViewModel musicModel = musicItemViewModels.get(musicItemViewModels.size() - 1);
            setPlayingFirstOrLast(musicModel);
        }
    }

    /**
     * 播放第一个/最后一个
     *
     * @param musicModel
     */
    private void setPlayingFirstOrLast(MusicItemViewModel musicModel) {
        int position = setBackColor(musicModel.getMusicModel());
        setPosition(position);
        if (getCallbackMusicModel() == null) {
            setCallbackMusicModel(musicModel.getMusicModel());
            AppCache.getPlayService().play(musicItemViewModels.indexOf(musicModel));
        } else {
            if (getCallbackMusicModel().getMusicId() != musicModel.getMusicModel().getMusicId()) {
                setCallbackMusicModel(null);
                setCallbackMusicModel(musicModel.getMusicModel());
                AppCache.getPlayService().play(musicItemViewModels.indexOf(musicModel));
            }
        }
    }

    /**
     * 获得当前的音乐服务
     *
     * @return
     */
    public PlayService getPlayService() {
        return AppCache.getPlayService();
    }

    /**
     * 检查服务的运行
     */
    private void checkService() {
        if (getPlayService() == null) {
            startService();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindService();
                }
            }, 1000);
        } else {
            bindService();
        }
    }

    /**
     * 开启音乐播放的服务
     */
    private void startService() {
        Intent intent = new Intent(context, PlayService.class);
        context.startService(intent);
    }

    /**
     * 绑定音乐播放的服务
     */
    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(context, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        context.bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onChange(MusicModel music) {
        onChangeImpl(music);
    }

    @Override
    public void onPlayerStart() {
        setPlayingState("true");
    }

    @Override
    public void onPlayerPause() {
        setPlayingState("false");
    }

    @Override
    public void onPublish(int progress) {
        publishProgress(progress);

    }

    /**
     * 进度变化
     *
     * @param progress
     */
    private void publishProgress(int progress) {
        if (getCallbackMusicModel() == null && isScanCompleted) {
            MusicModel playingMusic = getPlayService().getPlayingMusic();
            int position = setBackColor(playingMusic);
            setPosition(position);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCallbackMusicModel(getPlayService().getPlayingMusic());
                }
            }, 500);
            setSeekMax(playingMusic.getMusic_duration());
            setTotalTime(SystemUtils.formatTime("mm:ss", playingMusic.getMusic_duration()));
        }
        if (getCallbackMusicModel() != null && TextUtils.isEmpty(getPlayingState())) {
            callbackMusicModel.setMusic_album_cover_path(callbackMusicModel.getMusic_album_cover_path());
            setPlayingState("true");
        }

        if (!isTouchSeekBar) {
            setSeekProgress(progress);
        }

        setPlayProgress(String.valueOf(progress));
        //更新当前播放时间
        if (progress - mLastProgress >= 1000) {
            setCurrentTime(SystemUtils.formatTime("mm:ss", progress));
            mLastProgress = progress;
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onTimer(long remain) {

    }

    @Override
    public void onMusicListUpdate() {
        isScanCompleted = true;

        PlayService playService = getPlayService();
        if (playService != null && playService.isPausing()) {
            publishProgress(playService.getPlayingPosition());
        }
    }

    /**
     * 切换歌曲触发的方法
     *
     * @param musicModel
     */
    private void onChangeImpl(MusicModel musicModel) {
        if (musicModel == null) {
            return;
        }
        int position = setBackColor(musicModel);
        setPosition(position);
        setCallbackMusicModel(musicModel);
        setSeekMax(musicModel.getMusic_duration());
        setTotalTime(SystemUtils.formatTime("mm:ss", musicModel.getMusic_duration()));
        mLastProgress = 0;
        setCurrentTime("00:00");
    }

    /**
     * 服务连接
     */
    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            AppCache.setPlayService(playService);
            getPlayService().setOnPlayEventListener(MusicViewModel.this);
            PermissionReq.with(((Activity) context))
                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .result(new PermissionResult() {
                        @Override
                        public void onGranted() {
                            scanMusic(playService);
                        }

                        @Override
                        public void onDenied() {
                            ((Activity) context).finish();
                            playService.quit();
                        }
                    })
                    .request();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    /**
     * 扫描指定文件夹下面的音乐文件
     *
     * @param playService
     */
    private void scanMusic(final PlayService playService) {
        playService.updateMusicList(new EventCallback<MusicModel>() {
            @Override
            public void onEvent(MusicModel musicModel) {
                MusicItemViewModel musicItemViewModel = new MusicItemViewModel(musicModel);
                musicItemViewModel.setCallback(new ICallback() {
                    @Override
                    public void postExec(Object object) {
                        //列表中音乐文件的点击回调
                        final MusicItemViewModel musicModel = (MusicItemViewModel) object;
                        //                        setBackColor(musicModel.getMusicModel());
                        if (getCallbackMusicModel() == null) {
                            setCallbackMusicModel(musicModel.getMusicModel());
                            playService.play(musicItemViewModels.indexOf(musicModel));
                        } else {
                            if (getCallbackMusicModel().getMusicId() != musicModel.getMusicModel().getMusicId()) {
                                setCallbackMusicModel(null);
                                setCallbackMusicModel(musicModel.getMusicModel());
                                playService.play(musicItemViewModels.indexOf(musicModel));
                            }
                        }
                    }

                    @Override
                    public void postExec(Object object, String state) {

                    }
                });
                musicItemViewModels.add(musicItemViewModel);
            }
        });
    }

    /**
     * 设置点击时其他项的颜色
     *
     * @param musicModel
     * @return
     */
    private int setBackColor(MusicModel musicModel) {
        int position = 0;
        for (MusicItemViewModel musicItemViewModel : musicItemViewModels) {
            if (musicItemViewModel.getMusicModel().getMusicId() == musicModel.getMusicId()) {
                musicItemViewModel.setBackColor(Color.WHITE);
                musicItemViewModel.setTextColor(Color.BLACK);
                position = musicItemViewModels.indexOf(musicItemViewModel);
            } else {
                musicItemViewModel.setBackColor(Color.TRANSPARENT);
                musicItemViewModel.setTextColor(Color.WHITE);
            }
        }
        return position;
    }
}
