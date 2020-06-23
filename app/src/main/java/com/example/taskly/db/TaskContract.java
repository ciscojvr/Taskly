package com.example.taskly.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.example.taskly.db";
    public static final int DB_Version = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_DATE = "date";
        public static final String COL_TASK_TIME = "time";
        public static final String COL_TASK_URGENCY = "urgency";
    }
}
