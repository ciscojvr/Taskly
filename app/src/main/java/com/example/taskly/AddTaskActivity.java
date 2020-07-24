package com.example.taskly;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskly.db.TaskContract;
import com.example.taskly.db.TaskDbHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;


public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "Add Task Activity"; // Using TAG constant for logging
    private TaskDbHelper mHelper;
    private static EditText task;

    private static ImageView imageView;

    private static TextView dateLabel, timeLabel, latitudeLabel, longitudeLabel;

    boolean isTaskProvided, isDateProvided, isTimeProvided, isUrgencyLevelSelected, isLatitudeProvided, isLongitudeProvided = false;

    String pathToFile;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mHelper = new TaskDbHelper(this);

        task = (EditText) findViewById(R.id.editText_taskInfo);

        imageView = (ImageView) findViewById(R.id.imageView_image);

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

        latitudeLabel = (TextView) findViewById(R.id.textView_locationLat);
        latitudeLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                shouldAllowTaskAdd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isLatitudeProvided = true;
                shouldAllowTaskAdd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                isLatitudeProvided = true;
                shouldAllowTaskAdd();
            }
        });

        longitudeLabel = (TextView) findViewById(R.id.textView_locationLng);
        longitudeLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                shouldAllowTaskAdd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isLongitudeProvided = true;
                shouldAllowTaskAdd();
            }

            @Override
            public void afterTextChanged(Editable s) {
                isLongitudeProvided = true;
                shouldAllowTaskAdd();
            }
        });

        findViewById(R.id.button_addTask).setEnabled(false);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
    }

    public static boolean areAllTrue(boolean[] array)
    {
        for(boolean b : array) if(!b) return false;
        return true;
    }

    public void shouldAllowTaskAdd() {
        boolean[] inputsProvided = {isTaskProvided, isDateProvided, isTimeProvided, isUrgencyLevelSelected, isLatitudeProvided, isLongitudeProvided};
        if (areAllTrue(inputsProvided)) {
            findViewById(R.id.button_addTask).setEnabled(true);
        } else {
            findViewById(R.id.button_addTask).setEnabled(false);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_addPhoto:
                // ToDo: Implement add a photo to the database by using the camera.
                Log.i(TAG, "Add Photo Button Pressed.");
                getImage();
                break;
            case R.id.button_addDate:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;
            case R.id.button_addTime:
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                break;
            case R.id.button_addLocation:
                getLocation();
                break;
            case R.id.button_cancel:
                cancelAddTask();
                break;
            case R.id.button_addTask:
                addTask();
                break;

        }
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

        String locationLat = String.valueOf(latitudeLabel.getText());
        String locationLong = String.valueOf(longitudeLabel.getText());

        byte[] data;
        if (imageBitmap != null) {
            data = getBitmapAsByteArray(imageBitmap);
        } else {
            data = null;
        }


        Log.d(TAG, "Task to add: " + task);
        Log.d(TAG, "Task due date: " + dueDate);
        Log.d(TAG, "Task due time: " + dueTime);
        Log.d(TAG, "Task urgency: " + urgency);
        Log.d(TAG, "Task Location (Lat): " + locationLat);
        Log.d(TAG, "Task Location (Lng): " + locationLong);

        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, taskInfo);
        values.put(TaskContract.TaskEntry.COL_TASK_DATE, dueDate);
        values.put(TaskContract.TaskEntry.COL_TASK_TIME, dueTime);
        values.put(TaskContract.TaskEntry.COL_TASK_URGENCY, urgency);
        values.put(TaskContract.TaskEntry.COL_TASK_LOCATION_LAT, locationLat);
        values.put(TaskContract.TaskEntry.COL_TASK_LOCATION_LNG, locationLong);
        values.put(TaskContract.TaskEntry.COL_TASK_IMAGE, data);
        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
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

        newLat = String.valueOf(Math.round(ChooseLocationActivity.LastLatLng.latitude * 1000000.0)/1000000.0);
        newLng = String.valueOf(Math.round(ChooseLocationActivity.LastLatLng.longitude * 1000000.0)/1000000.0);

        latitudeLabel.setText(newLat);
        longitudeLabel.setText(newLng);
    }

    public static void SetInfoFromLocationChooser(double lat, double lng) {
        latitudeLabel.setText(String.valueOf(Math.round(lat * 1000000.0)/1000000.0));
        longitudeLabel.setText(String.valueOf(Math.round(lng * 1000000.0)/1000000.0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageBitmap = BitmapFactory.decodeFile(pathToFile);
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void getImage() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            photoFile = createPhotoFile();

            if(photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.taskly.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePic, 1);
            }
        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (Exception e) {
            Log.d("myLog", "Exception : " + e.toString());
        }
        return image;
    }

}