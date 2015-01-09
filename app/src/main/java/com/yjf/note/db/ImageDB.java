package com.yjf.note.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2015/1/2 0002.
 */
public class ImageDB extends DataBaseHelper{

    //数据库列名
    public  static final String KEY_ROWID = "_id";
    public static final String KEY_ADDR = "image_addr";

    private static final String DATABASE_TABLE = "image";

    private static final String TABLE_CREATE = "create table "+DATABASE_TABLE+" ("+KEY_ROWID+" integer primary key autoincrement, "+KEY_ADDR+" text not null);";
    private static final String TABLE_DROP = "drop table if exists "+DATABASE_TABLE;
    public ImageDB(Context ctx){
        super(ctx,TABLE_CREATE,TABLE_DROP);
    }

    public long insert(String imageAddr){
        db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ADDR,imageAddr);
        return db.insert(DATABASE_TABLE,null,initialValues);
    }

    public Cursor queryAllData(){
        db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE,new String[]{KEY_ROWID,KEY_ADDR},null,null,null,null,KEY_ROWID+" asc");
        return cursor;
    }
}
