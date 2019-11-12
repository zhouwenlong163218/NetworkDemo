package com.example.network;

import android.text.TextUtils;

import org.apache.http.NameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {

    public static String get(String urlPath) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            //将url字符串转为URL对象
            URL url = new URL(urlPath);

            //获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();

            //设置连接的相关参数
            connection.setRequestMethod("GET");//默认为GET
            connection.setUseCaches(false);//不使用缓存
            connection.setConnectTimeout(15000);//设置连接超时时间为1.5s
            connection.setReadTimeout(15000);//设置读取超时时间
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36\n");
            connection.setDoInput(true);//设置是否从httpUrlConnection读入,默认true

            //配置https的证书
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(HttpsUtil.getSSLSocketFactory());
            }

            //进行数据的读取，首先判断响应码是否为200
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //获得输入流
                is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //读取数据
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                //关闭资源
                is.close();
                connection.disconnect();
                //返回结果
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //post请求的网络请求方法
    public static String post(String urlPath,List<NameValuePair> params){
        //处理请求参数
        if (params == null || params.size() == 0){
            return get(urlPath);
        }
        try {
            String body = getParamString(params);
            byte[] data = body.getBytes();
            //将url字符串转为URL对象
            URL url = new URL(urlPath);

            //获得HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //设置连接的相关参数
            connection.setRequestMethod("POST");//默认为POST
            connection.setUseCaches(false);//不使用缓存
            connection.setConnectTimeout(15000);//设置连接超时时间
            connection.setReadTimeout(15000);//设置读取超时时间
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36\n");
            connection.setDoInput(true);//设置是否从httpUrlConnection读入,默认true
            connection.setDoOutput(true);

            //配置https的证书
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(HttpsUtil.getSSLSocketFactory());
            }

            //设置请求头属性，写入参数
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",String.valueOf(data.length));
            OutputStream os = connection.getOutputStream();
            os.write(data);
            os.flush();

            //进行数据的读取，首先判断响应码是否为200
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //获得输入流
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //读取数据
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                //关闭资源
                is.close();
                connection.disconnect();
                //返回结果
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void downFile(String strUrl, String path) {
        String fileName = strUrl.substring(strUrl.lastIndexOf("/") + 1);
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 配置https的证书
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(HttpsUtil.getSSLSocketFactory());
            }
            connection.setRequestProperty("Connection", "Keep-Alive");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                writeFile(is, path, fileName);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String BOUNDARY = UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
    ;

    public static String uploadFile(String strUrl, String fileName) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(HttpsUtil.getSSLSocketFactory());
            }
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 设置字符编码
            connection.setRequestProperty("Charset", "UTF-8");
            // 设置请求内容类型
            connection.setRequestProperty("Content-Type", "text/x-markdown;charset=utf-8;boundary=" + BOUNDARY);

            // 设置DataOutputStream，上传请求头信息
            DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
            StringBuilder builder = new StringBuilder();

            // 上传文件信息
            FileInputStream fis = new FileInputStream(fileName);
            int bufferSize = 1024;
            byte[] tmp = new byte[bufferSize];
            int length = -1;
            while ((length = fis.read(tmp)) != -1) {
                ds.write(tmp, 0, length);
            }
            ds.flush();
            fis.close();
            ds.close();

            if (connection.getResponseCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + connection.getResponseCode() + ",message:" + connection.getResponseMessage());
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String temp;
                builder = new StringBuilder();
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((temp = reader.readLine()) != null) {
                    builder.append(temp).append("\n");
                }
                reader.close();
                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 组装请求参数
    private static String getParamString(List<NameValuePair> pairs)
            throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        for (NameValuePair pair : pairs) {
            if (!TextUtils.isEmpty(builder)) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            builder.append("=");
            builder.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return builder.toString();
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

    //组装请求参数
    private static String getParamString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            String key = URLEncoder.encode(param.getKey(), "UTF-8");
            String value = URLEncoder.encode(param.getValue(), "UTF-8");
            builder.append(key).append('=').append(value);
            if (iterator.hasNext()) {
                builder.append('&');
            }
        }
        return builder.toString();
    }

}
