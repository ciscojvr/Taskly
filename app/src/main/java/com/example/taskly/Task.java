package com.example.taskly;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Task {

    private String taskName;
    private String taskDueDate;
    private String taskDueTime;
    private String taskUrgency;
    private LatLng taskLocation;
    private Bitmap taskImage;

    public Task(String taskName, String taskDueDate, String taskDueTime, String taskUrgency, LatLng taskLocation, Bitmap taskImage) {
        this.taskName = taskName;
        this.taskDueDate = taskDueDate;
        this.taskDueTime = taskDueTime;
        this.taskUrgency = taskUrgency;
        this.taskLocation = taskLocation;
        this.taskImage = taskImage;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(String taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public String getTaskDueTime() {
        return taskDueTime;
    }

    public void setTaskDueTime(String taskDueTime) {
        this.taskDueTime = taskDueTime;
    }

    public String getTaskUrgency() {
        return taskUrgency;
    }

    public void setTaskUrgency(String taskUrgency) {
        this.taskUrgency = taskUrgency;
    }

    public LatLng getTaskLocation() { return taskLocation; }

    public void setTaskLocation(LatLng taskLocation) { this.taskLocation = taskLocation; }

    public Bitmap getTaskImage() { return taskImage; }

    public void setTaskImage(Bitmap taskImage) { this.taskImage = taskImage; }
}
