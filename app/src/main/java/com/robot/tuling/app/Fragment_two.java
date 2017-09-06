package com.robot.tuling.app;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robot.tuling.videomodule.View.EducationActivity;
import com.robot.tuling.videomodule.View.MovieAlbumActivity;
import com.robot.tuling.videomodule.View.MusicActivity;

import static com.robot.tuling.app.R.id.movie_btn;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Fragment_two#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_two extends Fragment implements View.OnClickListener {

    private Context context;
    private boolean isClick = false;
    private View view;

    public Fragment_two() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static Fragment_two newInstance(String content) {
        Fragment_two fragment = new Fragment_two();
        Bundle args = new Bundle();
        args.putString("content", content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment_two, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        initView();
    }

    private void initView() {
        getView().findViewById(R.id.music_btn).setOnClickListener(this);
        getView().findViewById(R.id.movie_btn).setOnClickListener(this);
        getView().findViewById(R.id.record_btn).setOnClickListener(this);
        getView().findViewById(R.id.reminder_btn).setOnClickListener(this);
        getView().findViewById(R.id.wiki_btn).setOnClickListener(this);
        getView().findViewById(R.id.children_educute_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
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
                case R.id.music_btn: {
                    Intent intent = new Intent(context, MusicActivity.class);
                    startActivity(intent);
                    break;
                }
                case movie_btn: {
                    Intent intent = new Intent(context, MovieAlbumActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.children_educute_btn: {
                    Intent intent = new Intent(context, EducationActivity.class);
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
