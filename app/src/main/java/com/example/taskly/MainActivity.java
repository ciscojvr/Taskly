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
import android.widget.TextView;

import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskDbHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewDone;
    private static final String TAG = "Main Activity"; // Using TAG constant for logging
    private TaskDbHelper mHelper;
    private ListView mTaskListView; // reference to the ListView created in activity_main.xml
    private ArrayAdapter<String> mAdapter; // ArrayAdapter will help populate the ListView with the data

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

                final EditText taskEditText = new EditText(this);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                Log.d(TAG, "Task to add: " + task);

                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
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
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
            Log.d(TAG, "Task: " + cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.task_todo,
                    R.id.task_title,
                    taskList);
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