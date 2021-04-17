package com.example.pocketsyllabus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    private Button button;
    private ListView courseList;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private SQLHelper helper;
    private SQLiteDatabase db;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder = null;
    private String CHANNEL_ID = "01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // course list, array adapter, array
        courseList = findViewById(R.id.listView);
        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        courseList.setAdapter(itemsAdapter);
        courseList.setOnItemClickListener(this);

        // setup db
        helper = new SQLHelper(this);

        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            Log.d("pocket syllabus", "Failed to Create DB");
        }

        // add course ( for testing )
        helper.addCourse("CS480", "Pepe", "pepe@pepe.com" );
        helper.addAssignment("Assignment 1", "01/02/2020", "CS480" );
        helper.addAssignment("Assignment 1", "04/18/2021", "CS480" );

        //create listener on button to run open Add method
        button = findViewById(R.id.button);
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OpenAddNewCourseActivity();
            }
        });

        // setup nofications for assignments upcoming within a week
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        sendAssignmentNotification();
    }

    // populate fields on return from another activity
    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

    // close database
    @Override
    protected void onPause() {
        super.onPause();
        if(db != null)
            db.close();
    }

    private void populateListView(){
        Log.d("pocket syllabus", "populateListView: Displaying data in the list view");

        Cursor data = helper.getCourses();

        items.clear();

        while (data.moveToNext()){
            items.add(data.getString(0)); //select course name, and add to listView
        }
        itemsAdapter.notifyDataSetChanged();
    }

    public void OpenAddNewCourseActivity(){
        Log.d( "pocket syllabus", "clicked add new course");

        Intent i1 = new Intent(this, AddCourse.class);
        startActivity(i1);
        Toast.makeText(this, "Opening Add New Course Page", Toast.LENGTH_SHORT).show();
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String courseName = items.get(position);

        // create intent for course activity
        Intent courseIntent = new Intent( this, Course.class );

        // create bundle for course activity
        Bundle courseBundle = new Bundle();
        courseBundle.putString( "courseName", courseName );

        // add bundle to intent
        courseIntent.putExtras( courseBundle );

        // launch course activity
        startActivity( courseIntent );
    }

    public void createNotificationChannel() {
        // only start it build version is api 26 or later
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            // create channel
            CharSequence channelName = "assignment notifications";
            String channelDescription = "notify user if an assignment is due within the next week";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    channelName,
                    importance
            );

            channel.setDescription( channelDescription );

            NotificationManager notificationManager = getSystemService( NotificationManager.class );
            notificationManager.createNotificationChannel( channel );
        }
    }

    public void sendAssignmentNotification() {
       ArrayList<Assignment> dueAssignments = findDueAssignments();



    }

    public ArrayList<Assignment> findDueAssignments() {
        ArrayList<Assignment> assignmentList = new ArrayList<>();
        Date currentTime = Calendar.getInstance().getTime();

        // used to find day of year
        int[] monthDays = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30 };

        int todayDate  = currentTime.getDate();
        int todayMonth = currentTime.getMonth();
        int todayYear  = currentTime.getYear();

        // handle leap year
        if ( ( todayYear % 4 ) == 0 ) {
            monthDays[ 2 ] = 29;
        }

        Cursor data = helper.getAssignments();

        String date;
        String name;

        while (data.moveToNext()){
            try {
                name = data.getString(1);
                date = data.getString(2);

                String[] assignmentDateArray = date.split("/");

                System.out.println(" shit is about to go down ");

                int assignmentMonth = Integer.parseInt(assignmentDateArray[0]);
                int assignmentDate  = Integer.parseInt(assignmentDateArray[1]);
                int assignmentYear  = Integer.parseInt(assignmentDateArray[2]);

                System.out.println(" shit probably went down ");

                if (todayYear == assignmentYear) {

                    int currentNum = 0;
                    for (int i = 0; i < todayMonth; i++) {
                        currentNum += monthDays[i];
                    }

                    currentNum += todayDate;

                    int assignmentNum = 0;
                    for (int i = 0; i < assignmentMonth; i++) {
                        assignmentNum += monthDays[i];
                    }

                    assignmentNum += assignmentDate;

                    if (currentNum >= (assignmentNum - 7)) {
                        assignmentList.add(new Assignment(name, date));
                    }
                }

                assignmentList.add(new Assignment(name, date));

            } catch ( Exception e ) {};
        }

        for ( Assignment assignment : assignmentList ) {
            System.out.println( assignment.getDueDate() );
        }

        return assignmentList;
    }
}