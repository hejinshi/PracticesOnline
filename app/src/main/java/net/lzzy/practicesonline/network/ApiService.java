package net.lzzy.practicesonline.network;

import android.text.TextUtils;

import net.lzzy.practicesonline.utils.StreamTool;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lzzy_gxy on 2019/4/19.
 * Description:
 */
public class ApiService {
    private static final OkHttpClient CLIENT=new OkHttpClient();

    public static String get(String address)throws IOException {
        URL url=new URL(address);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        try{
            connection.setConnectTimeout(6*1000);
            connection.setRequestMethod("GET");
            connection.setReadTimeout(6*1000);
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder=new StringBuilder();
            String line;
            while ((line=reader.readLine())!=null){
                builder.append(line).append("\n");
            }
            reader.close();
            return builder.toString();
        }finally {
            connection.disconnect();
        }


    }

    public static void post(String address, JSONObject json) throws IOException{
        URL url=new URL(address);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        connection.setRequestProperty("Content-type","application/json");
        byte[] data=json.toString().getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length",String.valueOf(data.length));
        connection.setUseCaches(false);
        try (OutputStream stream=connection.getOutputStream()){
            //try括号里的资源在try{...}内生效，try方法块结束时会自动释放该资源
            stream.write(data);
            stream.flush();




        }finally {
            connection.disconnect();
        }
//region history
//        String msg = "";
//        try{
//            HttpURLConnection conn = (HttpURLConnection) new URL(address).openConnection();
//            //设置请求方式,请求超时信息
//            conn.setRequestMethod("POST");
//            conn.setReadTimeout(5000);
//            conn.setConnectTimeout(5000);
//            //设置运行输入,输出:
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            //Post方式不能缓存,需手动设置为false
//            conn.setUseCaches(false);
//            //我们请求的数据:
//            String data = "passwd="+ URLEncoder.encode(json.getString(""), "UTF-8")+
//                    "&number="+ URLEncoder.encode(json.getString(""), "UTF-8");
//            //这里可以写一些请求头的东东...
//            //获取输出流
//            OutputStream out = conn.getOutputStream();
//            out.write(data.getBytes());
//            out.flush();
//            if (conn.getResponseCode() == 200) {
//                // 获取响应的输入流对象
//                InputStream is = conn.getInputStream();
//                // 创建字节输出流对象
//                ByteArrayOutputStream message = new ByteArrayOutputStream();
//                // 定义读取的长度
//                int len = 0;
//                // 定义缓冲区
//                byte buffer[] = new byte[1024];
//                // 按照缓冲区的大小，循环读取
//                while ((len = is.read(buffer)) != -1) {
//                    // 根据读取的长度写入到os对象中
//                    message.write(buffer, 0, len);
//                }
//                // 释放资源
//                is.close();
//                message.close();
//                // 返回字符串
//                msg = new String(message.toByteArray());
//                //return msg;
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        //return msg;
//endregion
    }

    public static String okGet(String address)throws IOException{
        Request request=new Request.Builder().url(address).build();
        try(Response response=CLIENT.newCall(request).execute()){
            if (response.isSuccessful()){
                return response.body().string();
            }else {
                throw new IOException("错误码："+response.code());
            }
        }

    }

    public static String okGet(String address, String args, HashMap<String,Object> headers)throws IOException{
        if (!TextUtils.isEmpty(args)){
            address=address.concat("?").concat(args);
        }
        Request.Builder builder=new Request.Builder().url(address);
        if (headers!=null&&headers.size()>0){
            for (Object o:headers.entrySet()){
                Map.Entry entry=(Map.Entry) o;
                String key=entry.getKey().toString();
                Object val=entry.getValue();
                if (val instanceof String){
                    builder=builder.header(key,val.toString());
                }else if(val instanceof List){
                    for (String v:ApiService.<List<String>>cast(val)){
                        builder=builder.addHeader(key,v);
                    }
                }
            }
        }
        Request request=new Request.Builder().url(address).build();
        try(Response response=CLIENT.newCall(request).execute()){
            if (response.isSuccessful()){
                return response.body().string();
            }else {
                throw new IOException("错误码："+response.code());
            }
        }

    }
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object){
        return (T) object;
    }
    public static int okPost(String address,JSONObject json) throws IOException {
        RequestBody body=RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                ,json.toString());
        Request request=new Request.Builder()
                .url(address)
                .post(body)
                .build();
        try(Response response=CLIENT.newCall(request).execute()){
            return response.code();
        }

    }

    public static String okRequest(String address,JSONObject json) throws IOException {
        RequestBody body=RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                ,json.toString());
        Request request=new Request.Builder()
                .url(address)
                .post(body)
                .build();
        try(Response response=CLIENT.newCall(request).execute()){
            return response.body().string();
        }
    }

}
