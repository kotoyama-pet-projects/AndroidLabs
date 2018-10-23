package com.example.kotoyama.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DataBase {
    private static final String DATABASE_NAME = "ToDoListDataBase";
    private static final String DATABASE_TABLE = "TaskTable";
    private static final int DATABASE_VERSION = 1;

    static final String KEY_ID = "_id";
    static final String KEY_NAME = "Task";

    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + "(" + KEY_ID
            + " integer primary key autoincrement, "
            + KEY_NAME + " text not null);";

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    DataBase(Context _context) {
        dbHelper = new DBHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    void open() throws SQLException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = dbHelper.getReadableDatabase();
        }
    }

    void close() {
        db.close();
    }

    Cursor getAllEntries() {
        return db.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_NAME },
                null, null, null, null, null);
    }

    void insertEntry(String task) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(KEY_NAME, task);
        db.insert(DATABASE_TABLE, null, insertValues);
    }

    void removeEntry(long id) {
        db.delete(DATABASE_TABLE, KEY_ID + "=" + id, null);
    }

    private static class DBHelper extends SQLiteOpenHelper {
        DBHelper(Context context, String name,
                 SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion
                    + " to " + _newVersion + ", which will destroy all old data");
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(_db);
        }
    }
}