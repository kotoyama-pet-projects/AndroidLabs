package com.example.kotoyama.contactssample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean CONTACTS_READING_PERMITTED = false;

    ListView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = findViewById(R.id.contactListView);
        int hasPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        if (hasPermission == PackageManager.PERMISSION_GRANTED)
            CONTACTS_READING_PERMITTED = true;
        else
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_CONTACTS }, REQUEST_CODE_READ_CONTACTS);
        if (CONTACTS_READING_PERMITTED)
            getContactList();
    }

    private void getContactList() {
        ArrayList<String> contacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contact = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                contacts.add(contact);
            }
            cursor.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, contacts);
        contactList.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    CONTACTS_READING_PERMITTED = true;
        }
        if (CONTACTS_READING_PERMITTED)
            getContactList();
        else
            Toast.makeText(this, "Permission required", Toast.LENGTH_LONG).show();
    }
}