package com.example.taskly;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskDbHelper;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewDone;
    private static final String TAG = "Main Activity"; // Using TAG constant for logging
    private TaskDbHelper mHelper;
    private ListView mTaskListView; // reference to the ListView created in activity_main.xml
    private ArrayAdapter<Task> mAdapter; // ArrayAdapter will help populate the ListView with the data

    SearchView taskSearchView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewDone = (TextView) findViewById(R.id.textViewDone);
        mTextViewDone.setText("");

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.task_list);

        taskSearchView = (SearchView) findViewById(R.id.searchView_tasks);
        taskSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchForTask(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchForTask(newText);
                return true;
            }
        });

        updateUI();
        getTasksDueToday();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This method will render the menu in the main activity
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // onCreateOptionsMenu reacts to different user interactions with the menu item(s)
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Log.d(TAG, "Adding a new task");
                Intent myIntent = new Intent(this, AddTaskActivity.class);
                this.startActivity(myIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        isDatabaseEmpty();
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE,
                new String[] {
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DATE,
                        TaskContract.TaskEntry.COL_TASK_TIME,
                        TaskContract.TaskEntry.COL_TASK_URGENCY,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_LAT,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_LNG,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_RADIUS,
                        TaskContract.TaskEntry.COL_TASK_REMINDER
                },
                null,
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {

            int idxTitle = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            String titleOfTask = cursor.getString((idxTitle));

            int idxDueDate = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE);
            String dueDateOfTask = cursor.getString((idxDueDate));

            int idxDueTime = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TIME);
            String dueTimeOfTask = cursor.getString((idxDueTime));

            int idxUrgency = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_URGENCY);
            String urgencyOfTask = cursor.getString((idxUrgency));

            int idxLocationLat = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_LOCATION_LAT);
            String locationLatOfTask = cursor.getString((idxLocationLat));

            int idxLocationLng = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_LOCATION_LNG);
            String locationLngOfTask = cursor.getString((idxLocationLng));

            int idxLocationRadius = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_LOCATION_RADIUS);
            String locationRadiusOfTask = cursor.getString((idxLocationRadius));

            int idxReminder = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_REMINDER);
            String remindMeValue = cursor.getString((idxReminder));

            LatLng locationLatLng = new LatLng(Double.parseDouble(locationLatOfTask), Double.parseDouble(locationLngOfTask));
            double locationRadius = Double.parseDouble(locationRadiusOfTask);
            Task currentTask = new Task(titleOfTask, dueDateOfTask, dueTimeOfTask, urgencyOfTask, locationLatLng, locationRadius, remindMeValue);

            taskList.add(currentTask);
//            Log.d(
//                    TAG,
//                    "Task: " +
//                            currentTask.getTaskName() +
//                            " Due on: " +
//                            currentTask.getTaskDueDate() +
//                            " At: " +
//                            currentTask.getTaskDueTime() +
//                            " With Urgency: " +
//                            currentTask.getTaskUrgency() +
//                            " Location/Radius: " +
//                            currentTask.getTaskLocation() + "/" + currentTask.getTaskLocationRadius()
//                    );
        }

        if (mAdapter == null) {
            mAdapter = new TasksAdapter(this, taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    private void updateUIWith(String taskName) {
        isDatabaseEmpty();
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE,
                new String[] {
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DATE,
                        TaskContract.TaskEntry.COL_TASK_TIME,
                        TaskContract.TaskEntry.COL_TASK_URGENCY,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_LAT,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_LNG,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_RADIUS,
                        TaskContract.TaskEntry.COL_TASK_REMINDER

                },
                TaskContract.TaskEntry.COL_TASK_TITLE + " LIKE '%" + taskName + "%' ",
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {

            int idxTitle = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            String titleOfTask = cursor.getString((idxTitle));

            int idxDueDate = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE);
            String dueDateOfTask = cursor.getString((idxDueDate));

            int idxDueTime = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TIME);
            String dueTimeOfTask = cursor.getString((idxDueTime));

            int idxUrgency = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_URGENCY);
            String urgencyOfTask = cursor.getString((idxUrgency));

            int idxLocationLat = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_LOCATION_LAT);
            String locationLatOfTask = cursor.getString((idxLocationLat));

            int idxLocationLng = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_LOCATION_LNG);
            String locationLngOfTask = cursor.getString((idxLocationLng));

            int idxLocationRadius = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_LOCATION_RADIUS);
            String locationRadiusOfTask = cursor.getString((idxLocationRadius));

            int idxReminder = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_REMINDER);
            String remindMeValue = cursor.getString((idxReminder));

            LatLng locationLatLng = new LatLng(Double.parseDouble(locationLatOfTask), Double.parseDouble(locationLngOfTask));
            double locationRadius = Double.parseDouble(locationRadiusOfTask);
            Task currentTask = new Task(titleOfTask, dueDateOfTask, dueTimeOfTask, urgencyOfTask, locationLatLng, locationRadius, remindMeValue);

            taskList.add(currentTask);
