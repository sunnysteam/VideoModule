package com.robot.common.lib.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.robot.common.lib.CommonKit;
import com.robot.common.lib.R;


/**
 * Created by zwl95
 * on 2017/3/10.
 */
public class ObjectAnimationUtil {

    private static final int DEFAULT_TIME = 300;

    /**
     * 收缩动画
     *
     * @param view     目标view
     * @param duration 时间
     * @param values   缩放倍数
     */
    public static void shrinkAnimation(View view, int duration, float... values) {
        ObjectAnimator.ofFloat(view, "scaleX", values).setDuration(duration == 0 ? DEFAULT_TIME : duration).start();
        ObjectAnimator.ofFloat(view, "scaleY", values).setDuration(duration == 0 ? DEFAULT_TIME : duration).start();
    }

    /**
     * 降低View透明度动画
     *
     * @param view
     * @param duration
     * @param values
     */
    public static void alphaAnimation(View view, int duration, float... values) {
        ObjectAnimator.ofFloat(view, "alpha", values).setDuration(duration == 0 ? DEFAULT_TIME : duration).start();
    }

    /**
     * 显示隐藏两个view 的动画
     *
     * @param showView
     * @param hideView
     */
    public static void animShowAndHide(final View showView, View hideView) {
        Context applicationContext = CommonKit.getContext().getApplicationContext();
        Animation animationOut = AnimationUtils.loadAnimation(applicationContext, R.anim.gf_flip_horizontal_out);
        long duration = animationOut.getDuration();
        final Animation animationIn = AnimationUtils.loadAnimation(applicationContext, R.anim.gf_flip_horizontal_in);
        hideView.setVisibility(View.GONE);
        hideView.setAnimation(animationOut);
        showView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showView.setAnimation(animationIn);
                showView.setVisibility(View.VISIBLE);
            }
        }, duration);
    }

    public static void animShowAndHideBottom(View showView, int visiablity) {
        Context applicationContext = CommonKit.getContext().getApplicationContext();
        Animation animation = null;
        if (visiablity == View.VISIBLE) {
            animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_in_bottom);
        } else {
            animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_bottom);
        }
        showView.setAnimation(animation);
        showView.setVisibility(visiablity);
    }

    /**
     * 像QQ糖一样的弹性动画
     */
    public static void animQQCandy(View view) {
        ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1, 0.9f, 1).setDuration(300).start();//弹性动画
        ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1, 0.9f, 1).setDuration(300).start();
    }

    /**
     * 一闪一闪动画
     *
     * @param view
     * @param duration
     */
    public static void flashingTweenAnim(View view, long duration) {
        Animation ani = new AlphaAnimation(0f, 1f);
        ani.setDuration(duration); // 每一个循环动画执行的时间
        ani.setRepeatMode(Animation.REVERSE);
        ani.setRepeatCount(Animation.INFINITE);
        view.startAnimation(ani);
    }

    /**
     * 震动动画
     */
    public static void shakeView(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 1, 0, 5);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(100);
        animation.setRepeatCount(5);
        view.startAnimation(animation);
    }

    public static void aroundCenter(final View view, final Around type, final int duration, final int repeatCount, final Handler handler) {
        final ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "translationX", 0, 10).setDuration(duration);
        final ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "translationY", 0, 10).setDuration(duration);
        final ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "translationX", 0, -10).setDuration(duration);
        final ObjectAnimator animator4 = ObjectAnimator.ofFloat(view, "translationY", 0, -10).setDuration(duration);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (type == Around.Clockwise) {
                    animator2.start();
                } else {
                    animator3.start();
                }
            }
        }, duration);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (type == Around.Clockwise) {
                    animator3.start();
                } else {
                    animator2.start();
                }
            }
        }, duration * 2);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (type == Around.Clockwise) {
                    animator4.start();
                } else {
                    animator1.start();
                }
                if (repeatCount > 0) {
                    aroundCenter(view, type, duration, repeatCount - 1, handler);
                }
            }
        }, duration * 3);
        if (type == Around.Clockwise) {
            animator1.start();
        } else {
            animator4.start();
        }
    }

    public static void rotate(View view, long duration) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f).setDuration(duration);
        rotate.setRepeatCount(-1);
        rotate.setRepeatMode(ValueAnimator.RESTART);
        rotate.start();
    }

    public enum Around {
        Clockwise,          // 顺时针
        Counterclockwise    // 逆时针
    }

}
