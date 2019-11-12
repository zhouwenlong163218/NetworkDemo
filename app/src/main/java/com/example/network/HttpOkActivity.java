package com.example.network;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.network.model.Ip;
import com.example.network.model.IpData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpOkActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OkHttpActivity";

    //请求的URL
    private static final String IP_BASE_URL = "http://ip.taobao.com/service/getIpInfo.php";
    private static final String IP_URL = IP_BASE_URL + "?ip=117.136.67.3";
    private static final String UPLOAD_FILE_URL = "https://api.github.com/markdown/raw";
    private static final String DOWNLOAD_URL = "https://github.com/zhayh/AndroidExample/blob/master/README.md";


    // 指定MIME类型
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private TextView ok;
    private ScrollView okView;
    private ImageView okImage;
    private Button btnOkGet,okPost,okUp,okDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok);
        initView();

        // 加载图片
        GlideApp.with(this)
                .load("http://guolin.tech/book.png")
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("OkHttpActivity", "加载失败 errorMsg：" + (e != null ? e.getMessage() : "null"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("OkHttpActivity", "成功  Drawable Name：" + resource.getClass().getCanonicalName());
                        return false;
                    }
                })
                .placeholder(R.mipmap.ic_launcher_round)
                .into(okImage);
    }

    private void initView() {
        ok = findViewById(R.id.tv_ok);
        okView = findViewById(R.id.sv_okView);
        okImage = findViewById(R.id.iv_okImage);
        btnOkGet = findViewById(R.id.btn_okGet);
        okPost = findViewById(R.id.btn_okPost);
        okUp =findViewById(R.id.btn_okUp);
        okDown = findViewById(R.id.btn_okDown);

        btnOkGet.setOnClickListener(this);
        okPost.setOnClickListener(this);
        okUp.setOnClickListener(this);
        okDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String path = getFilesDir().getAbsolutePath();
        switch (v.getId()){
            case R.id.btn_okGet:
                okView.setVisibility(View.VISIBLE);
                okImage.setVisibility(View.GONE);
                get(IP_URL);
                break;
            case R.id.btn_okPost:
                okView.setVisibility(View.VISIBLE);
                okImage.setVisibility(View.GONE);
                Map<String, String> params = new HashMap<>();
                params.put("ip","121,229,217,223");
                post(IP_URL,params);
                break;
            case R.id.btn_okUp:
                okView.setVisibility(View.VISIBLE);
                okImage.setVisibility(View.GONE);
                final String fileName = path + File.separator + "readme.md";
                uploadFile(UPLOAD_FILE_URL,fileName);
                break;
            case R.id.btn_okDown:
                okView.setVisibility(View.VISIBLE);
                okImage.setVisibility(View.GONE);
                downFile(DOWNLOAD_URL, path);
                break;
        }
    }

    //get异步请求是在子线程中执行的，需要切换到主线程更新UI
    private void get(String url){
        //构造Request
        Request request = new Request.Builder().url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36")
                .addHeader("Accept","application/json")
                .get()
                .method("GET",null)
                .build();
        //发送请求，并处理回调
        OkHttpClient client = HttpsUtil.handleSSLHandshakeByOkHttp();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("OKActivity",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    //获取响应主体的字符串
                    String json = response.body().string();
                    //使用FastJson库解析json字符串
                    final Ip ip = JSON.parseObject(json,Ip.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //根据返回的code判断获取是否成功
                            if (ip.getCode() != 0){
                                ok.setText("未获得数据");
                            }else {
                                //解析数据
                                IpData data = ip.getData();
                                ok.setText(ip.getData().getIp() + "," + data);
                            }
                        }
                    });
                }
            }
        });
    }

    //将请求的参数组装成RequestBody
    private RequestBody setRequestBody(Map<String,String> params){
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()){
            builder.add(key,params.get(key));
        }
        return builder.build();
    }

    private void post(String url,Map<String,String> params){
        RequestBody body = setRequestBody(params);
        //构造Request
        Request request = new Request.Builder().url(url).post(body)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36")
                .addHeader("Accept","application/json")
                .build();
        //发送请求，并处理回调
        OkHttpClient client = HttpsUtil.handleSSLHandshakeByOkHttp();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    //获取响应主体的字符串
                    String json = response.body().string();
                    //使用FastJson库解析json字符串
                    final Ip ip = JSON.parseObject(json,Ip.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //根据返回的code判断获取是否成功
                            if (ip.getCode() != 0){
                                ok.setText("未获得数据");
                            }else {
                                //解析数据
                                IpData data = ip.getData();
                                ok.setText(ip.getData().getIp() + "," + data);
                            }
                        }
                    });
                }
            }
        });
    }

    private void uploadImage(String url, final String fileName) {
        // 1. 创建请求主体RequestBody
        RequestBody fileBody = RequestBody.create(new File(fileName), MEDIA_TYPE_PNG);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "头像")
                .addFormDataPart("file", fileName, fileBody)
                .build();

        // 2. 创建请求
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Client-ID 4ff8b2fc6d5f339")
                .header("User-Agent", "NetworkDemo")
                .post(body)
                .build();

        // 3. 创建OkHttpClient对象，发送请求，并处理回调
        OkHttpClient client = HttpsUtil.handleSSLHandshakeByOkHttp();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ok.setText(fileName + "上传失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String str = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ok.setText("上传成功，" + str);
                    }
                });
            }
        });
    }

    private void uploadFile(String url,String fileName){
        Request request = new Request.Builder().url(url)
                .post(RequestBody.create(new File(fileName),MEDIA_TYPE_MARKDOWN))
                .build();
        OkHttpClient client = HttpsUtil.handleSSLHandshakeByOkHttp();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.e(TAG,e.getMessage());
                ok.post(new Runnable() {
                    @Override
                    public void run() {
                        ok.setText("上传失败，" + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String str = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ok.setText("上传成功，" + str);
                        }
                    });
                }else {
                    Log.d(TAG,response.body().string());
                }
            }
        });
    }

    public static void writeFile(InputStream is, String path, String fileName) throws IOException {
        // 1. 根据path创建目录对象，并检查path是否存在，不存在则创建
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 2. 根据path和fileName创建文件对象，如果文件存在则删除
        File file = new File(path, fileName);
        if (file.exists()) {
            file.delete();
        }
        // 3. 创建文件输出流对象，根据输入流创建缓冲输入流对象，
        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        // 4. 以每次1024个字节写入输出流对象
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.flush();
        // 5. 关闭输入流、输出流对象
        fos.close();
        bis.close();
    }

    private void downFile(final String url, final String path) {
        // 1. 创建Requet对象
        Request request = new Request.Builder().url(url).build();
        // 2. 创建OkHttpClient对象，发送请求，并处理回调
        OkHttpClient client = HttpsUtil.handleSSLHandshakeByOkHttp();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 1. 获取下载文件的后缀名
                    String ext = url.substring(url.lastIndexOf(".") + 1);
                    // 2. 根据当前时间创建文件名，避免重名冲突
                    final String fileName = System.currentTimeMillis() + "." + ext;
                    // 3. 获取响应主体的字节流
                    InputStream is = response.body().byteStream();
                    // 4. 将文件写入path目录
                    writeFile(is, path, fileName);
                    // 5. 在界面给出提示信息
                    ok.post(new Runnable() {
                        @Override
                        public void run() {
                            ok.setText(fileName + "下载成功，存放在" + path);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.d(TAG, e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ok.setText("下载失败，" + e.getMessage());
                    }
                });
            }
        });
    }
}

