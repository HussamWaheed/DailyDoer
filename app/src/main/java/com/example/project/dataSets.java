package com.example.project;

import android.view.View;
import android.widget.CheckBox;

import java.sql.Time;
import java.util.Date;

public class dataSets {

    public String title;
    public String date;
    public String time;
    public String description;


    public dataSets(String title, String description, String date, String time){
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public String getTitle(){return title;}
    public String getDate(){return date;}
    public String getTime(){return time;}
    public String getDescription(){return description;}

}
