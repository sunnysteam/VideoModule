package com.robot.tuling.app;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.robot.tuling.videomodule.UIControl.WeatherUIControl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private String[] permissions;
    private WeatherUIControl mWeatherUIControl;
    private ViewPager viewPager;
    private static final int GET_TIME = 925;
    private static final int GET_WEATHER = 926;
    private List<Fragment> fragments;
    private TextView timeView;
    private TextView dateView;
    private View pageOneView;
    private View pageTwoView;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_TIME: {
                    long currentTimeMillis = System.currentTimeMillis();
                    Date date = new Date(currentTimeMillis);
                    String time = new SimpleDateFormat("yyyy年M月d日 H:mm:ss", Locale.CHINA).format(date);
                    String[] times = time.split(" ");
                    String date_text = times[0].substring(5) + "  " + getWeek(times[0]);
                    String time_text = times[1].substring(0, times[1].length() - 3);

                    timeView.setText(time_text);
                    dateView.setText(date_text);

                    Message message = obtainMessage();
                    message.what = GET_TIME;
                    sendMessageDelayed(message, 10000);
                    break;
                }
                case GET_WEATHER: {
                    mWeatherUIControl.setCity("深圳");

                    Message message = obtainMessage();
                    message.what = GET_WEATHER;
                    sendMessageDelayed(message, 1000 * 60 * 5);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initView();

        handler.sendMessage(handler.obtainMessage(GET_TIME));
        handler.sendMessage(handler.obtainMessage(GET_WEATHER));
    }

    private void initView() {
        pageOneView = findViewById(R.id.page_one);
        pageOneView.setOnClickListener(this);
        pageTwoView = findViewById(R.id.page_two);
        pageTwoView.setOnClickListener(this);
        ((ViewPager) findViewById(R.id.viewPager)).addOnPageChangeListener(this);
        mWeatherUIControl = ((WeatherUIControl) findViewById(R.id.weather_control));
        mWeatherUIControl.setCity("深圳");

        timeView = (TextView) findViewById(R.id.time_text);
        dateView = ((TextView) findViewById(R.id.date_text));

        fragments = new ArrayList<>();
        fragments.add(Fragment_one.newInstance(""));
        fragments.add(Fragment_two.newInstance(""));

        viewPager = ((ViewPager) findViewById(R.id.viewPager));
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), fragments));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.page_one: {
                viewPager.setCurrentItem(0, true);
                break;
            }
            case R.id.page_two: {
                viewPager.setCurrentItem(1, true);
                break;
            }
        }
    }

    private String getWeek(String pTime) {


        String Week = "星期";


        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Calendar c = Calendar.getInstance();
        try {

            c.setTime(format.parse(pTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "六";
        }
        return Week;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                ((TextView) pageOneView).setTextColor(Color.parseColor("#ffffff"));
                ((TextView) pageTwoView).setTextColor(Color.parseColor("#a4a4a4"));
                break;
            case 1:
                ((TextView) pageOneView).setTextColor(Color.parseColor("#a4a4a4"));
                ((TextView) pageTwoView).setTextColor(Color.parseColor("#ffffff"));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    //权限请求
    private void requestPermission() {
        permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
        };

        int ret = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (ret != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 100);
            }
        }
    }
}
