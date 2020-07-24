package com.example.taskly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends ArrayAdapter<Task> {

    private Context mContext;
    private List<Task> tasksList = new ArrayList<>();

    public TasksAdapter(@NonNull Context context, ArrayList<Task> list) {
        super(context, 0, list);
        mContext = context;
        tasksList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.task_todo, parent, false);
        }

            Task currentTask = tasksList.get(position);

            TextView taskTitle = (TextView) listItem.findViewById(R.id.task_title);
            taskTitle.setText(currentTask.getTaskName());

            TextView taskDueDate = (TextView) listItem.findViewById(R.id.task_due_date);
            taskDueDate.setText(currentTask.getTaskDueDate());

            TextView taskDueTime = (TextView) listItem.findViewById(R.id.task_due_time);
            taskDueTime.setText(currentTask.getTaskDueTime());

            TextView taskUrgency = (TextView) listItem.findViewById(R.id.task_urgency);
            taskUrgency.setText(currentTask.getTaskUrgency());

            TextView taskLocationLatLng = (TextView) listItem.findViewById(R.id.task_location);
            taskLocationLatLng.setText(String.valueOf(currentTask.getTaskLocation()));

            ImageView taskImage = (ImageView) listItem.findViewById(R.id.imageView_taskImage);
            taskImage.setImageBitmap(currentTask.getTaskImage());

            return listItem;
    }
}
