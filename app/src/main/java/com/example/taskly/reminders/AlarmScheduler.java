package com.example.taskly.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;

public class AlarmScheduler {


    public void scheduleAlarm(Context context, long alarmTime, Uri reminderTask) {
        //Schedule the alarm. Will update an existing item for the same task.
        AlarmManager manager = AlarmManagerProvider.getAlarmManager(context);

        PendingIntent operation =
                ReminderAlarmService.getReminderPendingIntent(context, reminderTask);

        manager.setExact(AlarmManager.RTC, alarmTime, operation);
    }
}
