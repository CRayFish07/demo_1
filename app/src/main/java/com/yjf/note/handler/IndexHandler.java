package com.yjf.note.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yjf.note.Exception.AppException;
import com.yjf.note.Exception.HttpException;
import com.yjf.note.db.LoginDB;
import com.yjf.note.util.HttpUtil;
import com.yjf.note.util.HttpUtil.ContentResult;
import com.yjf.note.util.HttpUtil.HttpResult;
import com.yjf.note.util.MD5Util;
import com.yjf.note.util.StatusEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/1/3 0003.
 */
public class IndexHandler {

    private static final String URI = "http://kuaiding.sinaapp.com/";
    private static final String URI_LOGIN = URI + "userInfo/validLogin.do";
    private static final String URI_REGIST = URI + "userInfo/validRegist.do";
    private static final String URI_COUNT = URI +"userInfo/get.do";
    private LoginDB loginDB;

    public IndexHandler(Context ctx){
        loginDB = new LoginDB(ctx);
    }

    public void login(final Handler handler,String username,String password){
        //密码生成的散列值发送到服务端验证，得到session与token
        final String hashPassword = MD5Util.getMd5(password);
        valid(handler,URI_LOGIN,username,password,hashPassword);
    }

    public void regist(final Handler handler,String username,String password){
        valid(handler, URI_REGIST, username, password, password);
    }

    public void count(final Handler handler){
        new Thread(){
            @Override
            public void run(){
                //验证密码，并将获取到的session,username存入数据库，用于之后退出的免登录
                Message msg = new Message();
                Bundle data = new Bundle();
                String cookie = loginDB.getCookie();
                if (cookie != null) {
                    try {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Cookie", cookie);
                        ContentResult content = HttpUtil.getToContent(URI_COUNT, new HashMap<String, String>(), headers);
                        if(content.getStatus() != 200){
                            cookie = reLogin();
                            headers.put("Cookie", cookie);
                            content = HttpUtil.getToContent(URI_COUNT, new HashMap<String, String>(), headers);
                            //存入数据库
                            loginDB.updateCookie(cookie);
                        }
                        JSONObject object = new JSONObject(content.getData().toString());
                        data.putInt("result",1);
                        data.putInt("count", object.getInt("count"));
                    }catch(IOException e){
                        Log.e("IndexHandler",e.getMessage(),e);
                        throw new AppException("app网络请求异常");
                    }catch(JSONException e) {
                        Log.e("IndexHandler",e.getMessage(),e);
                        throw new AppException("服务端返回数据格式异常");
                    }
                    catch(HttpException e){
                        Log.e("IndexHandler",e.getMessage(),e);
                        throw new AppException("app网络请求异常");
                    } catch(AppException e){
                        Log.e("IndexHandler",e.getMessage(),e);
                        data.putInt("result",0);
                        data.putString("message",e.getMessage());
                    }
                }else{
                    data.putInt("result",2);
                }
            msg.setData(data);
            //最后一步，将请求获得的数据回填到handler中
            handler.sendMessage(msg);
            }
        }.start();
    }

    private String reLogin(){
        //请求失败表示cookie过期失效了，需要重新登录获取cookie
        String username  = loginDB.getUsername();
        String password = loginDB.getPassword();
        Map<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("finalPassword",MD5Util.getMd5(password));
        //重新登录
        return httpValid(URI_LOGIN,params);
    }

    //android禁止在主线程使用网络请求，原因是由于网络请求属于耗时操作，
    // 故需要单独启动线程去执行任务，线程执行结束后会自动调用handler的handlerMessage方法
    private void valid(final Handler handler,final String url,String username,String password,String aesPassword){
        final String word = password;
        final String name = username;
        final  Map<String,String> params = new HashMap<String,String>();
        params.put("username", username);
        params.put("finalPassword",aesPassword);
        new Thread(){
            @Override
            public void run(){
                //验证密码，并将获取到的session,username存入数据库，用于之后退出的免登录
                Message msg = new Message();
                Bundle data = new Bundle();
                try{
                    String cookie = httpValid(url,params);
                    //存入数据库
                    loginDB.updateCookie(cookie);
                    loginDB.updateUsername(name);
                    loginDB.updatePassword(word);
                    data.putInt("result", 1);
                }catch(AppException e){
                    Log.e("IndexHandler",e.getMessage(),e);
                    data.putInt("result",0);
                    data.putString("message",e.getMessage());
                }
                msg.setData(data);
                //最后一步，将请求获得的数据回填到handler中
                handler.sendMessage(msg);
            }
        }.start();
    }

    private String httpValid(String url,Map<String,String> params){
        try {
            HttpResult result = HttpUtil.get(url, params, null);
            ContentResult content = HttpUtil.toContentResult(result);
            if(content.getStatus() != 200){
                throw new AppException(content.getMessage());
            }
            String cookie = getCookie(result.getHeader());
            return cookie;
        }catch(IOException e){
            Log.e("IndexHandler",e.getMessage(),e);
            throw new AppException("app网络请求异常");
        }
    }

    private String getCookie(Map<String,List<String>> headers){
        List<String> cookies = headers.get("Set-Cookie");
        StringBuilder sb = new StringBuilder();
        for(String s:cookies){
            sb.append(s).append(";");
        }
        if(sb.length()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

}
