package com.yjf.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yjf.note.Exception.AppException;
import com.yjf.note.db.LoginDB;
import com.yjf.note.handler.IndexHandler;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/1/2 0002.
 */
public class IndexActivity extends Activity {

    private IndexHandler indexHandler;
    private Intent intent;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        indexHandler = new IndexHandler(this.getApplicationContext());
    }

   @Override
    public void onResume(){
        super.onResume();
        defaultLogin();
    }

    private void defaultLogin(){
        LoginDB db = new LoginDB(this.getApplicationContext());
        username = db.getUsername();
        if(username != null){
            String password = db.getPassword();
            indexHandler.login(handler,username,password);
        }else{
            intent = new Intent(IndexActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //网络请求结束后系统自动回调handlerMessage方法
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch(data.getInt("result")){
                case 1:
                    intent = new Intent(IndexActivity.this,MainActivity.class);
                    intent.putExtra("username",username);
                    break;
                case 0:
                    intent = new Intent(IndexActivity.this,LoginActivity.class);
                    break;
            }
            startActivity(intent);
            finish();
        }
    };

}
