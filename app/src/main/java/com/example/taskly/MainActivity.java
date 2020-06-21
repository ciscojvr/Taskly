package com.example.taskly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskDbHelper;

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


                final View customLayout = getLayoutInflater().inflate(R.layout.activity_add_task, null);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(customLayout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText taskInfo = customLayout.findViewById(R.id.editText_taskInfo);
                                EditText taskDueDate = customLayout.findViewById(R.id.editText_dateDue);
                                EditText taskDueTime = customLayout.findViewById(R.id.editText_timeDue);

                                RadioGroup taskUrgencyGroup = customLayout.findViewById(R.id.radioGroup_urgency_levels);
                                int taskUrgencyId = taskUrgencyGroup.getCheckedRadioButtonId();
                                RadioButton taskUrgency = customLayout.findViewById(taskUrgencyId);

                                String task = String.valueOf(taskInfo.getText());
                                String dueDate = String.valueOf(taskDueDate.getText());
                                String dueTime = String.valueOf(taskDueTime.getText());
                                String urgency = taskUrgency.getText().toString();

                                Log.d(TAG, "Task to add: " + task);
                                Log.d(TAG, "Task due date: " + dueDate);
                                Log.d(TAG, "Task due time: " + dueTime);
                                Log.d(TAG, "Task urgency: " + urgency);

                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                values.put(TaskContract.TaskEntry.COL_TASK_DATE, dueDate);
                                values.put(TaskContract.TaskEntry.COL_TASK_TIME, dueTime);
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

                return true;
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
                new String[] {TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE, TaskContract.TaskEntry.COL_TASK_DATE, TaskContract.TaskEntry.COL_TASK_TIME},
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

            Task currentTask = new Task(titleOfTask, dueDateOfTask, dueTimeOfTask);

            taskList.add(currentTask);
            Log.d(
                    TAG,
                    "Task: " +
                            currentTask.getTaskName() +
                            " Due on: " +
                            currentTask.getTaskDueDate() +
                            " At: " +
                            currentTask.getTaskDueTime());
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