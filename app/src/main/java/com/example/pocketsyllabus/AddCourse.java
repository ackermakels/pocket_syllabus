package com.example.pocketsyllabus;


import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Locale;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class AddCourse extends Activity implements TextToSpeech.OnInitListener {

    private EditText courseName;
    private EditText professor;
    private EditText professorEmail;
    private Button addCourse;
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

        helper = new SQLHelper(this);

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { addCourse(); }
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

    // add a course
    public void addCourse(){

        String courseNameString = courseName.getText().toString();
        String professorString = professor.getText().toString();
        String emailString = professorEmail.getText().toString();

        if (courseName == null || courseName.length() == 0){

            Toast.makeText(this, "Enter a course name", Toast.LENGTH_SHORT).show();
            speak("Please enter a course name.");
        } else if (professor == null || professor.length() == 0){

            Toast.makeText(this, "Enter a professor name", Toast.LENGTH_SHORT).show();
            speak("Please enter a professor name.");
        } else if (professorEmail == null || professorEmail.length() == 0){

            Toast.makeText(this, "Enter a professor email", Toast.LENGTH_SHORT).show();
            speak("Please enter a professor email.");
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
