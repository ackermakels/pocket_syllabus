package com.example.pocketsyllabus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import static android.content.Intent.ACTION_VIEW;

public class Assignment extends Activity {

    // instance vars
    private String name;
    private String dueDate;

    // constructor
    public Assignment( String name, String dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    // getters
    public String getName() {
        return this.name;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    // setters
    public void setName( String name ) {
        this.name = name;
    }

    public void setDueDate( String dueDate ) {
        this.dueDate = dueDate;
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
