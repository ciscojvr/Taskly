package com.example.taskly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskDbHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewDone;
    private static final String TAG = "Main Activity"; // Using TAG constant for logging
    private TaskDbHelper mHelper;
    private ListView mTaskListView; // reference to the ListView created in activity_main.xml
    private ArrayAdapter<Task> mAdapter; // ArrayAdapter will help populate the ListView with the data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewDone = (TextView) findViewById(R.id.textViewDone);
        mTextViewDone.setText("");

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.task_list);

        updateUI();
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
                        TaskContract.TaskEntry.COL_TASK_LOCATION_RADIUS
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

            LatLng locationLatLng = new LatLng(Double.parseDouble(locationLatOfTask), Double.parseDouble(locationLngOfTask));
            double locationRadius = Double.parseDouble(locationRadiusOfTask);
            Task currentTask = new Task(titleOfTask, dueDateOfTask, dueTimeOfTask, urgencyOfTask, locationLatLng, locationRadius);

            taskList.add(currentTask);
            Log.d(
                    TAG,
                    "Task: " +
                            currentTask.getTaskName() +
                            " Due on: " +
                            currentTask.getTaskDueDate() +
                            " At: " +
                            currentTask.getTaskDueTime() +
                            " With Urgency: " +
                            currentTask.getTaskUrgency() +
                            " Location/Radius: " +
                            currentTask.getTaskLocation() + "/" + currentTask.getTaskLocationRadius()
                    );
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
}