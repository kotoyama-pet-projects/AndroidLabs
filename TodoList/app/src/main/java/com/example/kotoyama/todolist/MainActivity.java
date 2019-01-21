package com.example.kotoyama.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    SimpleCursorAdapter scAdapter;
    EditText addTaskEditText;
    ListView taskListView;

    private static final int DELETE_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addTaskEditText = findViewById(R.id.edit_text);
        taskListView = findViewById(R.id.taskList);
        showTasks();
    }

    public void addTask(View v) {
        String itemText = addTaskEditText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(ToDoContentProvider.KEY_TASK, itemText);
        addTaskEditText.setText("");
        Uri uri = ToDoContentProvider.CONTENT_URI;
        getApplicationContext().getContentResolver().insert(uri, values);
        showTasks();
    }

    public void showTasks() {
        Uri uri = ToDoContentProvider.CONTENT_URI;
        Cursor cursor = this.getContentResolver().query(uri,null,null,null,null);
        scAdapter = new SimpleCursorAdapter(
                this,
                R.layout.tasks,
                cursor,
                new String[]{ ToDoContentProvider.KEY_TASK, ToDoContentProvider.KEY_ID },
                new int[] { R.id.nameTextView, R.id.numberTextView},
                0
        );
        taskListView.setAdapter(scAdapter);
        registerForContextMenu(taskListView);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Uri uri = ToDoContentProvider.CONTENT_URI;
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                getContentResolver().delete(uri,
                        ToDoContentProvider.KEY_ID + "=?",
                        new String[]{ String.valueOf(info.id) });
                showTasks();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}