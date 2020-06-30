package com.example.taskly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskDbHelper;

import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {
    private static final String TAG = "Add Task Activity"; // Using TAG constant for logging
    private TaskDbHelper mHelper;
    private ArrayAdapter<String> mAdapter; // ArrayAdapter will help populate the ListView with the data
    EditText enteredTask;
    EditText enteredDueDate;
    EditText enteredDueTime;
    EditText enteredLocationLat;
    EditText enteredLocationLng;
    EditText enteredLocationRadius;
    Button addTaskButton;
    Button cancelAddTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        enteredTask = (EditText) findViewById(R.id.editText_taskInfo);
        enteredDueDate = (EditText) findViewById(R.id.editText_dateDue);
        enteredDueTime = (EditText) findViewById(R.id.editText_timeDue);
        enteredLocationLat = (EditText) findViewById(R.id.editText_locationLat);
        enteredLocationLng = (EditText) findViewById(R.id.editText_locationLng);
        enteredLocationRadius = (EditText) findViewById(R.id.editText_locationRadius);

        Log.d(null,"onCreate within AddTask called");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(null,"onStart within AddTask called");
    }
}