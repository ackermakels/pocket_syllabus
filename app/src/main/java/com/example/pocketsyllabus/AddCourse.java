package com.example.pocketsyllabus;


import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AddCourse extends Activity {

    private EditText courseName;
    private EditText professor;
    private EditText professorEmail;
    private Button addCourse;
    private SQLiteDatabase db;
    private SQLHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_course);

        courseName = findViewById(R.id.courseNameInput);
        professor = findViewById(R.id.professorNameInput);
        professorEmail = findViewById(R.id.emailInput);
        addCourse = findViewById(R.id.AddCourse);

        helper = new SQLHelper(this);


        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { addCourse(); }
        });
    }

    // add a course
    public void addCourse(){

        String courseNameString = courseName.getText().toString();
        String professorString = professor.getText().toString();
        String emailString = professorEmail.getText().toString();

        if (courseName == null || courseName.length() == 0){

            Toast.makeText(this, "Enter a course name", Toast.LENGTH_SHORT).show();
        } else if (professor == null || professor.length() == 0){

            Toast.makeText(this, "Enter a professor name", Toast.LENGTH_SHORT).show();
        } else if (professorEmail == null || professorEmail.length() == 0){

            Toast.makeText(this, "Enter a professor email", Toast.LENGTH_SHORT).show();
        } else {
            helper.addCourse(courseNameString, professorString, emailString);
            Toast.makeText(this, "Course Added Successfully", Toast.LENGTH_SHORT).show();
            // go to course activity
            OpenCourseViewActivity();
        }


        /***
         * add course info to the Database
         *
         * update the main Activity listView with the Course Name
         */
    }
    public void OpenCourseViewActivity(){
        Log.d( "pocket syllabus", "clicked add new course");

        Intent i1 = new Intent(this, Course.class);
        startActivity(i1);
        Toast.makeText(this, "Opening Course View Page", Toast.LENGTH_SHORT).show();
    }
}
