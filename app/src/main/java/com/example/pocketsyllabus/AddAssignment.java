package com.example.pocketsyllabus;

import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.database.sqlite.*;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Locale;
import java.util.Objects;
import androidx.appcompat.app.AppCompatActivity;
import static android.content.Intent.ACTION_VIEW;

public class AddAssignment extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Animation shake;
    private EditText txtName;
    private EditText txtDue;
    private Button btnAdd;
    private SQLiteDatabase db;
    private SQLHelper helper;
    private TextToSpeech speaker;
    private static final String tag = "Widgets";
    private String assignmentName;
    private String assignmentDueDate;
    private String courseName;
    private boolean update = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_assignment);

        txtName = findViewById(R.id.txtName);
        txtDue = findViewById(R.id.txtDueDate);
        btnAdd = findViewById(R.id.btnAdd);

        helper = new SQLHelper(this);

        try {
            db = helper.getWritableDatabase();
        } catch ( SQLException e ) {
            Log.d("pocket syllabus", "Failed to Create DB Connection");
        }

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);

        courseName = getIntent().getStringExtra("course");

        // attempt get assignment info ( for edit )
        Intent editIntent = getIntent();
        assignmentName = editIntent.getStringExtra( "assignmentName" );
        assignmentDueDate = editIntent.getStringExtra( "assignmentDueDate" );

        if ( Objects.isNull(assignmentName) ) {
            update = false;
        } else {
            // set assignment inputs
            txtName.setText( assignmentName );
            txtDue.setText( assignmentDueDate );
        }

        // setup shake animation
        shake = AnimationUtils.loadAnimation( getApplicationContext(), R.anim.shake );

        btnAdd.setOnClickListener(v1 -> {
            //get user input
            String newAssignmentName = txtName.getText().toString();
            String dueDate = txtDue.getText().toString();
            // get course name from extras sent when running intent in course activity
            courseName = getIntent().getStringExtra("courseName");

            if(newAssignmentName.length() == 0 || dueDate.length() == 0) {
                btnAdd.startAnimation( shake );
                Toast.makeText(this, "Enter both the Assignment Name and Due Date",
                        Toast.LENGTH_SHORT).show();
                speak("Please make sure to enter both the assignment name and due date.");
            } else {
                // if editing assignment update it
                if ( update ) {
                    helper.updateAssignment(assignmentName, newAssignmentName, dueDate, courseName);
                } else {
                    helper.addAssignment(newAssignmentName, dueDate, courseName);
                }
                //add new row to assignment table

                //make a toast to screen and have speech to say it was added
                Toast.makeText(this, newAssignmentName + " due on " + dueDate + " added.", Toast.LENGTH_SHORT).show();
                openCourseViewActivity();
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

    public void openCourseViewActivity(){
        // create intent
        Intent courseIntent = new Intent(this, Course.class);

        // create bundle
        Bundle courseBundle = new Bundle();
        courseBundle.putString( "courseName", courseName );

        // add bundle to intent
        courseIntent.putExtras( courseBundle );

        // launch activity
        startActivity(courseIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                returnToMain();
                return true;

            case R.id.item2:
                startWeb();
                return true;

            case R.id.item3:
                startMap();
                return true;

            case R.id.item4:
                System.exit(0);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startWeb() {
        Uri blackboardURI = Uri.parse("https://blackboard.bentley.edu/");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, blackboardURI );

        if (webIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(webIntent);
        } else {
            Toast.makeText(this, "Google Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMap() {
        Uri bentleyURI = Uri.parse("geo:0,0?q=175+forest+street+waltham+ma");
        Intent mapsIntent = new Intent(ACTION_VIEW, bentleyURI);

        if (mapsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapsIntent);
        } else {
            Toast.makeText(this, "Google Maps Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void returnToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);

        startActivity(mainIntent);
    }
}