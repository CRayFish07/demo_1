package com.yjf.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yjf.note.db.LoginDB;
import com.yjf.note.handler.IndexHandler;

/**
 * Created by Administrator on 2015/1/8 0008.
 */
public class LoginActivity  extends Activity implements View.OnClickListener{

    private EditText username;
    private EditText password;
    private String nameValue;
    private String passwordValue;
    private IndexHandler indexHandler;
    private LoginDB loginDB;

    private Intent intent;

    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        intent = new Intent(LoginActivity.this,MainActivity.class);
        indexHandler = new IndexHandler(this.getApplicationContext());
        loginDB = new LoginDB(this.getApplicationContext());
        nameValue = loginDB.getUsername();

        Button login = (Button)findViewById(R.id.login);
        Button regist = (Button)findViewById(R.id.regist);
        login.setOnClickListener(this);
        regist.setOnClickListener(this);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        username.setText(nameValue);

    }

    @Override
    public void onClick(View view) {
        if(!getNameAndPassword()){
            return;
        }
        switch(view.getId()){
            case R.id.login:
                indexHandler.login(handler,nameValue,passwordValue);
                break;
            case R.id.regist:
                indexHandler.regist(handler, nameValue, passwordValue);
                break;
            default: return;
        }
    }

    private boolean getNameAndPassword(){
        nameValue = getEditTextContent(username,"用户名必须输入");
        if(nameValue == null){
            return false;
        }
        passwordValue = getEditTextContent(password,"密码必须输入");
        if(passwordValue == null){
            return false;
        }
        return true;
    }

    private String getEditTextContent(EditText editText,String tips){
        String value = editText.getText().toString().trim();
        if(value == null || value.equals("")){
            Toast.makeText(this.getApplicationContext(),tips,Toast.LENGTH_SHORT).show();
            return null;
        }
        return value;
    }

    //网络请求结束后系统自动回调handlerMessage方法
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch(data.getInt("result")){
                case 1:
                    intent.putExtra("username",nameValue);
                    startActivity(intent);
                    //直接销毁本activity
                    finish();
                    break;
                case 0:
                    Toast.makeText(LoginActivity.this.getApplicationContext(), data.getString("message"), Toast.LENGTH_SHORT).show();break;
            }
        }
    };
}
