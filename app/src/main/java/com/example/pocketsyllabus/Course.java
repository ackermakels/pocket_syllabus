package com.example.pocketsyllabus;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import static android.content.Intent.ACTION_VIEW;

public class Course extends AppCompatActivity implements AdapterView.OnItemClickListener, TextToSpeech.OnInitListener{

    private String courseName;
    private String professorName;
    private String professorEmail;
    private TextView courseNameView;
    private TextView professorNameView;
    private TextView professorEmailView;
    private Button addButton;
    private Button editButton;
    private Button emailButton;
    private ListView listView;
    private ArrayList<Assignment> arrayList = new ArrayList<>();
    private AssignmentAdapter adapter;
    private SQLHelper helper;
    private SQLiteDatabase db;
    private TextToSpeech speaker;
    private static final String tag = "Widgets";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course);

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);

        // use bundle data
        Intent courseIntent = getIntent();
        courseName = courseIntent.getStringExtra( "courseName" );

        // setup db
        helper = new SQLHelper( this );

        try {
            db = helper.getWritableDatabase();
        } catch ( SQLException e ) {
            Log.d("pocket syllabus", "Failed to Create DB Connection");
        }

        // setup text views
        courseNameView = findViewById( R.id.courseName );
        professorNameView = findViewById( R.id.professorName );
        professorEmailView = findViewById( R.id.professorEmail );

        // setup assignment list
        listView = findViewById( R.id.listView );

        // connect adapter to list
        adapter = new AssignmentAdapter(
                this,
                arrayList
        );

        listView.setAdapter( adapter );
        listView.setOnItemClickListener( this );

        populateViewData();

        // initialize buttons and respective listener callbacks
        addButton = findViewById( R.id.addAssignmentButton );
        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                addButtonHandler();
            }
        });

        editButton = findViewById( R.id.editCourseButton );
        editButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                editButtonHandler();
            }
        });

        emailButton = findViewById( R.id.emailButton);
        emailButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) { emailButtonHandler(); }
        });

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

        //text to speech
        speak("Overview of " + courseName);
    }

    //speak methods will send text to be spoken
    public void speak(String output){
        speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null, "Id 0");
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

    public void populateViewData() {
        // get course data
        Cursor courseData = helper.getCourseInfo( courseName );
        courseData.moveToNext();
        professorName = courseData.getString(1);
        professorEmail = courseData.getString(2);
        System.out.println(professorEmail);

        // get course assignments
        Cursor assignmentsData = helper.getCourseAssignments( courseName );

        while( assignmentsData.moveToNext() ) {
            Assignment newAssignment = new Assignment(
                    assignmentsData.getString(1),
                    assignmentsData.getString(2)
            );

            arrayList.add(newAssignment);
        }

        // update list
        adapter.notifyDataSetChanged();

        // set fields
        courseNameView.setText( courseName );
        professorNameView.setText( professorName );
        professorEmailView.setText( professorEmail );
    }

    public void editButtonHandler() {
        // get course data
        Cursor courseData = helper.getCourseInfo( courseName );
        courseData.moveToNext();
        professorName = courseData.getString(1);
        professorEmail = courseData.getString(2);

        // create intent for edit course activity
        Intent addAssignmentIntent = new Intent( getApplicationContext(), AddCourse.class );

        // create bundle
        Bundle assignmentBundle = new Bundle();
        assignmentBundle.putString( "courseName", courseName );
        assignmentBundle.putString( "professorName", professorName );
        assignmentBundle.putString( "professorEmail", professorEmail );

        // start add course activity
        startActivity( addAssignmentIntent );
    }

    public void addButtonHandler() {
        // create intent for add assignment activity
        Intent addAssignmentIntent = new Intent( getApplicationContext(), AddAssignment.class );

        //pass course name to intent
        addAssignmentIntent.putExtra("course", courseName);

        // start add assignment activity
        startActivity( addAssignmentIntent );
    }

    public void emailButtonHandler() {
        Intent emailIntent = new Intent( Intent.ACTION_SENDTO, Uri.parse( "mailto:" ) );

        emailIntent.putExtra( Intent.EXTRA_EMAIL, new String[] { professorEmail } );

        if( emailIntent.resolveActivity( getPackageManager() ) != null ) {
            startActivity( emailIntent );
        }
    }


    public void onItemClick( AdapterView<?> parent, View v, int position, long id ) {
        return;
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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blackboard.bentley.edu/"));
                startActivity(browserIntent);
                return true;



            case R.id.item3:

                startMap();
                return true;

            case R.id.item4:

                finish();
                System.exit(0);


            default:
                return super.onOptionsItemSelected(item);

        }


    }

    private void startMap() {
        Uri uri2 = Uri.parse("geo:0,0?q=175+forest+street+waltham+ma");
        Intent i2 = new Intent(ACTION_VIEW, uri2);

        if (i2.resolveActivity(getPackageManager()) != null) {
            startActivity(i2);
        }

    }

    private void returnToMain() {

        Intent i1 = new Intent(this, MainActivity.class);
        startActivity(i1);
    }
}

