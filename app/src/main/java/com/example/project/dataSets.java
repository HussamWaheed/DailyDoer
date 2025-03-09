package com.example.project;

import android.widget.CheckBox;

import java.sql.Time;
import java.util.Date;

public class dataSets {

    public String title;
    public Date date;
    public Time time;
    public String description;

    public dataSets(String title, Date date, Time time, String description){
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public String getTitle(){return title;}
    public Date getDate(){return date;}
    public Time getTime(){return time;}
    public String getDescription(){return description;}

}
