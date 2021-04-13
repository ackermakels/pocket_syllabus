package com.example.pocketsyllabus;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;
import android.widget.TextView;
import android.content.ContentValues;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddAssignment extends Activity {
    private EditText txtName;
    private EditText txtDue;
    private Button btnAdd;
    private SQLiteDatabase db;
    private SQLHelper helper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_assignment);
        helper = new SQLHelper(this);
        txtName = (EditText) findViewById(R.id.txtName);
        txtDue = (EditText) findViewById(R.id.txtDueDate);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        //Bundle bundle = getIntent().getExtras();

        btnAdd.setOnClickListener(v1 -> {
            //get user input
            String assignName = txtName.getText().toString();
            String dueDate = txtDue.getText().toString();
            // use bundle data
           // String courseName = bundle.getString("course");
            String courseName = getIntent().getStringExtra("course");

            if(assignName.length() == 0 || dueDate.length() == 0){
                Toast.makeText(this, "Enter both the Assignment Name and Due Date", Toast.LENGTH_SHORT).show();
            }
            else {
                //add new row to assignment table
                //get sqlHelper working and then use addAssignment to add assignment
                Toast.makeText(this, courseName+" "+assignName+" "+dueDate, Toast.LENGTH_SHORT).show();
                helper.addAssignment(assignName, dueDate, courseName);
            }

        });

    }
}