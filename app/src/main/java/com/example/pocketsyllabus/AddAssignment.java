package com.example.pocketsyllabus;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.database.*;
import android.database.sqlite.*;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.Locale;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import androidx.appcompat.app.AppCompatActivity;

public class AddAssignment extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private EditText txtName;
    private EditText txtDue;
    private Button btnAdd;
    private SQLiteDatabase db;
    private SQLHelper helper;
    private TextToSpeech speaker;
    private static final String tag = "Widgets";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_assignment);
        helper = new SQLHelper(this);
        txtName = (EditText) findViewById(R.id.txtName);
        txtDue = (EditText) findViewById(R.id.txtDueDate);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);

        btnAdd.setOnClickListener(v1 -> {
            //get user input
            String assignName = txtName.getText().toString();
            String dueDate = txtDue.getText().toString();
            // get course name from extras sent when running intent in course activity
            String courseName = getIntent().getStringExtra("course");

            if(assignName.length() == 0 || dueDate.length() == 0){
                Toast.makeText(this, "Enter both the Assignment Name and Due Date", Toast.LENGTH_SHORT).show();
                speak("Please make sure to enter both the assignment name and due date.");
            }
            else {
                //add new row to assignment table
                helper.addAssignment(assignName, dueDate, courseName);
                //make a toast to screen and have speech to say it was added
                Toast.makeText(this, assignName+" due on "+dueDate+" added.", Toast.LENGTH_SHORT).show();
                OpenCourseViewActivity();
            }

        });


    }
    //speak methods will send text to be spoken
    public void speak(String output){
        speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null, "Id 0");
    }

    // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // If a language is not be available, the result will indicate it.
            int result = speaker.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language data is missing or the language is not supported.
                Log.e(tag, "Language is not available.");
            } else {
                // The TTS engine has been successfully initialized
                Log.i(tag, "TTS Initialization successful.");
            }
        } else {
            // Initialization failed.
            Log.e(tag, "Could not initialize TextToSpeech.");
        }

    }

    // on destroy
    public void onDestroy(){
        // shut down TTS engine
        if(speaker != null){
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }

    public void OpenCourseViewActivity(){
        Intent i1 = new Intent(this, Course.class);
        startActivity(i1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:

                returnToMain();
                return true;
            case R.id.item2:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blackboard.bentley.edu/"));
                startActivity(browserIntent);

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void returnToMain() {

        Intent i1 = new Intent(this, MainActivity.class);
        startActivity(i1);
    }
}