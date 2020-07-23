package com.example.taskly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "Add Task Activity"; // Using TAG constant for logging
    private TaskDbHelper mHelper;
//    private ArrayAdapter<String> mAdapter; // ArrayAdapter will help populate the ListView with the data
    EditText task;

    Button addDateButton;
    TextView dateLabel;

    Button addTimeButton;
    TextView timeLabel;

    Switch reminderSelection;

    private static EditText taskLocationLat, taskLocationLong, taskLocationRadius;

    Button cancelButton, addTaskButton;

//    boolean isTaskProvided, isDateProvided, isTimeProvided, isUrgencyLevelSelected, isLatProvided, isLongProvided, isRadiusProvided, isReminderProvided = false;
    boolean isTaskProvided, isDateProvided, isTimeProvided, isUrgencyLevelSelected, isLatProvided, isLongProvided, isRadiusProvided = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mHelper = new TaskDbHelper(this);

        task = (EditText) findViewById(R.id.editText_taskInfo);

        task.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                shouldAllowTaskAdd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isTaskProvided = true;
                shouldAllowTaskAdd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                isTaskProvided = true;
                shouldAllowTaskAdd();
            }
        });

        addDateButton = (Button) findViewById(R.id.button_addDate);
        dateLabel = (TextView) findViewById(R.id.textView_dateDue);
        dateLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                shouldAllowTaskAdd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isDateProvided = true;
                shouldAllowTaskAdd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                isDateProvided = true;
                shouldAllowTaskAdd();

            }
        });

        addTimeButton = (Button) findViewById(R.id.button_addTime);
        timeLabel = (TextView) findViewById(R.id.textView_timeDue);
        timeLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                shouldAllowTaskAdd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isTimeProvided = true;
                shouldAllowTaskAdd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                isTimeProvided = true;
                shouldAllowTaskAdd();
            }
        });

        RadioGroup radioGroupUrgencies = (RadioGroup)findViewById(R.id.radioGroup_urgency_levels);
        radioGroupUrgencies.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isUrgencyLevelSelected = true;
                shouldAllowTaskAdd();
            }
        });

        taskLocationLat = (EditText) findViewById(R.id.editText_locationLat);
        taskLocationLat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                shouldAllowTaskAdd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isLatProvided = true;
                shouldAllowTaskAdd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                isLatProvided = true;
                shouldAllowTaskAdd();
            }
        });

        taskLocationLong = (EditText) findViewById(R.id.editText_locationLng);
        taskLocationLong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                shouldAllowTaskAdd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isLongProvided = true;
                shouldAllowTaskAdd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                isLongProvided = true;
                shouldAllowTaskAdd();
            }
        });

        taskLocationRadius = (EditText) findViewById(R.id.editText_locationRadius);
        taskLocationRadius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                shouldAllowTaskAdd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isRadiusProvided = true;
                shouldAllowTaskAdd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                isRadiusProvided = true;
                shouldAllowTaskAdd();
            }
        });

        reminderSelection = (Switch) findViewById(R.id.task_reminder_selection);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        addTaskButton = (Button) findViewById(R.id.button_addTask);
        addTaskButton.setEnabled(false);
    }

    public static boolean areAllTrue(boolean[] array)
    {
        for(boolean b : array) if(!b) return false;
        return true;
    }

    public void shouldAllowTaskAdd() {
        boolean[] inputsProvided = {isTaskProvided, isDateProvided, isTimeProvided, isUrgencyLevelSelected, isLatProvided, isLongProvided, isRadiusProvided};
        if (areAllTrue(inputsProvided)) {
            addTaskButton.setEnabled(true);
        } else {
            addTaskButton.setEnabled(false);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_addDate:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;
            case R.id.button_addTime:
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                break;
            case R.id.button_cancel:
                cancelAddTask();
                break;
            case R.id.button_addTask:
                addTask();
                break;
            case R.id.button_chooseLocation:
                //Call the new activity here
                getLocation();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG,"onStart called");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = new SimpleDateFormat("MM/dd/yyy", Locale.ENGLISH).format(c.getTime());
        dateLabel.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String strMinsToShow = (minute >= 10) ? String.valueOf(minute): "0" + minute;
        timeLabel.setText(hourOfDay + ":" + strMinsToShow);
    }

    public void addTask() {
        String taskInfo = String.valueOf(task.getText());

        String dueDate = String.valueOf(dateLabel.getText());

        String dueTime = String.valueOf(timeLabel.getText());

        RadioGroup taskUrgencyGroup = findViewById(R.id.radioGroup_urgency_levels);
        int taskUrgencyId = taskUrgencyGroup.getCheckedRadioButtonId();
        RadioButton taskUrgency = findViewById(taskUrgencyId);
        String urgency = taskUrgency.getText().toString();

        String locationLat = String.valueOf(taskLocationLat.getText());
        String locationLong = String.valueOf(taskLocationLong.getText());
        String locationRadius = String.valueOf(taskLocationRadius.getText());

        boolean shouldRemind = reminderSelection.isChecked();
        String remindMeSelection;
        if (shouldRemind) {
            remindMeSelection = "Yes";
        } else {
            remindMeSelection = "No";
        }

        Log.d(TAG, "Task to add: " + task);
        Log.d(TAG, "Task due date: " + dueDate);
        Log.d(TAG, "Task due time: " + dueTime);
        Log.d(TAG, "Task urgency: " + urgency);
        Log.d(TAG, "Task Location (Lat): " + locationLat);
        Log.d(TAG, "Task Location (Lng): " + locationLong);
        Log.d(TAG, "Task Location Radius (m): " + locationRadius);
        Log.d(TAG, "Task reminder selection: " + remindMeSelection);

        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, taskInfo);
        values.put(TaskContract.TaskEntry.COL_TASK_DATE, dueDate);
        values.put(TaskContract.TaskEntry.COL_TASK_TIME, dueTime);
        values.put(TaskContract.TaskEntry.COL_TASK_URGENCY, urgency);
        values.put(TaskContract.TaskEntry.COL_TASK_LOCATION_LAT, locationLat);
        values.put(TaskContract.TaskEntry.COL_TASK_LOCATION_LNG, locationLong);
        values.put(TaskContract.TaskEntry.COL_TASK_LOCATION_RADIUS, locationRadius);
        values.put(TaskContract.TaskEntry.COL_TASK_REMINDER, remindMeSelection);
        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
    }

    public void cancelAddTask() {
        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
    }

    public void getLocation() {
        String newLat = "";
        String newLng = "";

        //Call get location activity here
        Intent i = new Intent(AddTaskActivity.this, ChooseLocationActivity.class);
        startActivity(i);

        newLat = String.valueOf(ChooseLocationActivity.LastLatLng.latitude);
        newLng = String.valueOf(ChooseLocationActivity.LastLatLng.longitude);

        taskLocationLat.setText(newLat);
        taskLocationLong.setText(newLng);
    }

    public static void SetInfoFromLocationChooser(double lat, double lng) {
        taskLocationLat.setText(String.valueOf(lat));
        taskLocationLong.setText(String.valueOf(lng));
        taskLocationRadius.setText("1");
    }
}