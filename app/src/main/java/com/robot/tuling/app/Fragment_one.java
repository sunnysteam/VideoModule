package com.robot.tuling.app;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robot.tuling.videomodule.View.CameraActivity;
import com.robot.tuling.videomodule.View.VideoActivity;


public class Fragment_one extends Fragment implements View.OnClickListener {

    private String content;
    private Context context;
    private View view;
    private boolean isClick = false;

    public Fragment_one() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static Fragment_one newInstance(String content) {

        Fragment_one fragment = new Fragment_one();
        Bundle args = new Bundle();
        args.putString("content", content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment_one, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        initView();
    }

    private void initView() {
        getView().findViewById(R.id.remote_vedio_btn).setOnClickListener(this);
        getView().findViewById(R.id.commuicate_btn).setOnClickListener(this);
        getView().findViewById(R.id.family_btn).setOnClickListener(this);
        getView().findViewById(R.id.home_control_btn).setOnClickListener(this);
        getView().findViewById(R.id.navi_btn).setOnClickListener(this);
        getView().findViewById(R.id.remote_audio_btn).setOnClickListener(this);
        getView().findViewById(R.id.photo_btn).setOnClickListener(this);
        getView().findViewById(R.id.vedio_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        if (!isClick) {
            isClick = true;
            view = v;
            AnimationUtil.alaphAnimation(v, listener);
        }
    }


    Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (view.getId()) {
                case R.id.photo_btn: {
                    Intent intent = new Intent(context, CameraActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.vedio_btn: {
                    Intent intent = new Intent(context, VideoActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            isClick = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
}
