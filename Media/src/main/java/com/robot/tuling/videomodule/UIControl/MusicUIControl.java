package com.robot.tuling.videomodule.UIControl;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.robot.tuling.videomodule.R;
import com.robot.tuling.videomodule.utils.ImageUtils;

/**
 * Created by sunnysteam on 2017/8/9.
 */

public class MusicUIControl extends View implements ValueAnimator.AnimatorUpdateListener {

    private static final long TIME_UPDATE = 10L;
    private static final float DISC_ROTATION_INCREASE = 0.2f;
    private static final float NEEDLE_ROTATION_PLAY = -5.0f;
    private static final float NEEDLE_ROTATION_PAUSE = -25.0f;
    private Handler mHandler = new Handler();
    private Bitmap mDiscBitmap;
    private Bitmap mCoverBitmap;
    private Bitmap mNeedleBitmap;
    private Drawable mTopLine;
    private Drawable mCoverBorder;
    private int mTopLineHeight;
    private int mCoverBorderWidth;
    private Matrix mDiscMatrix = new Matrix();
    private Matrix mCoverMatrix = new Matrix();
    private Matrix mNeedleMatrix = new Matrix();
    private ValueAnimator mPlayAnimator;
    private ValueAnimator mPauseAnimator;
    private float mDiscRotation = 0.0f;
    private float mNeedleRotation = NEEDLE_ROTATION_PLAY;
    private boolean isPlaying = false;
    public boolean isInited = false;

    // 图片起始坐标
    private Point mDiscPoint = new Point();
    private Point mCoverPoint = new Point();
    private Point mNeedlePoint = new Point();
    // 旋转中心坐标
    private Point mDiscCenterPoint = new Point();
    private Point mCoverCenterPoint = new Point();
    private Point mNeedleCenterPoint = new Point();
    public Bitmap lastCoverBitmap;
    private boolean isFirst = true;

    public MusicUIControl(Context context) {
        this(context, null);
    }

