package com.robot.tuling.videomodule.UIControl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.robot.common.lib.net.HttpUtil;
import com.robot.tuling.videomodule.Model.WeatherModel;
import com.robot.tuling.videomodule.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sunnysteam on 2017/9/4.
 */

public class WeatherUIControl extends LinearLayout {


    private final ImageView mWeatherImage;
    private final TextView mCityNameTextView;
    private final TextView mDegreeFormatTextView;
    private final TextView mWeatherTextView;
    private final TextView mDegressTextView;

    private String lastCityName;
    private static final int GET_WEATHER = 988;
    private String key = "uvqkmy75frap92cs";
    private String degreeFormat = "c";
    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_WEATHER:
                    setCity(lastCityName);

                    Message message = obtainMessage();
                    message.what = GET_WEATHER;
                    sendMessageDelayed(message, 1000 * 60 * 5);
                    break;
            }
        }
    };

    public WeatherUIControl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.control_weather, this, true);
        mWeatherImage = ((ImageView) findViewById(R.id.img_weather));
        mCityNameTextView = ((TextView) findViewById(R.id.tv_city_name));
        mDegreeFormatTextView = ((TextView) findViewById(R.id.tv_degree_format));
        mWeatherTextView = ((TextView) findViewById(R.id.tv_weather_text));
        mDegressTextView = ((TextView) findViewById(R.id.tv_degree));

        handler.sendMessage(handler.obtainMessage(GET_WEATHER));
    }

    public void setCity(String city) {
        if(TextUtils.isEmpty(city)){
            return;
        }
        if (TextUtils.isEmpty(lastCityName) || !lastCityName.equals(city)) {
            String[] temp = city.split("市");
            temp = temp[0].split("特");
            city = temp[0];
            lastCityName = city;

            String now = "https://api.thinkpage.cn/v3/weather/now.json?key=%s&location=%s&language=zh-Hans&unit=%s";//实时天气
            try {
                city = URLEncoder.encode(city, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            now = String.format(now, key, city, degreeFormat);
            final String finalNow = now;
            HttpUtil.DownloadListener listener = new HttpUtil.DownloadListener() {
                @Override
                public void downSuccess(String url, String json) {
                    if (finalNow.equals(url)) {
                        WeatherModel weather = new Gson().fromJson(json, WeatherModel.class);
                        mCityNameTextView.setText(weather.getResults().get(0).getLocation().getName());
                        mWeatherTextView.setText(weather.getResults().get(0).getNow().getText());
                        mDegressTextView.setText(weather.getResults().get(0).getNow().getTemperature());
                        String imageName = String.format("weather_%d", Integer.valueOf(weather.getResults().get(0).getNow().getCode()));
                        mWeatherImage.setImageResource(getResource(imageName));
                    }
                }

                @Override
                public void downFail(String url) {
                    mDegressTextView.setText("- -");
                    mWeatherImage.setImageResource(R.drawable.weather_99);
                }
            };
            HttpUtil.downJson(now, listener);
        }
    }

    public int getResource(String imageName) {
        return getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
}
