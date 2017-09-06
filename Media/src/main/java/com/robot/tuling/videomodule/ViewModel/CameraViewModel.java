package com.robot.tuling.videomodule.ViewModel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.robot.common.lib.actions.RecordVideoAction;
import com.robot.common.lib.actions.TakePhotoAciton;
import com.robot.common.lib.animation.ObjectAnimationUtil;
import com.robot.common.lib.constant.ConstantFileType;
import com.robot.common.lib.file.FileStore;
import com.robot.tuling.videomodule.BR;
import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.UIControl.CircleImageView;
import com.robot.tuling.videomodule.View.PhotoAlbumActivity;
import com.robot.tuling.videomodule.View.VideoAlbumActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static android.os.Looper.getMainLooper;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.robot.tuling.videomodule.ViewModel.MediaViewModel.TYPE_TAKE_PHOTO;
import static com.robot.tuling.videomodule.ViewModel.MediaViewModel.TYPE_TAKE_VIDEO;

/**
 * Created by sunnysteam on 2017/8/1.
 */

public class CameraViewModel extends BaseObservable {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private final String cameraType;

    private Context context;
    private SurfaceHolder mSurfaceHolder;
    private Handler childHandler;
    private ImageReader mImageReader;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private MediaRecorder mRecorder;
    private SurfaceView mSurfaceView;
    private MediaPlayer mediaPlayer;
    private String videoUri;
    private String photoUri;
    private boolean mStartedFlg = false;//是否正在录像
    private boolean mIsPlay = false;//是否正在播放录像

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000);
        }
    };

    private int isSaveVisible = GONE;
    private int isTakeVisible = VISIBLE;
    private Bitmap image;
    private Bitmap circleImage;
    private String videoAction = "开始";
    private String photoAction = "返回";
    private String videoThumbnailPath;

    @Bindable
    public int getIsSaveVisible() {
        return isSaveVisible;
    }

    private void setIsSaveVisible(int isSaveVisible) {
        this.isSaveVisible = isSaveVisible;
        notifyPropertyChanged(BR.isSaveVisible);
    }

    @Bindable
    public int getIsTakeVisible() {
        return isTakeVisible;
    }

    private void setIsTakeVisible(int isTakeVisible) {
        this.isTakeVisible = isTakeVisible;
        notifyPropertyChanged(BR.isTakeVisible);
    }

    @Bindable
    public Bitmap getImage() {
        return image;
    }

    private void setImage(Bitmap image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable
    public Bitmap getCircleImage() {
        return circleImage;
    }

    public void setCircleImage(Bitmap circleImage) {
        this.circleImage = circleImage;
        notifyPropertyChanged(BR.circleImage);
    }

    @Bindable
    public String getVideoAction() {
        return videoAction;
    }

    private void setVideoAction(String videoAction) {
        this.videoAction = videoAction;
        notifyPropertyChanged(BR.videoAction);
    }

    @Bindable
    public String getPhotoAction() {
        return photoAction;
    }

    public void setPhotoAction(String photoAction) {
        this.photoAction = photoAction;
        notifyPropertyChanged(BR.photoAction);
    }

    /**
     * 绑定预览图
     *
     * @param imageView
     * @param bitmap
     */
    @BindingAdapter("android:src")
    public static void bindImage(ImageView imageView, Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 绑定按钮图标
     *
     * @param imageView
     * @param tag
     */
    @BindingAdapter("android:tag")
    public static void bindSnapImage(ImageView imageView, String tag) {
        if (tag.equals("开始")) {
            imageView.setTag("开始");
            imageView.setImageResource(R.drawable.snap);
        } else if (tag.equals("停止")) {
            imageView.setTag("停止");
            imageView.setImageResource(R.drawable.snap_stop);
        }
    }

    /**
     * 绑定左上角相册图
     *
     * @param circleImageView
     * @param bitmap
     */
    @BindingAdapter({"circleImage"})
    public static void bindCircleImage(CircleImageView circleImageView, Bitmap bitmap) {
        if (bitmap != null) {
            circleImageView.setImageBitmap(bitmap);
            circleImageView.setVisibility(VISIBLE);
            ObjectAnimationUtil.alphaAnimation(circleImageView, 1000, 0f, 1f);
        } else {
            circleImageView.setVisibility(GONE);
        }
    }

    public CameraViewModel(Context context, String type) {
        this.context = context;
        setCircleImage(null);
        cameraType = type;
        initView(type);
    }

    /**
     * 初始化
     *
     * @param type
     */
    private void initView(String type) {
        if (type.equals("camera")) {
            SurfaceView mSurfaceView = ((SurfaceView) ((Activity) context).findViewById(R.id.surfacecameraview));
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.setKeepScreenOn(true);
            // mSurfaceView添加回调
            mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) { //SurfaceView销毁
                    // 释放Camera资源
                    if (null != mCameraDevice) {
                        mCameraDevice.close();
                        mCameraDevice = null;
                    }
                }
            });
        } else if (type.equals("video")) {
            setVideoAction("开始");
            mSurfaceView = ((SurfaceView) ((Activity) context).findViewById(R.id.surfacevideoview));
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.setKeepScreenOn(true);
            // mSurfaceView添加回调
            mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mSurfaceHolder = holder;
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    mSurfaceHolder = holder;
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) { //SurfaceView销毁
                    // 释放Camera资源
                    mSurfaceView = null;
                    mSurfaceHolder = null;
                    handler.removeCallbacks(runnable);
                    if (mRecorder != null) {
                        mRecorder.release();
                        mRecorder = null;
                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            });
        }
        // 初始化Camera
        initCamera2();
    }

    public void speechNotification(String text) {
        if (cameraType.equals("camera")) {
            if (TakePhotoAciton.None.name().equals(text)) {

            } else if (TakePhotoAciton.Snap.name().equals(text)) {
                takePhoto();
            } else if (TakePhotoAciton.Open.name().equals(text)) {
                showPhotoAlbum();
            }
        } else if (cameraType.equals("video")) {
            if (TakePhotoAciton.None.name().equals(text)) {

            } else if (RecordVideoAction.StartRecord.name().equals(text)) {
                startRecord();
            } else if (RecordVideoAction.StopRecord.name().equals(text)) {
                stopRecord();
            } else if (RecordVideoAction.Open.name().equals(text)) {
                showVideoAlbum();
            }
        }

    }


    /**
     * 拍照界面按钮命令
     *
     * @param view
     */
    public void takePhotoCommand(View view) {
        ObjectAnimationUtil.alphaAnimation(view, 500, 1.0f, 0.8f, 1.0f);
        ObjectAnimationUtil.shrinkAnimation(view, 500, 1.0f, 0.8f, 1.0f);
        if (view.getId() == R.id.imageCircleView) {
            showPhotoAlbum();
        } else if (view.getId() == R.id.img_takePhoto) {
            takePhoto();
        } else if (view.getId() == R.id.img_cancel) {
            backFromPhoto(photoUri);
        }
        //        else if (((Button) view).getText().equals("保存")) {
        //            savePhoto();
        //        } else if (((Button) view).getText().equals("取消")) {
        //            backFormPhoto();
        //        }

    }

    /**
     * 打开相册
     */
    private void showPhotoAlbum() {
        Intent intent = new Intent(context, PhotoAlbumActivity.class);
        context.startActivity(intent);
    }

    /**
     * 从拍照界面返回
     *
     * @param photoUri
     */
    private void backFromPhoto(String photoUri) {
        if (TextUtils.isEmpty(photoUri)) {
            ((Activity) context).finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", photoUri);
            ((Activity) context).setResult(Activity.RESULT_OK, intent);
            ((Activity) context).finish();
        }
    }

    /**
     * 取消当前拍摄的照片
     */
    private void backFormPhoto() {
        setPhotoAction("返回");
        this.image.recycle();
        setIsSaveVisible(GONE);
        setIsTakeVisible(VISIBLE);
        initView("camera");
    }

    /**
     * 从相册返回
     */
    public void backFormPhotoAlbum() {
        initView("camera");
    }

    /**
     * 保存照片
     */
    private void savePhoto() {
        setPhotoAction("返回");
        if (Build.VERSION.SDK_INT >= 24) {
            photoUri = get24MediaFileUri(TYPE_TAKE_PHOTO);
            saveData(photoUri, image);

        } else {
            photoUri = getMediaFileUri(TYPE_TAKE_PHOTO);
            saveData(photoUri, image);
        }
        //        handler.removeCallbacks(runnable);
        setCircleImage(image);
        setIsSaveVisible(GONE);
        setIsTakeVisible(VISIBLE);
        initView("camera");
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (mCameraDevice == null)
            return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取手机方向
            int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 0);
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mCameraCaptureSession.capture(mCaptureRequest, null, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 录像界面按钮命令
     *
     * @param view
     */
    public void takeVideoCommand(View view) {
        ObjectAnimationUtil.alphaAnimation(view, 500, 1.0f, 0.8f, 1.0f);
        ObjectAnimationUtil.shrinkAnimation(view, 500, 1.0f, 0.8f, 1.0f);
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (view.getId() == R.id.imageCircleView) {
            showVideoAlbum();
        } else if (view.getId() == R.id.img_start_stop) {
            if (view.getTag().equals("开始")) {
                startRecord();
            } else if (view.getTag().equals("停止")) {
                stopRecord();
            }
        } else if (view.getId() == R.id.img_play_video) {
            playRecordVideo();
        } else if (view.getId() == R.id.img_back_video) {
            backFromVideo();
        }
    }

    /**
     * 打开相册
     */
    private void showVideoAlbum() {
        Intent intent = new Intent(context, VideoAlbumActivity.class);
        context.startActivity(intent);
    }

    /**
     * 从录制视频返回
     */
    private void backFromVideo() {
        if (mStartedFlg) {
            try {
                handler.removeCallbacks(runnable);
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mStartedFlg = false;
        }
        backFromPhoto(videoUri);
    }

    /**
     * 播放录制的视频
     */
    private void playRecordVideo() {
        mIsPlay = true;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        Uri uri = Uri.parse(videoUri);
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(mSurfaceHolder);
        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        if (mStartedFlg) {
            try {
                handler.removeCallbacks(runnable);
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                setVideoThumbnail();
                setVideoAction("开始");
                setIsSaveVisible(VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mStartedFlg = false;
        }
    }

    /**
     * 从相册返回
     */
    public void backFormVideoAlbum() {
        initView("video");
    }

    /**
     * 创建录制视频的缩略图
     */
    private void setVideoThumbnail() {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoUri, MediaStore.Images.Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(), bitmap.getHeight(),
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        setCircleImage(bitmap);
        File mediaStorageDir = new File(FileStore.getPathByType(ConstantFileType.RECORD_VIDEO), "video_thumbnails");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        String path = mediaStorageDir + File.separator + videoThumbnailPath;
        saveData(path, bitmap);
    }


    /**
     * 开始录制
     */
    private void startRecord() {
        if (mIsPlay) {
            if (mediaPlayer != null) {
                mIsPlay = false;
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        if (!mStartedFlg) {
            setIsSaveVisible(GONE);
            handler.postDelayed(runnable, 1000);
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
            }

            try {
                // 这两项需要放在setOutputFormat之前
                mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                // Set output file format
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                // 这两项需要放在setOutputFormat之后
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

                DisplayMetrics dm = new DisplayMetrics();
                //取得窗口属性
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
                //窗口的宽度
                int screenWidth = dm.widthPixels;
                //窗口高度
                int screenHeight = dm.heightPixels;

                mRecorder.setVideoSize(screenWidth, screenHeight);
                mRecorder.setVideoFrameRate(30);
                mRecorder.setVideoEncodingBitRate(6 * 1024 * 1024);
                //1.5 * 1024 * 1024微信小视频码率
                //                        mRecorder.setOrientationHint(90);
                //设置记录会话的最大持续时间（毫秒）
                mRecorder.setMaxDuration(30 * 1000);
                mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                if (Build.VERSION.SDK_INT >= 24) {
                    videoUri = get24MediaFileUri(TYPE_TAKE_VIDEO);
                } else {
                    videoUri = getMediaFileUri(TYPE_TAKE_VIDEO);
                }
                if (videoUri != null) {
                    mRecorder.setOutputFile(videoUri);
                    mRecorder.prepare();
                    mRecorder.start();
                    mStartedFlg = true;
                    setVideoAction("停止");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化摄像头
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initCamera2() {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        Handler mainHandler = new Handler(getMainLooper());
        String mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;
        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() { //可以在这里处理拍照得到的临时照片 例如，写入本地
            @Override
            public void onImageAvailable(final ImageReader reader) {
                mCameraDevice.close();
                setIsTakeVisible(INVISIBLE);
                setPhotoAction("取消");
                setIsSaveVisible(VISIBLE);
                // 拿到拍照照片数据
                Image image = reader.acquireNextImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);//由缓冲区存入字节数组
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    setImage(bitmap);
                    savePhoto();
                }
            }
        }, mainHandler);
        //获取摄像头管理
        CameraManager mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                //默认打开后置摄像头
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT)
                    continue;
                mCameraID = cameraId;
                break;
            }
            //打开摄像头
            mCameraManager.openCamera(mCameraID, stateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 摄像头创建监听
     */
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {//打开摄像头
            mCameraDevice = camera;
            //开启预览
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {//关闭摄像头
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {//发生错误
            Toast.makeText(context, "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 开始预览
     */
    private void takePreview() {
        try {
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice)
                        return;
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        // 自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(context, "配置失败", Toast.LENGTH_SHORT).show();
                }
            }, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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
     * 获取文件路径
     *
     * @param type
     * @return
     */
    private String getMediaFileUri(int type) {
        File mediaStorageDir = null;
        if (type == TYPE_TAKE_PHOTO) {
            mediaStorageDir = new File(FileStore.getPathByType(ConstantFileType.IMAGE), "photos");
        } else if (type == TYPE_TAKE_VIDEO) {
            mediaStorageDir = new File(FileStore.getPathByType(ConstantFileType.RECORD_VIDEO), "videos");
        }
        if (mediaStorageDir != null && !mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        if (type == TYPE_TAKE_PHOTO) {
            return mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png";
        } else if (type == TYPE_TAKE_VIDEO) {
            videoThumbnailPath = "VID_" + timeStamp + ".png";
            return mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";
        } else {
            return null;
        }
    }

    /**
     * 24版本以上的获取文件路径
     *
     * @param type
     * @return
     */
    private String get24MediaFileUri(int type) {
        File mediaStorageDir = null;
        if (type == TYPE_TAKE_PHOTO) {
            mediaStorageDir = new File(FileStore.getPathByType(ConstantFileType.IMAGE), "photos");
        } else if (type == TYPE_TAKE_VIDEO) {
            mediaStorageDir = new File(FileStore.getPathByType(ConstantFileType.RECORD_VIDEO), "videos");
        }
        if (mediaStorageDir != null && !mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        if (type == TYPE_TAKE_PHOTO) {
            return mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png";
        } else if (type == TYPE_TAKE_VIDEO) {
            videoThumbnailPath = "VID_" + timeStamp + ".png";
            return mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";
        } else {
            return null;
        }
    }

}
