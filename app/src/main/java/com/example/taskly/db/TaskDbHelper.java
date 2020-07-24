package com.example.taskly.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {

    public TaskDbHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE + " (" +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_DATE + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_TIME + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_URGENCY + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_LOCATION_LAT + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_LOCATION_LNG + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.COL_TASK_IMAGE + " BLOB);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
        onCreate(db);
    }
}
