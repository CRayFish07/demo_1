package com.yif.note.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/1/2 0002.
 */
public class ImageDB {

    //数据库列名
    public  static final String KEY_ROWID = "_id";
    public static final String KEY_ADDR = "image_addr";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "image";

    private static final String TABLE_CREATE = "create table "+DATABASE_TABLE+" ("+KEY_ROWID+" integer primary key autoincrement, "+KEY_ADDR+" text not null);";
    private static final String TABLE_DROP = "drop table "+DATABASE_TABLE;

    private Context context;
    private DataBaseHelper DBHelper;
    private SQLiteDatabase db;

    public ImageDB(Context ctx){
        this.context = ctx;
        DBHelper = new DataBaseHelper(context,TABLE_CREATE,TABLE_DROP,DATABASE_VERSION);

    }

    public long insert(String imageAddr){
        db = DBHelper.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ADDR,imageAddr);
        return db.insert(DATABASE_TABLE,null,initialValues);
    }

    public Cursor queryAllData(){
        db = DBHelper.getReadableDatabase();
       return db.query(DATABASE_TABLE,new String[]{KEY_ROWID,KEY_ADDR},null,null,null,null,KEY_ROWID+" asc");
    }
}
