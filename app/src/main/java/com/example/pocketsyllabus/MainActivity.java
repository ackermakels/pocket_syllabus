package com.example.pocketsyllabus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new SQLHelper(this);

        try {
            db = helper.getWritableDatabase
        } catch

    }

    public void OpenAddNewCourseActivity(){

        Intent i1 = new Intent(this, AddCourse.class);
        startActivity(i1);
        Toast.makeText(this, "Opening Add New Course Page", Toast.LENGTH_SHORT).show();
    }
}