//            Log.d(
//                    TAG,
//                    "Task: " +
//                            currentTask.getTaskName() +
//                            " Due on: " +
//                            currentTask.getTaskDueDate() +
//                            " At: " +
//                            currentTask.getTaskDueTime() +
//                            " With Urgency: " +
//                            currentTask.getTaskUrgency() +
//                            " Location/Radius: " +
//                            currentTask.getTaskLocation() + "/" + currentTask.getTaskLocationRadius()
//            );
        }

        if (mAdapter == null) {
            mAdapter = new TasksAdapter(this, taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    private void isDatabaseEmpty() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String count = "SELECT count(*) FROM tasks";
        Cursor mCursor = db.rawQuery(count, null);
        mCursor.moveToFirst();
        int icount = mCursor.getInt(0);
        if (icount > 0) {
            mTextViewDone.setText("Click on the \"Done\" button to remove task.");
        } else {
            mTextViewDone.setText("");
        }
        mCursor.close();
        db.close();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ? ",
                new String[]{task});
        db.close();
        updateUI();
    }

    public void searchForTask(String task) {
//        StringBuilder sb = new StringBuilder();
//        SQLiteDatabase db = mHelper.getWritableDatabase();
//        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, null, TaskContract.TaskEntry.COL_TASK_TITLE + " LIKE '%" + task + "%' ", null, null, null, null);
//        while (cursor.moveToNext()) {
//            sb.append(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE)));
//            sb.append("\n");
//        }
//        if (cursor.getCount() < 1) {
//            Log.d(TAG, "No tasks found matching the search criteria.");
//        } else {
//            Log.d(TAG, "Search results:\n" + sb.toString());
//        }

        updateUIWith(task);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getTasksDueToday() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyy");
        Date date = new Date();
        String todaysDate = formatter.format(date);
        Log.i(TAG, "Today's date is " + todaysDate);

        SQLiteDatabase db = mHelper.getReadableDatabase();

        StringBuilder sb = new StringBuilder();
        ArrayList<String> dueToday = new ArrayList<String>();

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE,
                new String[] {
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DATE,
                        TaskContract.TaskEntry.COL_TASK_TIME,
                        TaskContract.TaskEntry.COL_TASK_URGENCY,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_LAT,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_LNG,
                        TaskContract.TaskEntry.COL_TASK_LOCATION_RADIUS,
                        TaskContract.TaskEntry.COL_TASK_REMINDER

                },
                TaskContract.TaskEntry.COL_TASK_DATE + " LIKE '%" + todaysDate + "%' AND " + TaskContract.TaskEntry.COL_TASK_REMINDER + " = 'Yes'",
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {

            int idxTitle = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            String titleOfTask = cursor.getString((idxTitle));

            int idxDueTime = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TIME);
            String dueTimeOfTask = cursor.getString((idxDueTime));
            String newDueTimeFormat = dueTimeOfTask.replaceAll("[^\\d:]", "");
            int hour = Integer.parseInt(newDueTimeFormat.split(":")[0]);
            int minute = Integer.parseInt(newDueTimeFormat.split(":")[1]);
            Log.i(TAG, "Hour is: " + hour + " and minute is: " + minute);

//            Calendar c = Calendar.getInstance();
//            c.set(Calendar.HOUR_OF_DAY,hour);
//            c.set(Calendar.MINUTE,minute);
//            c.set(Calendar.SECOND, 0);
            sb.append(titleOfTask + "\n");

            dueToday.add(titleOfTask + "," + dueTimeOfTask);
        }

        cursor.close();
        db.close();

        Log.i(TAG, sb.toString());

        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("These tasks are due today:")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(sb.length() > 0 ? sb.toString() : "No tasks due today."))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }
}