package com.yjf.note.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/1/3 0003.
 */
public class LoginDB extends DataBaseHelper{

    //数据库列名
    public  static final String KEY_ROWID = "_id";
    public static final String KEY_PROP_KEY = "prop_key";
    public static final String KEY_PROP_VALUE = "prop_value";

    private static final String COOKIE = "cookie";
    private static final String USERNAME = "username";
    private static final String TOKEN = "token";
    private static final String PASSWORD = "password";

    private static final String DATABASE_TABLE = "login";

    private static String TABLE_CREATE = "create table "+DATABASE_TABLE+" ("+KEY_ROWID+" integer primary key autoincrement, "+KEY_PROP_KEY+" text not null, "+KEY_PROP_VALUE+" text not null);" ;
    private static String TABLE_DROP = "drop table if exists "+DATABASE_TABLE;

    public LoginDB(Context ctx){
        super(ctx,TABLE_CREATE,TABLE_DROP);
    }

    public void updateCookie(String value){
        update(COOKIE,value);
    }

    public void updateUsername(String value){
        update(USERNAME,value);
    }

    public void updateToken(String value){
        update(TOKEN,value);
    }

    public void updatePassword(String password){
        update(PASSWORD,password);
    }

    private void update(String key,String value){
        db = this.getWritableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_PROP_VALUE}, KEY_PROP_KEY + "=?", new String[]{key}, null, null, null);
        if(cursor.getCount() == 0){
            //游标不使用的时候要close，不然会出现异常
           cursor.close();
           db.execSQL("insert into login(prop_key,prop_value) values('"+key+"','"+value+"');");
        }
        else{
             ContentValues initValues = new ContentValues();
             initValues.put(KEY_PROP_VALUE,value);
             db.update(DATABASE_TABLE,initValues,KEY_PROP_KEY+"=?",new String[]{key});
        }
    }

    private String query(String key){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_PROP_VALUE}, KEY_PROP_KEY + "=?", new String[]{key}, null, null, null);
        if(cursor.getCount() == 0){
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(KEY_PROP_VALUE);
        String result = cursor.getString(index);
        cursor.close();
        return result;
    }

    public String getCookie(){
           return query(COOKIE);
    }

    public String getUsername(){
        return query(USERNAME);
    }

    public String getToken(){
        return query(TOKEN);
    }

    public String getPassword(){
        return query(PASSWORD);
    }

}
