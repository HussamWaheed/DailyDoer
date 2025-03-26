package com.example.project;

public class dataSets {
    public int id;
    public String title;
    public String date;
    public String time;
    public String description;
    public String importance;

    // Constructor with ID (for tasks retrieved from the database)
    public dataSets(int id, String title, String description, String date, String time, String importance) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.importance = importance;
    }

    // Constructor without ID (for new tasks)
    public dataSets(String title, String description, String date, String time, String importance) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.importance = importance;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

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