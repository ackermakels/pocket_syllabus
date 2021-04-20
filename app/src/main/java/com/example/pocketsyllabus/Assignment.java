package com.example.pocketsyllabus;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
