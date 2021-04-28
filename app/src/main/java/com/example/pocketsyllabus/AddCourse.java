package com.example.pocketsyllabus;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.Objects;
import static android.content.Intent.ACTION_VIEW;

public class AddCourse extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private Animation shake;
    private EditText courseName;
    private EditText professor;
    private EditText professorEmail;
    private TextView title;
    private String courseNameString;
    private String professorString;
    private String professorEmailString;
    private boolean update = true;
    private Button addCourse;
    private Button deleteCourse;
    private SQLiteDatabase db;
    private SQLHelper helper;
    private TextToSpeech speaker;
    private static final String tag = "Widgets";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_course);

        courseName = findViewById(R.id.courseNameInput);
        professor = findViewById(R.id.professorNameInput);
        professorEmail = findViewById(R.id.emailInput);
        addCourse = findViewById(R.id.AddCourse);
        deleteCourse = findViewById(R.id.DeleteCourse);
        title = findViewById( R.id.custom );
        //initialize sql helper
        helper = new SQLHelper(this);

        try {
            db = helper.getWritableDatabase();
        } catch ( SQLException e ) {
            Log.d("pocket syllabus", "Failed to Create DB Connection");
        }

        // attempt get course info ( for edit )
        Intent editIntent = getIntent();
        courseNameString = editIntent.getStringExtra( "courseName" );
        professorString = editIntent.getStringExtra( "professorName" );
        professorEmailString = editIntent.getStringExtra( "professorEmail" );
        // if course info was passed, set screen for update
        if ( Objects.isNull(courseNameString) ) {
            update = false;
        } else {
            // set title to edit
            title.setText( "Edit Course" );

            // set course inputs
            courseName.setText(courseNameString);
            professor.setText(professorString);
            professorEmail.setText(professorEmailString);
        };

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);
        //if updating, use editcourse method, else use addcourse method
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( update ) {
                    editCourse();
                } else {
                    addCourse();
                }
            }
        });
        //deletes course and return to mainactivity
        deleteCourse.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCourse();
                returnToMain();
            }
        });

        // setup animation for add button
        shake = AnimationUtils.loadAnimation( getApplicationContext(), R.anim.shake );
    }

    //speak methods will send text to be spoken
    public void speak(String output) {
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
    public void onDestroy() {
        // shut down TTS engine
        if(speaker != null){
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }
    //method to add a course
    public void addCourse() {
        String courseNameString = courseName.getText().toString();
        String professorString = professor.getText().toString();
        String emailString = professorEmail.getText().toString();
        //check to make each field is filled in and if it isn't, add button will shake, tts, and toast will appear on screen
        if (courseName == null || courseName.length() == 0){
            addCourse.startAnimation( shake );
            Toast.makeText(this, "Enter a course name", Toast.LENGTH_SHORT).show();
            speak("Please enter a course name.");
        } else if (professor == null || professor.length() == 0){
            addCourse.startAnimation( shake );
            Toast.makeText(this, "Enter a professor name", Toast.LENGTH_SHORT).show();
            speak("Please enter a professor name.");
        } else if (professorEmail == null || professorEmail.length() == 0){
            addCourse.startAnimation( shake );
            Toast.makeText(this, "Enter a professor email", Toast.LENGTH_SHORT).show();
            speak("Please enter a professor email.");
        } else {
            // add course to course table
            helper.addCourse(courseNameString, professorString, emailString);
            Toast.makeText(this, "Course Added Successfully", Toast.LENGTH_SHORT).show();
            // go to course activity
            openCourseViewActivity();
        }
    }
    //method to update a course
    public void editCourse() {
        String newCourseName = courseName.getText().toString();
        String professorString = professor.getText().toString();
        String emailString = professorEmail.getText().toString();
        //check to make each field is filled in and if it isn't, add button will shake, tts, and toast will appear on screen
        if (courseName == null || courseName.length() == 0){
            addCourse.startAnimation( shake );
            Toast.makeText(this, "Enter a course name", Toast.LENGTH_SHORT).show();
            speak("Please enter a course name.");

        } else if (professor == null || professor.length() == 0){
            addCourse.startAnimation( shake );
            Toast.makeText(this, "Enter a professor name", Toast.LENGTH_SHORT).show();
            speak("Please enter a professor name.");

        } else if (professorEmail == null || professorEmail.length() == 0){
            addCourse.startAnimation( shake );
            Toast.makeText(this, "Enter a professor email", Toast.LENGTH_SHORT).show();
            speak("Please enter a professor email.");

        } else {
            //update course in course table
            helper.updateCourse( courseNameString, newCourseName, professorString, emailString);
            Toast.makeText(this, "Course Added Successfully", Toast.LENGTH_SHORT).show();
            // go to course activity
            openCourseViewActivity();
        }
    }
    //method to delete a course
    public void deleteCourse() {
        helper.deleteCourse( courseNameString );
    }
    //method to create intent to go back to course activity and pass needed data
    public void openCourseViewActivity(){
        // get new course name
        String newCourseName = courseName.getText().toString();

        // create intent
        Intent courseIntent = new Intent(this, Course.class);

        // create bundle
        Bundle courseBundle = new Bundle();
        courseBundle.putString( "courseName", newCourseName );

        // add bundle to intent
        courseIntent.putExtras( courseBundle );

        // start activity
        startActivity( courseIntent );
    }
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    //menu
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //opens website in web
    private void startWeb() {
        Uri blackboardURI = Uri.parse("https://blackboard.bentley.edu/");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, blackboardURI );

        startActivity(webIntent);
        // only start google if has google
        /**if (webIntent.resolveActivity(getPackageManager()) != null) {

         } else {
         Toast.makeText(this, "Google Not Found", Toast.LENGTH_SHORT).show();
         }**/
    }
    //opens google maps
    private void startMap() {
        Uri bentleyURI = Uri.parse("geo:0,0?q=175+forest+street+waltham+ma");
        Intent mapsIntent = new Intent(ACTION_VIEW, bentleyURI);

        startActivity(mapsIntent);
        // only start maps if have google maps
        /**
         if (mapsIntent.resolveActivity(getPackageManager()) != null) {

         } else {
         Toast.makeText(this, "Google Maps Not Found", Toast.LENGTH_SHORT).show();
         }**/
    }
    //goes to mainactivity
    private void returnToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);

        startActivity(mainIntent);
    }
}
