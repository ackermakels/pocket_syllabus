package com.example.pocketsyllabus;
import android.app.Activity;
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

public class add_assignment extends Activity {
    private EditText txtName;
    private EditText txtDue;
    private Button btnAdd;
    private SQLiteDatabase db;
    private SQLHelper helper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_assignment);

//        txtName = (EditText) findViewById(R.id.txtName);
//        txtDue = (EditText) findViewById(R.id.txtDueDate);
//        btnAdd = (Button) findViewById(R.id.btnAdd);
        Bundle bundle = getIntent().getExtras();

        btnAdd.setOnClickListener(v1 -> {
            //get user input
            String assignName = txtName.getText().toString();
            String dueDate = txtDue.getText().toString();
            String className = bundle.getString("name");
            //add new row to assignment table
            //get sqlHelper working and then use addAssignment to add assignment
            // helper.addAssignment(assignName, dueDate);

        });

    }
}