package com.example.network;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.network.model.OilPrice;
import com.example.network.model.OilPriceBody;
import com.example.network.model.OilPriceRes;
import com.example.network.model.WeatherDay;
import com.example.network.model.WeatherToday;
import com.show.api.ShowApiRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ThreeHttpActivity extends AppCompatActivity implements View.OnClickListener {

    // 易源数据的app_id和key
    private static final String APP_ID = "47490";
    private static final String KEY = "c1813441e5a0477cb68618165c4226dc";

    private static final String WEATHER_URL = "http://v.juhe.cn/weather/index";
    private static final String OIL_URL = "http://route.showapi.com/138-46";
    private static final String NEWS_URL = "http://v.juhe.cn/toutiao/index";

    private TextView three;
    private Button weather,oil,news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        initView();
    }

    private void initView() {
        three = findViewById(R.id.tv_three);
        weather = findViewById(R.id.btn_weather);
        oil = findViewById(R.id.btn_oil);
        news = findViewById(R.id.btn_news);

        weather.setOnClickListener(this);
        oil.setOnClickListener(this);
        news.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_weather:
                getWeather("南京");
                break;
            case R.id.btn_oil:
                getOilPrice("江苏");
                break;
            case R.id.btn_news:
                getTopNews();
                break;
        }
    }

    private void getOilPrice(final String province) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = new ShowApiRequest(OIL_URL, APP_ID, KEY)
                        .addTextPara("prov", province)
                        .post();
                three.post(new Runnable() {
                    @Override
                    public void run() {
                        OilPriceRes priceRes = JSON.parseObject(res, OilPriceRes.class);
                        if(priceRes != null && priceRes.getResCode() == 0) {
                            OilPriceBody body = priceRes.getResBody();
                            if(body != null && body.getRetCode() == 0) {
                                List<OilPrice> prices = body.getList();
                                three.setText(prices.get(0).toString());
                            }
                        }

                    }
                });
            }
        }).start();
    }


    // http://v.juhe.cn/weather/index?cityname=南京&key=bac4ac762eeb118fe8752ff1b252a47b
    private void getWeather(String city) {
        String appKey = "bac4ac762eeb118fe8752ff1b252a47b";
        try {
            String url = WEATHER_URL + "?key=" + appKey + "&cityname=" + URLEncoder.encode(city, "utf-8");
            OkHttpClient client = HttpsUtil.handleSSLHandshakeByOkHttp();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("ThreeDataActivity", e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.isSuccessful()) {
                        String json = response.body().string();
                        JSONObject result = JSON.parseObject(json);
                        if("200".equals(result.getString("resultcode"))) {
                            JSONObject obj = result.getJSONObject("result").getJSONObject("today");
                            final WeatherToday today = JSON.parseObject(obj.toJSONString(), WeatherToday.class);
                            if(today != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        three.setText(today.getTemperature());
                                    }
                                });
                            }
                            obj = result.getJSONObject("result").getJSONObject("future");
                            Map<String, WeatherDay> days = getWeatherFuture(obj.toJSONString());
                        } else {
                            Log.d("ThreeDataActivity", result.getString("reason"));
                        }

                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getTopNews() {
        String appKey = "479a00943101b903d3704563ead8769a";
        String url = NEWS_URL + "?key=" + appKey + "&type=top";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("ThreeDataActivity", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String json = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            three.setText(json);
                        }
                    });
                }
            }
        });
    }

    /**
     * 解析聚合数据的未来7天的天气数据
     * @param futureJson future数据的字符串
     * @return 7天天气的map
     */
    private Map<String, WeatherDay> getWeatherFuture(String futureJson) {
        Map<String, WeatherDay> days = new HashMap<>();

        // 1. 获取今天日期的字符串
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // 2. 获取未来7天的天气对象
        for(int i = 0; i < 7; i++) {
            String day = format.format(calendar.getTime());
            String data = JSON.parseObject(futureJson).getJSONObject("day_" + day).toJSONString();
            days.put(day, JSON.parseObject(data, WeatherDay.class));

            // 在现有日期上增加1天
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }
}
