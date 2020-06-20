package com.example.taskly;

public class Task {

    private String taskName;
    private String taskDueDate;
    private String taskDueTime;

    public Task(String taskName, String taskDueDate, String taskDueTime) {
        this.taskName = taskName;
        this.taskDueDate = taskDueDate;
        this.taskDueTime = taskDueTime;
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
}
