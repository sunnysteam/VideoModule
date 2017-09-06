package com.robot.tuling.app;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by sunnysteam on 2017/7/13.
 */

public class AnimationUtil {
    private static final float MIN_SCALE = 1.2f;

    public static void candy(final View view, Animator.AnimatorListener listener){
        float scaleX = view.getScaleX();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", scaleX, scaleX / MIN_SCALE, scaleX).setDuration(300);
        animator.addListener(listener);
        animator.start();
        ObjectAnimator.ofFloat(view,"scaleY",scaleX,scaleX/MIN_SCALE,scaleX).setDuration(300).start();
    }

    public static void alaphAnimation(View view, Animator.AnimatorListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"alpha",1,0.8f,0.4f,0.8f,1).setDuration(500);
        animator.addListener(listener);
        animator.start();
    }
}
