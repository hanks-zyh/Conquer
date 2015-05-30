/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.hanks.com.conquer.bean.Task;

/**
 * Created by Hanks on 2015/5/30.
 */
public class TaskDao {

    private String[] columns = new String[] {
            DBConstants.TaskColum.COLUMN_NAME_ID,
            DBConstants.TaskColum.COLUMN_NAME_NAME,
            DBConstants.TaskColum.COLUMN_NAME_TIME,
            DBConstants.TaskColum.COLUMN_NAME_NOTE,
            DBConstants.TaskColum.COLUMN_NAME_TAGID,
            DBConstants.TaskColum.COLUMN_NAME_AUDIOURL,
            DBConstants.TaskColum.COLUMN_NAME_IMAGEURL,
            DBConstants.TaskColum.COLUMN_NAME_HASALERTED,
            DBConstants.TaskColum.COLUMN_NAME_NEEDALERT,
            DBConstants.TaskColum.COLUMN_NAME_REPEAT,
            DBConstants.TaskColum.COLUMN_NAME_ATFRIENDS
    };

    // Database fields
    private SQLiteDatabase database;
    private DBHelper       dbHelper;


    public TaskDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * 创建成功，返回记录的ID
     *
     * @param task
     * @return
     */
    public long create(Task task) {

        open();

        ContentValues values = new ContentValues();
        values.put(DBConstants.TaskColum.COLUMN_NAME_NAME, task.getName());
        values.put(DBConstants.TaskColum.COLUMN_NAME_NEEDALERT, task.isNeedAlerted());
        values.put(DBConstants.TaskColum.COLUMN_NAME_HASALERTED, task.isHasAlerted());
        values.put(DBConstants.TaskColum.COLUMN_NAME_TIME, task.getTime());
        if (task.getAudioUrl() != null) {
            values.put(DBConstants.TaskColum.COLUMN_NAME_AUDIOURL, task.getAudioUrl());
        }
        if (task.getImageUrl() != null) {
            values.put(DBConstants.TaskColum.COLUMN_NAME_IMAGEURL, task.getImageUrl());
        }
        if (task.getNote() != null) {
            values.put(DBConstants.TaskColum.COLUMN_NAME_NOTE, task.getNote());
        }
        values.put(DBConstants.TaskColum.COLUMN_NAME_REPEAT, task.getRepeat());
        if (task.getTag() != null) {
            values.put(DBConstants.TaskColum.COLUMN_NAME_TAGID, task.getTag().getId());
        }
        if (task.getAtFriends() != null) {
            StringBuilder atFriends = new StringBuilder();
            for (String at : task.getAtFriends()) {
                atFriends.append(at);
                atFriends.append(",");
            }
            if (atFriends.toString().endsWith(",")) {
                atFriends.deleteCharAt(atFriends.length() - 1);
            }
            values.put(DBConstants.TaskColum.COLUMN_NAME_ATFRIENDS, atFriends.toString());
        }
        long id = database.insert(DBConstants.TaskColum.TABLE_NAME, null, values);
        close();
        return id;
    }


    /**
     * 删除
     *
     * @param task
     * @return 是否删除成功
     */
    public boolean deleteTask(Task task) {
        open();
        long id = task.getId();
        int affectedRows = database.delete(DBConstants.TaskColum.TABLE_NAME, DBConstants.TaskColum.COLUMN_NAME_ID
                + " = " + id, null);
        close();
        return affectedRows > 0;
    }

    /**
     * 获取所有task
     *
     * @return
     */
    public List<Task> getAllTask() {
        open();
        List<Task> taskList = new ArrayList<Task>();
        Cursor cursor = database.query(DBConstants.TaskColum.TABLE_NAME,
                columns, null, null, null, null, DBConstants.TaskColum.COLUMN_NAME_TIME);
        while (cursor.moveToNext()) {
            Task task = cursorToTask(cursor);
            taskList.add(task);
        }
        // make sure to close the cursor
        cursor.close();
        close();
        return taskList;
    }


    /**
     * 获取单个Task
     *
     * @param id
     * @return
     */
    public Task getTask(int id) {
        open();
        Task task = null;
        String sql = "SELECT * FROM " + DBConstants.TaskColum.TABLE_NAME + " WHERE id = (?)";
        Cursor cursor = database.rawQuery(sql, new String[] { id + "" });
        if (cursor.moveToNext()) {
            task = cursorToTask(cursor);
        }
        cursor.close();
        close();
        return task;
    }

    public void update(Task task) {
        open();
        ContentValues values = new ContentValues();
        values.put(DBConstants.TaskColum.COLUMN_NAME_NAME, task.getName());
        values.put(DBConstants.TaskColum.COLUMN_NAME_NEEDALERT, task.isNeedAlerted());
        values.put(DBConstants.TaskColum.COLUMN_NAME_HASALERTED, task.isHasAlerted());
        values.put(DBConstants.TaskColum.COLUMN_NAME_TIME, task.getTime());
        values.put(DBConstants.TaskColum.COLUMN_NAME_AUDIOURL, task.getAudioUrl());
        values.put(DBConstants.TaskColum.COLUMN_NAME_IMAGEURL, task.getImageUrl());
        values.put(DBConstants.TaskColum.COLUMN_NAME_NOTE, task.getNote());
        values.put(DBConstants.TaskColum.COLUMN_NAME_REPEAT, task.getRepeat());
        values.put(DBConstants.TaskColum.COLUMN_NAME_TAGID, task.getTag().getId());
        values.put(DBConstants.TaskColum.COLUMN_NAME_NOTE, task.getNote());

        StringBuilder atFriends = new StringBuilder();
        for (String at : task.getAtFriends()) {
            atFriends.append(at);
            atFriends.append(",");
        }
        if (atFriends.toString().endsWith(",")) {
            atFriends.deleteCharAt(atFriends.length() - 1);
        }
        values.put(DBConstants.TaskColum.COLUMN_NAME_ATFRIENDS, atFriends.toString());
        database.update(DBConstants.TaskColum.TABLE_NAME, values, "id = ?", new String[] { task.getId() + "" });
        close();
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getInt(0));
        task.setName(cursor.getString(1));
        task.setTime(cursor.getLong(2));
        task.setNote(cursor.getString(3));
//        task.setTag(cursor.getString(3));
        task.setAudioUrl(cursor.getString(5));
        task.setImageUrl(cursor.getString(6));
        task.setHasAlerted(cursor.getInt(7) > 0);
        task.setNeedAlerted(cursor.getInt(8) > 0);
        task.setRepeat(cursor.getInt(9));
        List<String> atFriends = Arrays.asList(cursor.getString(10).split(","));
        task.setAtFriends(atFriends);
        return task;
    }

    /**
     * 获取没有提醒过的task
     *
     * @return
     */
    public List<Task> getNotAlertTasks() {
        open();
        List<Task> taskList = new ArrayList<Task>();
        Cursor cursor = database.query(DBConstants.TaskColum.TABLE_NAME,
                columns, DBConstants.TaskColum.COLUMN_NAME_HASALERTED + " = ?", new String[] { "false" }, null, null, DBConstants.TaskColum.COLUMN_NAME_TIME);
        while (cursor.moveToNext()) {
            Task task = cursorToTask(cursor);
            taskList.add(task);
        }
        // make sure to close the cursor
        cursor.close();
        close();
        return taskList;
    }
}
