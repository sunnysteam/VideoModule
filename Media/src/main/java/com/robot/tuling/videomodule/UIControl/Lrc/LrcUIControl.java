package com.robot.tuling.videomodule.UIControl.Lrc;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.robot.tuling.videomodule.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunnysteam on 2017/8/11.
 */

public class LrcUIControl extends View {
    private List<LrcEntry> mLrcEntryList = new ArrayList<>();
    private TextPaint mPaint = new TextPaint();
    private float mTextSize;
    private float mDividerHeight;
    private long mAnimationDuration;
    private int mNormalColor;
    private int mCurrentColor;
    private String mLabel;
    private float mLrcPadding;
    private ValueAnimator mAnimator;
    private float mAnimateOffset;
    private long mNextTime = 0L;
    private int mCurrentLine = 0;
    private Object mFlag;
    public long mLastTime;

    public LrcUIControl(Context context) {
        this(context, null);
    }

    public LrcUIControl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcUIControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LrcUIControl);
        mTextSize = ta.getDimension(R.styleable.LrcUIControl_lrcTextSize, LrcUtils.sp2px(getContext(), 12));
        mDividerHeight = ta.getDimension(R.styleable.LrcUIControl_lrcDividerHeight, LrcUtils.dp2px(getContext(), 16));
        mAnimationDuration = ta.getInt(R.styleable.LrcUIControl_lrcAnimationDuration, 1000);
        mAnimationDuration = (mAnimationDuration < 0) ? 1000 : mAnimationDuration;
        mNormalColor = ta.getColor(R.styleable.LrcUIControl_lrcNormalTextColor, 0xFFA4A4A4);
        mCurrentColor = ta.getColor(R.styleable.LrcUIControl_lrcCurrentTextColor, 0xFFFFFFFF);
        mLabel = ta.getString(R.styleable.LrcUIControl_lrcLabel);
        mLabel = TextUtils.isEmpty(mLabel) ? "无歌词" : mLabel;
        mLrcPadding = ta.getDimension(R.styleable.LrcUIControl_lrcPadding, 0);
        ta.recycle();

        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initEntryList();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(0, mAnimateOffset);

        // 中心Y坐标
        float centerY = getHeight() / 2;

        mPaint.setColor(mCurrentColor);

        // 无歌词文件
        if (!hasLrc()) {
            @SuppressLint("DrawAllocation")
            StaticLayout staticLayout = new StaticLayout(mLabel, mPaint, (int) getLrcWidth(),
                    Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
            drawText(canvas, staticLayout, centerY - staticLayout.getLineCount() * mTextSize / 2);
            return;
        }

        // 画当前行
        float currY = centerY - mLrcEntryList.get(mCurrentLine).getTextHeight() / 2;
        drawText(canvas, mLrcEntryList.get(mCurrentLine).getStaticLayout(), currY);

        // 画当前行上面的
        mPaint.setColor(mNormalColor);
        float upY = currY;
        for (int i = mCurrentLine - 1; i >= 0; i--) {
            upY -= mDividerHeight + mLrcEntryList.get(i).getTextHeight();
            drawText(canvas, mLrcEntryList.get(i).getStaticLayout(), upY);

            if (upY <= 0) {
                break;
            }
        }

        // 画当前行下面的
        float downY = currY + mLrcEntryList.get(mCurrentLine).getTextHeight() + mDividerHeight;
        for (int i = mCurrentLine + 1; i < mLrcEntryList.size(); i++) {
            drawText(canvas, mLrcEntryList.get(i).getStaticLayout(), downY);

            if (downY + mLrcEntryList.get(i).getTextHeight() >= getHeight()) {
                break;
            }

            downY += mLrcEntryList.get(i).getTextHeight() + mDividerHeight;
        }
    }

    private void drawText(Canvas canvas, StaticLayout staticLayout, float y) {
        canvas.save();
        canvas.translate(mLrcPadding, y);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private float getLrcWidth() {
        return getWidth() - mLrcPadding * 2;
    }

    /**
     * 设置歌词为空时屏幕中央显示的文字，如“无歌词”
     */
    public void setLabel(final String label) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                reset();

                mLabel = label;
                invalidate();
            }
        });
    }

    /**
     * 加载歌词文件
     *
     * @param lrcFile 歌词文件
     */
    public void loadLrc(final File lrcFile) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                reset();

                setFlag(lrcFile);
                new AsyncTask<File, Integer, List<LrcEntry>>() {
                    @Override
                    protected List<LrcEntry> doInBackground(File... params) {
                        return LrcEntry.parseLrc(params[0]);
                    }

                    @Override
                    protected void onPostExecute(List<LrcEntry> lrcEntries) {
                        if (getFlag() == lrcFile) {
                            onLrcLoaded(lrcEntries);
                            setFlag(null);
                        }
                    }
                }.execute(lrcFile);
            }
        });
    }

    /**
     * 加载歌词文件
     *
     * @param lrcText 歌词文本
     */
    public void loadLrc(final String lrcText) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                reset();

                setFlag(lrcText);
                new AsyncTask<String, Integer, List<LrcEntry>>() {
                    @Override
                    protected List<LrcEntry> doInBackground(String... params) {
                        return LrcEntry.parseLrc(params[0]);
                    }

                    @Override
                    protected void onPostExecute(List<LrcEntry> lrcEntries) {
                        if (getFlag() == lrcText) {
                            onLrcLoaded(lrcEntries);
                            setFlag(null);
                        }
                    }
                }.execute(lrcText);
            }
        });
    }

    private void onLrcLoaded(List<LrcEntry> entryList) {
        if (entryList != null && !entryList.isEmpty()) {
            mLrcEntryList.addAll(entryList);
        }

        if (hasLrc()) {
            initEntryList();
            initNextTime();
        }

        invalidate();
    }

    /**
     * 刷新歌词
     *
     * @param time 当前播放时间
     */
    public void updateTime(final long time) {
        mLastTime = time;
        runOnUi(new Runnable() {
            @Override
            public void run() {
                // 避免重复绘制
                if (time < mNextTime) {
                    return;
                }
                for (int i = mCurrentLine; i < mLrcEntryList.size(); i++) {
                    if (mLrcEntryList.get(i).getTime() > time) {
                        mNextTime = mLrcEntryList.get(i).getTime();
                        mCurrentLine = (i < 1) ? 0 : (i - 1);
                        newlineAnimation(i);
                        break;
                    } else if (i == mLrcEntryList.size() - 1) {
                        // 最后一行
                        mCurrentLine = mLrcEntryList.size() - 1;
                        mNextTime = Long.MAX_VALUE;
                        newlineAnimation(i);
                        break;
                    }
                }
            }
        });
    }

    /**
     * 将歌词滚动到指定时间
     *
     * @param time 指定的时间
     */
    public void onDrag(final long time) {
        mLastTime = time;
        runOnUi(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mLrcEntryList.size(); i++) {
                    if (mLrcEntryList.get(i).getTime() > time) {
                        if (i == 0) {
                            mCurrentLine = i;
                            initNextTime();
                        } else {
                            mCurrentLine = i - 1;
                            mNextTime = mLrcEntryList.get(i).getTime();
                        }
                        newlineAnimation(i);
                        break;
                    }
                }
            }
        });
    }

    /**
     * 歌词是否有效
     *
     * @return true，如果歌词有效，否则false
     */
    public boolean hasLrc() {
        return !mLrcEntryList.isEmpty();
    }

    private void reset() {
        mLrcEntryList.clear();
        mCurrentLine = 0;
        mNextTime = 0L;

        stopAnimation();
        invalidate();
    }

    private void initEntryList() {
        if (getWidth() == 0) {
            return;
        }

        //        Collections.sort(mLrcEntryList);

        for (LrcEntry lrcEntry : mLrcEntryList) {
            lrcEntry.init(mPaint, (int) getLrcWidth());
        }
    }

    private void initNextTime() {
        if (mLrcEntryList.size() > 1) {
            mNextTime = mLrcEntryList.get(1).getTime();
        } else {
            mNextTime = Long.MAX_VALUE;
        }
    }

    /**
     * 换行动画<br>
     * 属性动画只能在主线程使用
     */
    private void newlineAnimation(int index) {
        stopAnimation();

        mAnimator = ValueAnimator.ofFloat(mLrcEntryList.get(index).getTextHeight() + mDividerHeight, 0.0f);
        mAnimator.setDuration(mAnimationDuration * mLrcEntryList.get(index).getStaticLayout().getLineCount());
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimateOffset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }

    private void stopAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
    }

    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

    private Object getFlag() {
        return mFlag;
    }

    private void setFlag(Object flag) {
        this.mFlag = flag;
    }
}
