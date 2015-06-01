/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作
 * Created by Hanks on 2015/5/30.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int    DATABASE_VERSION = 1;
    public static final String DATABASE_NAME    = "Conquer.db";

    private static final String TEXT_TYPE       = " TEXT";
    private static final String INTEGER_TYPE    = " INTEGER";
    private static final String COMMA_SEP       = ",";
    private static final String SQL_CREATE_TASK =
            "CREATE TABLE " + DBConstants.TaskColum.TABLE_NAME + " (" +
                    DBConstants.TaskColum.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    DBConstants.TaskColum.COLUMN_NAME_OBJID + TEXT_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_TIME + INTEGER_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_TAGID + INTEGER_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_REPEAT + INTEGER_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_NEEDALERT + INTEGER_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_NOTE + TEXT_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_IMAGEURL + TEXT_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_AUDIOURL + TEXT_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_ATFRIENDS + TEXT_TYPE + COMMA_SEP +
                    DBConstants.TaskColum.COLUMN_NAME_HASALERTED + INTEGER_TYPE +
                    " )";

    private static final String SQL_CREATE_TAG =
            "CREATE TABLE " + DBConstants.TagColum.TABLE_NAME + " (" +
                    DBConstants.TagColum.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    DBConstants.TagColum.COLUMN_NAME_NAME + TEXT_TYPE +
                    " )";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //创建数据库后，对数据库的操作
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASK);
        db.execSQL(SQL_CREATE_TAG);
    }

    //数据库升级
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
