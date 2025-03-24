package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbhelper extends SQLiteOpenHelper {

    private static dbhelper instance;
    public dbhelper(@Nullable Context context) {
        super(context, "dailyTask", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query1 = "CREATE TABLE schedule(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, description TEXT, date TEXT NOT NULL, time TEXT, status TEXT NOT NULL)";
        sqLiteDatabase.execSQL(query1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query1= "DROP TABLE IF EXISTS schedule";
        sqLiteDatabase.execSQL(query1);
        onCreate(sqLiteDatabase);
    }

    public long addItems(String newtitle, String newdescription, String newdate, String newtime,String status){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", newtitle);
        contentValues.put("description", newdescription);
        contentValues.put("date",newdate);
        contentValues.put("time", newtime);
        contentValues.put("status", status);
        return sqLiteDatabase.insert("schedule", null, contentValues);
    }

    public Cursor displayData(String status){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM schedule WHERE status=?",new String[]{status});
        return cursor;
    }
    public Cursor complete_displayData(String status){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM schedule WHERE status=?",new String[]{status});
        return cursor;
    }

    public Cursor deleteData(String title, String date){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM schedule WHERE title=? AND date=?", new String[]{title, date});
        if(cursor.getCount() > 0){
            sqLiteDatabase.delete("schedule", "title=? AND date=?", new String[]{title, date});
        }
        return  cursor;
    }

    public long editItem(String title, String newDescription, String date, String time, String status){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", newDescription);
        contentValues.put ("date",date);
        contentValues.put ("time",time);
        contentValues.put ("status",status);
        return sqLiteDatabase.update("schedule", contentValues, "title=?", new String[]{title});
    }

}
