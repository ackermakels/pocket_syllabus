package com.example.pocketsyllabus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.net.Uri;
import static android.content.Intent.ACTION_VIEW;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private Animation bounce;
    private Button button;
    private ListView courseList;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private SQLHelper helper;
    private SQLiteDatabase db;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder = null;
    private String CHANNEL_ID = "01";
    private int NOTIFICATION_ID = 1;


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

        //create listener on button to run open Add method
        button = findViewById(R.id.button);
        button.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                button.startAnimation( bounce );
            }
        });

        // setup notifications for assignments upcoming within a week
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        // setup animation for add course button
        bounce = AnimationUtils.loadAnimation( getApplicationContext(), R.anim.bounce);

        bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { return; }

            @Override
            public void onAnimationEnd(Animation animation) { OpenAddNewCourseActivity(); }

            @Override
            public void onAnimationRepeat(Animation animation) { return; }
        });
    }

    // populate fields on return from another activity
    @Override
    public void onResume() {
        super.onResume();
        populateListView();
        sendAssignmentNotification();
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

        if ( dueAssignments.size() < 1 ) return;

        String dueAssignmentString = "Assignment due in the next week: \n";
        for ( Assignment assignment : dueAssignments ) {
            dueAssignmentString += assignment.getName() + ", \n";
        }

        // Create an explicit intent for an Activity
        Intent intent = new Intent(this, MainActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        //the pending intent will outlive this app
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 9, intent,0 );

        builder = new NotificationCompat.Builder(this, CHANNEL_ID )
                .setSmallIcon( R.drawable.ic_launcher_foreground )
                .setContentTitle( "Upcoming Due Assignments" )
                .setContentText( dueAssignmentString )
                .setPriority( NotificationCompat.PRIORITY_DEFAULT )
                // Set the intent that will fire when the user taps the notification
                .setContentIntent( pendingIntent )
                .setAutoCancel( true );

        builder.build();

        notificationManager.notify( NOTIFICATION_ID, builder.build() );
    }

    public ArrayList<Assignment> findDueAssignments() {
        ArrayList<Assignment> assignmentList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // used to find day of year
        int[] monthDays = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30 };

        int todayDate  = calendar.get( Calendar.DAY_OF_MONTH );
        int todayMonth = calendar.get( Calendar.MONTH ) + 1; // month starts at 0 (ugh)
        int todayYear  = calendar.get( Calendar.YEAR );

        // handle leap year
        if ( ( todayYear % 4 ) == 0 ) {
            monthDays[ 2 ] = 29;
        }

        // get assignment date from db
        Cursor data = helper.getAssignments();

        String date;
        String name;

        while ( data.moveToNext() ){
            try {
                name = data.getString(1 );
                date = data.getString(2 );

                String[] assignmentDateArray = date.split("/");

                int assignmentMonth = Integer.parseInt( assignmentDateArray[0] );
                int assignmentDate  = Integer.parseInt( assignmentDateArray[1] );
                int assignmentYear  = Integer.parseInt( assignmentDateArray[2] );

                if ( todayYear == assignmentYear ) {

                    // calculate days of year
                    int currentNum = 0;
                    for (int i = 0; i < todayMonth; i++) {
                        currentNum += monthDays[i];
                    }

                    currentNum += todayDate;

                    // calculate days of year
                    int assignmentNum = 0;
                    for (int i = 0; i < assignmentMonth; i++) {
                        assignmentNum += monthDays[i];
                    }

                    assignmentNum += assignmentDate;

                    if ( currentNum >= (assignmentNum - 7) && assignmentNum > currentNum ) {
                        // if in next 7 days add to due assignments list
                        assignmentList.add( new Assignment( name, date ) );
                    }
                }

            } catch ( Exception e ) {
                // may fail if invalid date input
                System.out.println( e.getMessage() );
            };
        }

        return assignmentList;
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
}