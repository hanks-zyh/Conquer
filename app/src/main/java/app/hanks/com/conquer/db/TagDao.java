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
import java.util.List;

import app.hanks.com.conquer.bean.Tag;

/**
 * Created by Hanks on 2015/5/30.
 */
public class TagDao {
    private String[] columns = new String[] {
            DBConstants.TagColum.COLUMN_NAME_ID,
            DBConstants.TagColum.COLUMN_NAME_NAME,
    };

    // Database fields
    private SQLiteDatabase database;
    private DBHelper       dbHelper;

    public TagDao(Context context) {
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
     * @param Tag
     * @return
     */
    public long create(Tag Tag) {
        open();
        ContentValues values = new ContentValues();
        values.put(DBConstants.TagColum.COLUMN_NAME_NAME, Tag.getName());
        long id = database.insert(DBConstants.TagColum.TABLE_NAME, null, values);
        close();
        return id;
    }


    /**
     * 删除
     *
     * @param Tag
     * @return 是否删除成功
     */
    public boolean deleteTag(Tag Tag) {
        open();
        long id = Tag.getId();
        int affectedRows = database.delete(DBConstants.TagColum.TABLE_NAME, DBConstants.TagColum.COLUMN_NAME_ID
                + " = " + id, null);
        close();
        return affectedRows > 0;
    }

    /**
     * 获取所有Tag
     *
     * @return
     */
    public List<Tag> getAllTag() {
        open();
        List<Tag> tagList = new ArrayList<>();
        Cursor cursor = database.query(DBConstants.TagColum.TABLE_NAME,
                columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Tag Tag = cursorToTag(cursor);
            tagList.add(Tag);
        }
        // make sure to close the cursor
        cursor.close();
        close();
        return tagList;
    }


    /**
     * 获取单个Tag
     *
     * @param id
     * @return
     */
    public Tag getTag(int id) {
        open();
        Tag Tag = null;
        String sql = "SELECT * FROM " + DBConstants.TagColum.TABLE_NAME + " WHERE " + DBConstants.TagColum.COLUMN_NAME_ID + " = (?)";
        Cursor cursor = database.rawQuery(sql, new String[] { id + "" });
        if (cursor.moveToNext()) {
            Tag = cursorToTag(cursor);
        }
        cursor.close();
        close();
        return Tag;
    }

    public void update(Tag Tag) {
        open();
        ContentValues values = new ContentValues();
        values.put(DBConstants.TagColum.COLUMN_NAME_NAME, Tag.getName());
        database.update(DBConstants.TagColum.TABLE_NAME, values, "id = ?", new String[] { Tag.getId() + "" });
        close();
    }

    private Tag cursorToTag(Cursor cursor) {
        Tag Tag = new Tag();
        Tag.setId(cursor.getInt(0));
        Tag.setName(cursor.getString(1));
        return Tag;
    }

    public Tag getTagByName(String name) {
        open();
        Tag Tag = null;
        String sql = "SELECT * FROM " + DBConstants.TagColum.TABLE_NAME + " WHERE  = " + DBConstants.TagColum.COLUMN_NAME_NAME + " (?)";
        Cursor cursor = database.rawQuery(sql, new String[] { name });
        if (cursor.moveToNext()) {
            Tag = cursorToTag(cursor);
        }
        cursor.close();
        close();
        return Tag;
    }
}
