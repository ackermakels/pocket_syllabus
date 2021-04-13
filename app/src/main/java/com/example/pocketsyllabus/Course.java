package com.example.pocketsyllabus;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Course extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String courseName;
    private String professorName;
    private String professorEmail;
    private TextView courseNameView;
    private TextView professorNameView;
    private TextView professorEmailView;
    private Button addButton;
    private Button editButton;
    private ListView listView;
    private ArrayList<Assignment> arrayList = new ArrayList<>();
    private AssignmentAdapter adapter;
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

        // setup text views
        courseNameView = findViewById( R.id.courseName );
        professorNameView = findViewById( R.id.professorName );
        professorEmailView = findViewById( R.id.professorEmail );

        // setup assignment list
        listView = findViewById( R.id.listView );

        // connect adapter to list
        adapter = new AssignmentAdapter(
                this,
                arrayList
        );

        listView.setAdapter( adapter );
        listView.setOnItemClickListener( this );

        populateViewData();

        // initialize buttons and respective listener callbacks
        addButton = findViewById( R.id.addAssignmentButton );
        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                addButtonHandler();
            }
        });

        editButton = findViewById( R.id.editCourseButton );
        editButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                editButtonHandler();
            }
        });
    }

    public void populateViewData() {
        // get course data
        Cursor courseData = helper.getCourseInfo( courseName );
        courseData.moveToNext();
        professorName = courseData.getString(1);
        professorEmail = courseData.getString(2);
        System.out.println(professorEmail);

        // get course assignments
        Cursor assignmentsData = helper.getCourseAssignments( courseName );

        while( assignmentsData.moveToNext() ) {
            Assignment newAssignment = new Assignment(
                    assignmentsData.getString(1),
                    assignmentsData.getString(2)
            );

            arrayList.add(newAssignment);
        }

        // update list
        adapter.notifyDataSetChanged();

        // set fields
        courseNameView.setText( courseName );
        professorNameView.setText( professorName );
        professorEmailView.setText( professorEmail );
    }

    public void editButtonHandler() {
        // get course data
        Cursor courseData = helper.getCourseInfo( courseName );
        courseData.moveToNext();
        professorName = courseData.getString(1);
        professorEmail = courseData.getString(2);
        System.out.println(professorEmail);

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

    public void addButtonHandler() {
        // create intent for add assignment activity
        Intent addAssignmentIntent = new Intent( getApplicationContext(), AddAssignment.class );

        // create bundle
        Bundle assignmentBundle = new Bundle();
        assignmentBundle.putString( "course", courseName );

        // start add assignment activity
        startActivity( addAssignmentIntent );
    }

    public void onItemClick( AdapterView<?> parent, View v, int position, long id ) {
        return;
    }
}
