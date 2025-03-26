package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class dbhelper extends SQLiteOpenHelper {

    private static dbhelper instance;

    public dbhelper(@Nullable Context context) {
        super(context, "dailyTask", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query1 = "CREATE TABLE schedule(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, description TEXT, date TEXT NOT NULL, time TEXT, status TEXT NOT NULL, importance TEXT DEFAULT 'High')";
        sqLiteDatabase.execSQL(query1);

        // Added deleted_tasks table
        String query2 = "CREATE TABLE deleted_tasks(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, description TEXT, date TEXT NOT NULL, time TEXT, status TEXT NOT NULL, deleted_at LONG)";
        sqLiteDatabase.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query1 = "DROP TABLE IF EXISTS schedule";
        String query2 = "DROP TABLE IF EXISTS deleted_tasks";
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.execSQL(query2);
        onCreate(sqLiteDatabase);
    }

    /**
     * Deletes a specific task using task ID as a unique identifier
     */
    public boolean deleteTask(int id) {
        SQLiteDatabase db = null;
        boolean success = false;

        try {
            db = getWritableDatabase();
            db.beginTransaction();

            int deleted = db.delete("schedule", "_id=?", new String[]{String.valueOf(id)});
            if (deleted > 0) {
                db.setTransactionSuccessful();
                success = true;
            }
        } catch (Exception e) {
            Log.e("DELETE", "Error deleting task", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();  // Ensure the database is properly closed
            }
        }

        return success;
    }

    /**
     * Moves specific task to archive using task ID
     */
    public boolean moveToDeletedTasks(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.query("schedule",
                    null,
                    "_id=?",
                    new String[]{String.valueOf(taskId)},
                    null, null, null);

            boolean success = false;
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("title", cursor.getString(1));
                values.put("description", cursor.getString(2));
                values.put("date", cursor.getString(3));
                values.put("time", cursor.getString(4));
                values.put("status", cursor.getString(5));
                values.put("deleted_at", System.currentTimeMillis());

                success = db.insert("deleted_tasks", null, values) != -1;
            }
            cursor.close();
            db.setTransactionSuccessful();
            return success;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Archive failed", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Checks if a task exists in the database
     */
    public boolean taskExists(int taskId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM schedule WHERE _id=?", new String[]{String.valueOf(taskId)});
        boolean exists = cursor.moveToFirst(); // Move to first row to check if any result exists
        cursor.close();
        return exists;
    }

    /**
     * Retrieves recently deleted tasks (within the last 24 hours)
     */
    public Cursor getRecentlyDeletedTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cutoff = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        return db.query("deleted_tasks",
                null,
                "deleted_at > ?",
                new String[]{String.valueOf(cutoff)},
                null, null, "deleted_at DESC");
    }

    /**
     * Adds a new task to the schedule
     */
    public long addItems(String newtitle, String newdescription, String newdate, String newtime, String status, String importance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newtitle);
        values.put("description", newdescription);
        values.put("date", newdate);
        values.put("time", newtime);
        values.put("status", status);
        values.put("importance", importance); // Include importance field

        return db.insert("schedule", null, values);
    }


    public Cursor displayData(String status) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM schedule WHERE status=?", new String[]{status});
    }

    public Cursor complete_displayData(String status) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM schedule WHERE status=?", new String[]{status});
    }

    public Cursor deleteData(String title, String date) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM schedule WHERE title=? AND date=?", new String[]{title, date});
        if (cursor.getCount() > 0) {
            sqLiteDatabase.delete("schedule", "title=? AND date=?", new String[]{title, date});
        }
        return cursor;
    }

    public long editItem(String title, String newDescription, String date, String time, String status, String importance) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", newDescription);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("status", status);
        contentValues.put("importance", importance); // Ensure importance is updated
    
        return sqLiteDatabase.update("schedule", contentValues, "title=?", new String[]{title});
    }
}
