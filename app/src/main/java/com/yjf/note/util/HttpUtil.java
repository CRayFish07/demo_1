package com.yjf.note.util;

import android.util.Log;

import com.yjf.note.Exception.AppException;
import com.yjf.note.Exception.HttpException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/1/3 0003.
 */
public class HttpUtil {

    private static final String UTF8 = "utf-8";
    private static final String POST = "POST";

    public static HttpResult post(String uri,Map<String,String> params, Map<String,String> headers) throws IOException{
        if(uri == null || uri.trim().equals("")){
            throw new IOException("请求地址不能为空");
        }
        if(headers == null){
            headers = new HashMap<String, String>();
        }
        headers.put("content-type", "text/plain");
        headers.put("charset", "utf-8");
        return requestHttp(POST, uri, params, headers);
    }

    public static ContentResult postToContent(String uri,Map<String,String> params, Map<String,String> headers) throws IOException{
        return toContentResult(post(uri,params,headers));
    }

    public static ContentResult getToContent(String uri,Map<String,String> params, Map<String,String> headers) throws IOException{
        return toContentResult(get(uri,params,headers));
    }

    public static HttpResult get(String uri,Map<String,String> params, Map<String,String> headers) throws IOException{
        if(uri == null || uri.trim().equals("")){
            throw new IOException("请求地址不能为空");
        }
        if(headers == null){
            headers = new HashMap<String, String>();
        }
        headers.put("content-type", "text/plain");
        headers.put("charset", "utf-8");
        return requestHttp("GET",uri,params,headers);
    }

    public static ContentResult toContentResult(HttpUtil.HttpResult httpResult){
        if(httpResult.getStatus() != 200) {
            Log.e("IndexHandler", httpResult.getMessage());
            throw new HttpException("请求失败");
        }
        try {
            JSONObject object = new JSONObject(httpResult.getContent());
            ContentResult result = new ContentResult();
            result.setData(object.getString("data"));
            result.setMessage(object.getString("message"));
            result.setStatus(object.getInt("status"));
            return result;
        }catch(JSONException e){
            Log.e("HttpUtil",e.getMessage(),e);
            throw new AppException("服务器返回数据异常");
        }
    }

    private static String makeUrl(String uri,Map<String,String> params){
        StringBuffer sb = new StringBuffer(uri);
        if(params != null && params.size()>0){
            sb.append("?");
            for(Map.Entry<String,String> entry:params.entrySet()){
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                sb.append("&");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    private static HttpResult requestHttp(String method,String uri,Map<String,String> params,Map<String,String> headers) throws IOException{
        HttpURLConnection conn = null;
        try{
            URL url = new URL(makeUrl(uri,params));
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);
            conn.setConnectTimeout(10000);

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.connect();
            int status = conn.getResponseCode();
            String message = conn.getResponseMessage();
            Map<String,List<String>> header = conn.getHeaderFields();
            InputStream in = conn.getErrorStream();
            if (in == null) {
                in = conn.getInputStream();
            }
            String body = null;
            if (in != null) {
                StringBuilder buf = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, UTF8));
                int len = 0;
                for (char[] cbuf = new char[1024]; (len = reader.read(cbuf)) > 0;) {
                    buf.append(cbuf, 0, len);
                }
                body = buf.toString();
            }
            return new HttpResult(status, message, body , header);
        }catch(MalformedURLException e){
             e.printStackTrace();
             throw e;
        }finally {
            if(conn != null){
                 conn.disconnect();
            }
        }
    }

    public static class HttpResult {
        public final int status;
        public final String message;
        public final String content;
        public final Map<String,List<String>> header;

        HttpResult(int status, String message, String body,Map<String,List<String>> header) {
            this.status = status;
            this.message = message;
            this.content = body;
            this.header = header;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getContent() {
            return content;
        }

        public Map<String,List<String>> getHeader() {
            return header;
        }
    }


    public static class ContentResult {
        private  int status;
        private  String message;
        private  String data;

        public void setStatus(int status){
            this.status = status;
        }
        public int getStatus(){
            return this.status;
        }
        public String getMessage(){
            return this.message;
        }
        public void setMessage(String message){
            this.message = message;
        }
        public void setData(String data){
            this.data = data;
        }
        public String getData(){
            return this.data;
        }
    }

}
