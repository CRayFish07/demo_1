package com.yjf.note.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/1/2 0002.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "note";
    private static  int DATABASE_VERSION = 2;
    private String DATABASE_CREATE;
    private String TABLE_DROP;
    public SQLiteDatabase db;

    public DataBaseHelper(Context context,String database_create_sql,String table_drop_sql){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.DATABASE_CREATE = database_create_sql;
        this.TABLE_DROP = table_drop_sql;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        if(i2 > i) {
            sqLiteDatabase.execSQL(TABLE_DROP);
            onCreate(sqLiteDatabase);
        }
    }
}
