package com.example.pocketsyllabus;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.util.ArrayList;

public class Course extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Button addButton;
    private Button editButton;
    private ListView listView;
    private ArrayList<Assignment> arrayList;
    private ArrayAdapter<Assignment> adapter;
    private SQLHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course);

        // setup assignment list
        listView = findViewById(R.id.listView);

        // connect adapter to list
        adapter = new ArrayAdapter<Assignment>(
                this,
                android.R.layout.simple_expandable_list_item_1,
                arrayList
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        // setup db
        helper = new SQLHelper(this);

        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e ) {
            Log.d("pocket syllabus", "Failed to Create DB");
        }

        // initialize buttons

    }

}
