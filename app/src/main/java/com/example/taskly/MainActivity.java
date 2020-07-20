package com.example.taskly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskDbHelper;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewDone;
    private static final String TAG = "Main Activity"; // Using TAG constant for logging
    private TaskDbHelper mHelper;
    private ListView mTaskListView; // reference to the ListView created in activity_main.xml
    private ArrayAdapter<Task> mAdapter; // ArrayAdapter will help populate the ListView with the data

    SearchView taskSearchView;

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

    public void showAllCompletedTasks(View v) {
        Intent myIntent = new Intent(this, CompletedTasksActivity.class);
        this.startActivity(myIntent);
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
                        TaskContract.TaskEntry.COL_TASK_COMPLETION
                },
                TaskContract.TaskEntry.COL_TASK_COMPLETION + "='incomplete'",
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
                        TaskContract.TaskEntry.COL_TASK_COMPLETION
                },
                TaskContract.TaskEntry.COL_TASK_TITLE + " LIKE '%" + taskName + "%' AND " + TaskContract.TaskEntry.COL_TASK_COMPLETION + " = incomplete",
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

    public void completeTask(View view) {
        //Todo: When marking a task as complete we want to update the task in the database and change it from incomplete to complete.
        SQLiteDatabase db = mHelper.getWritableDatabase();

        View parent = (View) view.getParent();

        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());

        TextView dueDateTextView = (TextView) parent.findViewById(R.id.task_due_date);
        String dueDate = String.valueOf(dueDateTextView);

        TextView dueTimeTextView = (TextView) parent.findViewById(R.id.task_due_time);
        String dueTime = String.valueOf(dueTimeTextView);

        TextView urgencyTextView = (TextView) parent.findViewById(R.id.task_urgency);
        String urgency = String.valueOf(urgencyTextView);

        TextView locationTextView = (TextView) parent.findViewById(R.id.task_location);
        String locationString = String.valueOf(locationTextView);
        Log.i(TAG, "Location string is: " + locationString);

//        String locationLat = locationString.split(",")[0];
//        String locationLong = locationString.split(",")[1];

        TextView radiusTextView = (TextView) parent.findViewById(R.id.task_location_radius);
        String locationRadius = String.valueOf(radiusTextView);

        String completion = "complete";

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
        values.put(TaskContract.TaskEntry.COL_TASK_DATE, dueDate);
        values.put(TaskContract.TaskEntry.COL_TASK_TIME, dueTime);
        values.put(TaskContract.TaskEntry.COL_TASK_URGENCY, urgency);
        values.put(TaskContract.TaskEntry.COL_TASK_LOCATION_LAT, "0");
        values.put(TaskContract.TaskEntry.COL_TASK_LOCATION_LNG, "0");
        values.put(TaskContract.TaskEntry.COL_TASK_LOCATION_RADIUS, locationRadius);
        values.put(TaskContract.TaskEntry.COL_TASK_COMPLETION, completion);

        db.update(TaskContract.TaskEntry.TABLE, values, TaskContract.TaskEntry.COL_TASK_TITLE + " = " + task, null);
        db.close();

//        SQLiteDatabase db = mHelper.getWritableDatabase();
//        db.delete(TaskContract.TaskEntry.TABLE,
//                TaskContract.TaskEntry.COL_TASK_TITLE + " = ? ",
//                new String[]{task});
//        db.close();
//        updateUI();



//        ContentValues cv = new ContentValues();
//cv.put("Field1","Bob"); //These Fields should be your String values of actual column names
//cv.put("Field2","19");
//cv.put("Field2","Male");

//        myDB.update(TableName, cv, "_id="+id, null);
    }

    public void searchForTask(String task) {
        StringBuilder sb = new StringBuilder();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, null, TaskContract.TaskEntry.COL_TASK_TITLE + " LIKE '%" + task + "%' ", null, null, null, null);
        while (cursor.moveToNext()) {
            sb.append(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE)));
            sb.append("\n");
        }
        if (cursor.getCount() < 1) {
            Log.d(TAG, "No tasks found matching the search criteria.");
        } else {
            Log.d(TAG, "Search results:\n" + sb.toString());
        }

        updateUIWith(task);
    }
}