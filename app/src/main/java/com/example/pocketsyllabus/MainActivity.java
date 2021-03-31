package com.example.pocketsyllabus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        helper = new SQLHelper(this);

        try {
            db = helper.getWritableDatabase9):
        } catch
    }
}