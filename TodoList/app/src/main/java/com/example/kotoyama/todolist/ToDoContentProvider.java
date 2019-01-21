package com.example.kotoyama.todolist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ToDoContentProvider extends ContentProvider {
    public static final Uri CONTENT_URI =  Uri.parse("content://com.example.provider.todolist/values");

    public static final String KEY_ID = "_id";
    public static final String KEY_TASK = "task";

    private DBHelper dbHelper;

    private static final int ALL = 1;
    private static final int SINGLE = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.example.provider.todolist", "values", ALL);
        uriMatcher.addURI("com.example.provider.todolist", "values/#", SINGLE);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext(), DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBHelper.DATABASE_TABLE);
        switch (uriMatcher.match(uri)) {
            case SINGLE:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(KEY_ID + "=" + id);
            default: break;
        }
        return queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALL:
                return "vnd.com.example.cursor.dir/todo";
            case SINGLE:
                return "vnd.com.example.cursor.item/todo";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String nullColumnHack = null;
        long id = db.insert(DBHelper.DATABASE_TABLE, nullColumnHack, values);
        if (id > -1) {
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(insertedId, null);
            return insertedId;
        }
        else
            return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SINGLE:
                String id = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + id
                    + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
            default: break;
        }
        if (selection == null)
            selection = "1";
        int deleteCount = db.delete(DBHelper.DATABASE_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SINGLE:
                String id = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + id
                    + (!TextUtils.isEmpty(selection) ?
                    " AND (" + selection + ')' : "");
            default: break;
        }
        int updateCount = db.update(DBHelper.DATABASE_TABLE,
                values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

    public static class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "todoDatabase.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_TABLE = "todoItemTable";

        private static final String DATABASE_CREATE = "create table "
                + DATABASE_TABLE + "(" + KEY_ID
                + " integer primary key autoincrement, "
                + KEY_TASK + " text not null);";

        DBHelper(Context context, String name,
                 SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
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