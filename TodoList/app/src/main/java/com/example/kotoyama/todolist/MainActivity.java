package com.example.kotoyama.todolist;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    SimpleCursorAdapter scAdapter;
    EditText addTaskEditText;
    ListView taskListView;
    DataBase dataBase;
    Cursor cursor;

    private static final int DELETE_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTaskEditText = findViewById(R.id.edit_text);
        taskListView = findViewById(R.id.taskList);

        dataBase = new DataBase(this);
        dataBase.open();
        showTasks();

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "ID: " + (position + 1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addTask(View v) {
        String itemText = addTaskEditText.getText().toString();
        dataBase.insertEntry(itemText);
        addTaskEditText.setText("");
        showTasks();
        cursor.requery();
    }

    public void showTasks() {
        cursor = dataBase.getAllEntries();
        startManagingCursor(cursor);
        String[] from = new String[] { DataBase.KEY_NAME, DataBase.KEY_ID };
        int[] to = new int[] { R.id.nameTextView, R.id.numberTextView};
        scAdapter = new SimpleCursorAdapter(this, R.layout.tasks, cursor, from, to);
        taskListView = findViewById(R.id.taskList);
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
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.removeEntry(info.id);
                cursor.requery();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBase.close();
    }
}