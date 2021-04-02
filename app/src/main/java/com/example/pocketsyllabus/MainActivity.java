package com.example.pocketsyllabus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;
import android.widget.TextView;
import android.content.ContentValues;
import android.text.method.ScrollingMovementMethod;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private SQLHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new SQLHelper(this);

        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e("PocketSty", "Failed to Create DB");
        }


        helper.addAssignment("A1", "1/1/2020", "CS480");
    }

    public void OpenAddNewCourseActivity(){

        Intent i1 = new Intent(this, AddCourse.class);
        startActivity(i1);
        Toast.makeText(this, "Opening Add New Course Page", Toast.LENGTH_SHORT).show();
    }

    //close database
    @Override
    protected void onPause() {
        super.onPause();
        if(db != null)
            db.close();
    }
}