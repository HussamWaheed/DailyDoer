package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Database helper class for managing the application's SQLite database.
 * Handles all CRUD operations for tasks and maintains database schema.
 */
public class dbhelper extends SQLiteOpenHelper {
    // Singleton instance for database helper
    private static dbhelper instance;

    /**
     * Constructor for database helper
     * @param context Application context
     */
    public dbhelper(@Nullable Context context) {
        super(context, "dailyTask", null, 4); // Database name and version
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create main tasks table
        String query1 = "CREATE TABLE schedule(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +          // Task title (required)
                "description TEXT, " +             // Task description (optional)
                "date TEXT NOT NULL, " +           // Due date (required)
                "time TEXT, " +                    // Due time (optional)
                "status TEXT NOT NULL, " +         // Completion status
                "importance TEXT DEFAULT 'High')"; // Priority level

        // Create archive table for deleted tasks
        String query2 = "CREATE TABLE deleted_tasks(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +          // Task title
                "description TEXT, " +             // Task description
                "date TEXT NOT NULL, " +           // Original due date
                "time TEXT, " +                   // Original due time
                "status TEXT NOT NULL, " +         // Completion status at deletion
                "deleted_at LONG)";                // Timestamp of deletion

        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop existing tables and recreate on upgrade
        String query1 = "DROP TABLE IF EXISTS schedule";
        String query2 = "DROP TABLE IF EXISTS deleted_tasks";
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.execSQL(query2);
        onCreate(sqLiteDatabase);
    }

    /**
     * Permanently deletes a task from the database
     * @param id The ID of the task to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteTask(int id) {
        SQLiteDatabase db = null;
        boolean success = false;

        try {
            db = getWritableDatabase();
            db.beginTransaction();

            // Delete task by ID
            int deleted = db.delete("schedule", "_id=?", new String[]{String.valueOf(id)});
            if (deleted > 0) {
                db.setTransactionSuccessful();
                success = true;
                Log.d("DELETE", "Task with ID " + id + " deleted successfully.");
            } else {
                Log.d("DELETE", "No task with ID " + id + " found.");
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
     * Archives a task by moving it to deleted_tasks table
     * @param taskId The ID of the task to archive
     * @return true if archiving was successful, false otherwise
     */
    public boolean moveToDeletedTasks(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Get task data from schedule table
            Cursor cursor = db.query("schedule",
                    null,
                    "_id=?",
                    new String[]{String.valueOf(taskId)},
                    null, null, null);

            boolean success = false;
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                // Copy all task data to new values
                values.put("title", cursor.getString(1));
                values.put("description", cursor.getString(2));
                values.put("date", cursor.getString(3));
                values.put("time", cursor.getString(4));
                values.put("status", cursor.getString(5));
                values.put("deleted_at", System.currentTimeMillis());

                // Insert into archive table
                long insertResult = db.insert("deleted_tasks", null, values);
                if (insertResult != -1) {
                    // Remove from main table if archived successfully
                    db.delete("schedule", "_id=?", new String[]{String.valueOf(taskId)});
                    success = true;
                }
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
     * @param taskId The ID of the task to check
     * @return true if task exists, false otherwise
     */
    public boolean taskExists(int taskId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM schedule WHERE _id=?",
                new String[]{String.valueOf(taskId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    /**
     * Retrieves tasks deleted within the last 24 hours
     * @return Cursor containing recently deleted tasks
     */
    public Cursor getRecentlyDeletedTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cutoff = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
        return db.query("deleted_tasks",
                null,
                "deleted_at > ?",
                new String[]{String.valueOf(cutoff)},
                null, null, "deleted_at DESC"); // Newest deletions first
    }

    /**
     * Adds a new task to the database
     * @return row ID of newly inserted task, or -1 if error occurred
     */
    public long addItems(String title, String description, String date,
                         String time, String status, String importance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("date", date);
        values.put("time", time);
        values.put("status", status);
        values.put("importance", importance);

        return db.insert("schedule", null, values);
    }

    /**
     * Retrieves tasks filtered by completion status
     * @param status "true" for completed, "false" for pending
     * @return Cursor containing matching tasks
     */
    public Cursor displayData(String status) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM schedule WHERE status=?",
                new String[]{status});
    }

    /**
     * Deletes tasks matching title and date
     * @return Cursor containing deleted tasks (for verification)
     */
    public Cursor deleteData(String title, String date) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM schedule WHERE title=? AND date=?",
                new String[]{title, date});
        if (cursor.getCount() > 0) {
            db.delete("schedule", "title=? AND date=?", new String[]{title, date});
        }
        return cursor;
    }

    /**
     * Updates an existing task
     * @return number of rows affected
     */
    public long editItem(String title, String newDescription, String date,
                         String time, String status, String importance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", newDescription);
        values.put("date", date);
        values.put("time", time);
        values.put("status", status);
        values.put("importance", importance);

        return db.update("schedule", values, "title=?", new String[]{title});
    }
}