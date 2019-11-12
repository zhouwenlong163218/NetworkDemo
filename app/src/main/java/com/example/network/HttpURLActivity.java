package com.example.network;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HttpURLActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String IP_BASE_URL = "http://ip.taobao.com/service/getIpInfo.php";
    private static final String IP_URL = IP_BASE_URL + "?ip=117.136.67.3";
    private static final String UPLOAD_FILE_URL = "https://api.github.com/markdown/raw";
    private static final String DOWNLOAD_URL = "https://github.com/zhayh/AndroidExample/blob/master/README.md";
    private static final String IMAGE_URL = "https://img.alicdn.com/bao/uploaded/i2/100000294640179384/TB24I0xXNQa61Bjy0FhXXaalFXa_!!0-0-travel.jpg";

    private TextView tvResult;
    private ImageView imageView;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private Button btnGet,post,up,down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);

        initView();
        scrollView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        // 加载图片
        Glide.with(this)
                .load(IMAGE_URL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("URLConnectionActivity", "加载失败 errorMsg：" + (e != null ? e.getMessage() : "null"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Log.d("URLConnectionActivity", "成功  Drawable Name：" + resource.getClass().getCanonicalName());
                        return false;
                    }
                })
                .error(R.mipmap.ic_launcher_round)
                .into(imageView);
    }

    private void initView() {
        tvResult = findViewById(R.id.tv_result);
        scrollView = findViewById(R.id.scroll_view);
        imageView = findViewById(R.id.image_view);
        progressBar = findViewById(R.id.progress);
        btnGet = findViewById(R.id.btn_get);
        post = findViewById(R.id.btn_post);
        up =findViewById(R.id.btn_up);
        down = findViewById(R.id.btn_down);

        btnGet.setOnClickListener(this);
        post.setOnClickListener(this);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get:
                scrollView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String result = NetworkUtils.get(IP_URL);
                        if (result != null) {
                            Log.d("MainActivity", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(result);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("请求失败，未获得数据");
                                }
                            });
                        }
                    }
                }).start();
                break;
            case R.id.btn_post:
                scrollView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair("ip", "221.226.155.10"));
                        final String result = NetworkUtils.post(IP_BASE_URL, params);
                        if (result != null) {
                            Log.d("MainActivity", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(result);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("请求失败，未获得数据");
                                }
                            });
                        }
                    }
                }).start();
                break;
            case R.id.btn_up:
                scrollView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                final String fileName = getFilesDir().getAbsolutePath() + File.separator + "readme.md";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String result = NetworkUtils.uploadFile(UPLOAD_FILE_URL, fileName);
                        if (result != null && !TextUtils.isEmpty(result)) {
                            tvResult.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText("上传结果： " + result);
                                }
                            });
                        }

                    }
                }).start();
                break;
            case R.id.btn_down:
                scrollView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkUtils.downFile(DOWNLOAD_URL, getFilesDir().getAbsolutePath());
                    }
                }).start();
                break;
        }
    }
}
