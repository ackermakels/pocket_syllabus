package com.example.pocketsyllabus;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Course extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String courseName;
    private String professorName;
    private String professorEmail;
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

        // use bundle data
        Intent courseIntent = getIntent();
        courseName = courseIntent.getStringExtra( "courseName" );

        // setup db
        helper = new SQLHelper( this );

        try {
            db = helper.getWritableDatabase();
        } catch ( SQLException e ) {
            Log.d("pocket syllabus", "Failed to Create DB Connection");
        }

        // get course data
        Cursor courseData = helper.getCourseInfo( courseName );
        courseData.moveToNext();
        professorName = courseData.getString(1);
        professorEmail = courseData.getString(2);
        System.out.println(professorEmail);

        // get course assignments
        Cursor assignmentsData = helper.getCourseAssignments( courseName );

        arrayList = new ArrayList<Assignment>();

        while( assignmentsData.moveToNext() ) {
            Assignment newAssignment = new Assignment(
                    assignmentsData.getString(2),
                    assignmentsData.getString(3)
            );

            arrayList.add(newAssignment);
        }

        // setup assignment list
        listView = findViewById( R.id.listView );

        // connect adapter to list
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                arrayList
        );

        listView.setAdapter( adapter );
        System.out.println("here");
        listView.setOnItemClickListener( this );

        // initialize buttons and respective listener callbacks
        addButton = findViewById( R.id.addAssignmentButton );
        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                // create intent for add assignment activity
                 Intent addAssignmentIntent = new Intent( getApplicationContext(), AddAssignment.class );

                // create bundle
                Bundle assignmentBundle = new Bundle();
                assignmentBundle.putString( "course", courseName );

                // launch add assignment activity
                startActivity( addAssignmentIntent );
            }
        });

        editButton = findViewById( R.id.editCourseButton );
        editButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                // create intent for edit course activity
                Intent addAssignmentIntent = new Intent( getApplicationContext(), AddCourse.class );

                // create bundle
                Bundle assignmentBundle = new Bundle();
                assignmentBundle.putString( "courseName", courseName );
                assignmentBundle.putString( "professorName", professorName );
                assignmentBundle.putString( "professorEmail", professorEmail );

                // start add course activity
                startActivity( addAssignmentIntent );
            }
        });
    }

    public void onItemClick( AdapterView<?> parent, View v, int position, long id ) {
        return;
    }
}
