package com.example.project;

public class dataSets {
    private int id;               // ID for tasks retrieved from the database
    private String title;         // Task title
    private String date;          // Task due date
    private String time;          // Task time
    private String description;   // Task description
    private String importance;    // Task importance level

    // Constructor for tasks retrieved from the database (includes ID)
    public dataSets(int id, String title, String description, String date, String time, String importance) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.importance = importance;
    }

    // Constructor for new tasks (does not include ID)
    public dataSets(String title, String description, String date, String time, String importance) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.importance = importance;
    }

    // Getter for ID (used when retrieving tasks from the database)
    public int getId() {
        return id;
    }

    // Getter for Title
    public String getTitle() {
        return title;
    }

    // Getter for Description
    public String getDescription() {
        return description;
    }

    // Getter for Date
    public String getDate() {
        return date;
    }

    // Getter for Time
    public String getTime() {
        return time;
    }

    // Getter for Importance
    public String getImportance() {
        return importance;
    }

    // Optional: toString() method for easier debugging and logging
    @Override
    public String toString() {
        return "dataSets{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", importance='" + importance + '\'' +
                '}';
    }
}
