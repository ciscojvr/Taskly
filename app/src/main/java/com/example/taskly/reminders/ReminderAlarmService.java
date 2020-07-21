package com.example.taskly.reminders;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import androidx.core.app.NotificationCompat;


import android.app.TaskStackBuilder;

import com.example.taskly.R;
import com.example.taskly.Task;
import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskContract.TaskEntry;


public class ReminderAlarmService extends IntentService {
    private static final String TAG = ReminderAlarmService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 42;

    //This is a deep link intent, and needs the task stack
    public static PendingIntent getReminderPendingIntent(Context context, Uri uri) {
        Intent action = new Intent(context, ReminderAlarmService.class);
        action.setData(uri);
        return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public ReminderAlarmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri uri = intent.getData();

        //Display a notification to view the task details
        Intent action = new Intent(this, Task.class);
        action.setData(uri);
//        PendingIntent operation = TaskStackBuilder.create(this)
//                .addNextIntentWithParentStack(action)
//                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Grab the task description
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String description = "";
        try {
            if (cursor != null && cursor.moveToFirst()) {
//                description = TaskContract.getColumnString(cursor, TaskEntry.COL_TASK_TITLE );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

//        Notification note = new NotificationCompat.Builder(this)
//                .setContentTitle("Please Complete Task!")
//                .setContentText(description)
//                .setSmallIcon(R.drawable.ic_done)
//                .setContentIntent(operation)
//                .setAutoCancel(true)
//                .build();

//        manager.notify(NOTIFICATION_ID, note);
    }
}