    public MusicUIControl(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicUIControl(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mTopLine = getResources().getDrawable(R.drawable.play_page_cover_top_line_shape, null);
        mCoverBorder = getResources().getDrawable(R.drawable.play_page_cover_border_shape, null);
        mDiscBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_disc);
        //                mDiscBitmap = ImageUtils.resizeImage(mDiscBitmap, (int) (getWidth() * 0.75),
        //                        (int) (getHeight() * 0.75));
        mDiscBitmap = ImageUtils.resizeImage(mDiscBitmap, (int) (getWidth() * 0.75),
                (int) (getWidth() * 0.75));
        mCoverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_default_cover);
        //        mCoverBitmap = ImageUtils.resizeImage(mCoverBitmap, getWidth() / 2, getHeight() / 2);
        mCoverBitmap = ImageUtils.resizeImage(mCoverBitmap, getWidth() / 2, getWidth() / 2);
        mNeedleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_needle);
        //        mNeedleBitmap = ImageUtils.resizeImage(mNeedleBitmap, (int) (getWidth() * 0.25),
        //                (int) (getHeight() * 0.375));
        mNeedleBitmap = ImageUtils.resizeImage(mNeedleBitmap, (int) (getWidth() * 0.25),
                (int) (getWidth() * 0.375));
        mTopLineHeight = dp2px(1);
        mCoverBorderWidth = dp2px(1);

        mPlayAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PAUSE, NEEDLE_ROTATION_PLAY);
        mPlayAnimator.setDuration(300);
        mPlayAnimator.addUpdateListener(this);
        mPauseAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PLAY, NEEDLE_ROTATION_PAUSE);
        mPauseAnimator.setDuration(300);
        mPauseAnimator.addUpdateListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isFirst) {
            init();
            isFirst = false;
        }
        initSize();
        isInited = true;
    }

    /**
     * 确定图片起始坐标与旋转中心坐标
     */
    private void initSize() {
        //                int discOffsetY = mNeedleBitmap.getHeight() / 2;
        int discOffsetY = mNeedleBitmap.getWidth() / 2;
        mDiscPoint.x = (getWidth() - mDiscBitmap.getWidth()) / 2;
        mDiscPoint.y = discOffsetY;
        mCoverPoint.x = (getWidth() - mCoverBitmap.getWidth()) / 2;
        //                mCoverPoint.y = discOffsetY + (mDiscBitmap.getHeight() - mCoverBitmap.getHeight()) / 2;
        mCoverPoint.y = discOffsetY + (mDiscBitmap.getWidth() - mCoverBitmap.getWidth()) / 2;

        mNeedlePoint.x = getWidth() / 2 - mNeedleBitmap.getWidth() / 6;
        mNeedlePoint.y = -mNeedleBitmap.getWidth() / 6;
        mDiscCenterPoint.x = getWidth() / 2;
        //        mDiscCenterPoint.y = mDiscBitmap.getHeight() / 2 + discOffsetY;
        mDiscCenterPoint.y = mDiscBitmap.getWidth() / 2 + discOffsetY;
        mCoverCenterPoint.x = mDiscCenterPoint.x;
        mCoverCenterPoint.y = mDiscCenterPoint.y;
        mNeedleCenterPoint.x = mDiscCenterPoint.x;
        mNeedleCenterPoint.y = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1.绘制顶部虚线
        mTopLine.setBounds(0, 0, getWidth(), mTopLineHeight);
        mTopLine.draw(canvas);
        // 2.绘制黑胶唱片外侧半透明边框
        //                mCoverBorder.setBounds(mDiscPoint.x - mCoverBorderWidth, mDiscPoint.y - mCoverBorderWidth,
        //                        mDiscPoint.x + mDiscBitmap.getWidth() + mCoverBorderWidth, mDiscPoint.y +
        //                                mDiscBitmap.getHeight() + mCoverBorderWidth);
        mCoverBorder.setBounds(mDiscPoint.x - mCoverBorderWidth, mDiscPoint.y - mCoverBorderWidth,
                mDiscPoint.x + mDiscBitmap.getWidth() + mCoverBorderWidth, mDiscPoint.y +
                        mDiscBitmap.getWidth() + mCoverBorderWidth);
        mCoverBorder.draw(canvas);

        // 4.绘制封面
        mCoverMatrix.setRotate(mDiscRotation, mCoverCenterPoint.x, mCoverCenterPoint.y);
        mCoverMatrix.preTranslate(mCoverPoint.x, mCoverPoint.y);
        canvas.drawBitmap(mCoverBitmap, mCoverMatrix, null);
        // 3.绘制黑胶
        // 设置旋转中心和旋转角度，setRotate和preTranslate顺序很重要
        mDiscMatrix.setRotate(mDiscRotation, mDiscCenterPoint.x, mDiscCenterPoint.y);
        // 设置图片起始坐标
        mDiscMatrix.preTranslate(mDiscPoint.x, mDiscPoint.y);
        canvas.drawBitmap(mDiscBitmap, mDiscMatrix, null);
        // 5.绘制指针
        mNeedleMatrix.setRotate(mNeedleRotation, mNeedleCenterPoint.x, mNeedleCenterPoint.y);
        mNeedleMatrix.preTranslate(mNeedlePoint.x, mNeedlePoint.y);
        canvas.drawBitmap(mNeedleBitmap, mNeedleMatrix, null);
    }

    public void initNeedle(boolean isPlaying) {
        mNeedleRotation = isPlaying ? NEEDLE_ROTATION_PLAY : NEEDLE_ROTATION_PAUSE;
        invalidate();
    }

    public void setCoverBitmap(Bitmap bitmap) {
        //        if (bitmap != null) {
        //            mCoverBitmap = bitmap;
        //            Log.e(TAG, "setCoverBitmap: "+bitmap.getHeight() );
        if (bitmap != null) {
            mCoverBitmap = bitmap;
            lastCoverBitmap = bitmap;
            //            Log.e(TAG, "setCoverBitmap: " + bitmap.getWidth());
        } else {
            mCoverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_default_cover);
        }
        //        mCoverBitmap = ImageUtils.resizeImage(mCoverBitmap, getWidth() / 2, getHeight() / 2);
        mCoverBitmap = ImageUtils.resizeImage(mCoverBitmap, getWidth() / 2, getWidth() / 2);
        mDiscRotation = 0.0f;
        invalidate();
    }

    public void start() {
        if (isPlaying) {
            return;
        }
        isPlaying = true;
        mHandler.post(mRotationRunnable);
        mPlayAnimator.start();
    }

    public void pause() {
        if (!isPlaying) {
            return;
        }
        isPlaying = false;
        mHandler.removeCallbacks(mRotationRunnable);
        mPauseAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mNeedleRotation = (float) animation.getAnimatedValue();
        invalidate();
    }

    private Runnable mRotationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                mDiscRotation += DISC_ROTATION_INCREASE;
                if (mDiscRotation >= 360) {
                    mDiscRotation = 0;
                }
                invalidate();
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
