/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.conquer.db;

/**
 * 数据库相关常量
 * Created by Hanks on 2015/5/30.
 */
public class DBConstants {

    /* Inner class that defines the table contents */
    public static abstract class TaskColum{
        public static final String TABLE_NAME             = "Task";
        public static final String COLUMN_NAME_ID         = "taskId";
        public static final String COLUMN_NAME_NAME       = "name";
        public static final String COLUMN_NAME_TIME       = "time";
        public static final String COLUMN_NAME_TAGID      = "tagId";
        public static final String COLUMN_NAME_REPEAT     = "repeat";
        public static final String COLUMN_NAME_NEEDALERT  = "needAlert";
        public static final String COLUMN_NAME_NOTE       = "note";
        public static final String COLUMN_NAME_IMAGEURL   = "imageUrl";
        public static final String COLUMN_NAME_AUDIOURL   = "audioUrl";
        public static final String COLUMN_NAME_ATFRIENDS  = "atFriends";
        public static final String COLUMN_NAME_HASALERTED = "hasAlerted";
    }

    /* Inner class that defines the table contents */
    public static abstract class TagColum{
        public static final String TABLE_NAME             = "Tag";
        public static final String COLUMN_NAME_ID         = "tagId";
        public static final String COLUMN_NAME_NAME       = "name";
    }


}
