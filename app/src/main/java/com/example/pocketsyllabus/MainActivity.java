package com.example.pocketsyllabus;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private Button button;
    private ListView courseList;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private SQLHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //course list, array adapter, array
        courseList = findViewById(R.id.listView);
        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        courseList.setAdapter(itemsAdapter);
        courseList.setOnItemClickListener(this);

        helper = new SQLHelper(this);

        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            Log.d("PocketSty", "Failed to Create DB");
        }

        helper.addCourse("CS480", "Pepe", "pepe@pepe.com");

        //create listener on button to run open Add method
        button = findViewById(R.id.button);
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OpenAddNewCourseActivity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

    private void populateListView(){
        Log.d("pocket Syllabus", "populateListView: Displaying data in the list view");

        Cursor data = helper.getData();

        items.clear();

        while (data.moveToNext()){
            items.add(data.getString(0)); //select course name, and add to listView
        }
        itemsAdapter.notifyDataSetChanged();
    }

    public void OpenAddNewCourseActivity(){
        Log.d( "pocket syllabus", "clicked add new course");

        Intent i1 = new Intent(this, AddCourse.class);
        startActivity(i1);
        Toast.makeText(this, "Opening Add New Course Page", Toast.LENGTH_SHORT).show();
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String courseName = items.get(position);
        Log.e("pocket syllabus", courseName);
    }

    //close database
    @Override
    protected void onPause() {
        super.onPause();
        if(db != null)
            db.close();
    }
}