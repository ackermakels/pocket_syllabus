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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import static android.content.Intent.ACTION_VIEW;

public class Course extends AppCompatActivity implements AdapterView.OnItemClickListener, TextToSpeech.OnInitListener {

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

        // setup sqlhelper and db
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

        // set listener for assignments in list view
        listView.setAdapter( adapter );
        listView.setOnItemClickListener( this );

        // fills in assignments
        populateViewData();

        // initialize buttons and respective listener callbacks
        addButton = findViewById( R.id.addAssignmentButton );
        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                addButtonHandler();
            }
        });

        // edit button setup
        editButton = findViewById( R.id.editCourseButton );
        editButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                editButtonHandler();
            }
        });

        // email button setup
        emailButton = findViewById( R.id.emailButton );
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

    //method ran when edit button is clicked
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

        // add bundle to intent
        addAssignmentIntent.putExtras( assignmentBundle );

        // start add course activity
        startActivity( addAssignmentIntent );
    }

    //method ran when add button is clicked
    public void addButtonHandler() {
        // create intent for add assignment activity
        Intent addAssignmentIntent = new Intent( getApplicationContext(), AddAssignment.class );

        //pass course name to intent
        addAssignmentIntent.putExtra("courseName", courseName);

        // start add assignment activity
        startActivity( addAssignmentIntent );
    }

    //method ran when email button is clicked
    public void emailButtonHandler() {
        Intent emailIntent = new Intent( Intent.ACTION_SENDTO, Uri.parse( "mailto:" ) );

        emailIntent.putExtra( Intent.EXTRA_EMAIL, new String[] { professorEmail } );
        startActivity( emailIntent );
        /**if( emailIntent.resolveActivity( getPackageManager() ) != null ) {

            Toast.makeText(this, "Trying to email", Toast.LENGTH_LONG).show();
        }**/
    }

    // responds to clicks on assignments
    public void onItemClick( AdapterView<?> parent, View v, int position, long id ) {
        Assignment assignment = arrayList.get(position);

        // create intent for add assignment activity
        Intent assignmentIntent = new Intent( this, AddAssignment.class );

        // create bundle
        Bundle assignmentBundle = new Bundle();
        assignmentBundle.putString( "courseName", courseName );
        assignmentBundle.putString( "assignmentName", assignment.getName() );
        assignmentBundle.putString( "assignmentDueDate", assignment.getDueDate() );

        // add bundle to intent
        assignmentIntent.putExtras( assignmentBundle );

        startActivity( assignmentIntent );
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
    //opens website on web
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
    //returns to mainactivty
    private void returnToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);

        startActivity(mainIntent);
    }
}

