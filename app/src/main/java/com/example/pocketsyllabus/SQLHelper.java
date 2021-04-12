package com.example.pocketsyllabus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** Helper to the database, manages versions and creation */
public class SQLHelper extends SQLiteOpenHelper {

    // database vars
    public static final String DATABASE_NAME = "pocketsyllabus.db";
    public static final int DATABASE_VERSION = 4;

    // assignment table vars
    public static final String ASSIGNMENT_TABLE = "assignments";
    public static final String KEY_A_NAME = "name";
    public static final String KEY_A_DATE = "duedate";
    public static final String KEY_A_ID = "id integer primary key autoincrement";
    public static final String KEY_A_COURSE = "course_name";

    // create assignment table query
    public static final String CREATE_A_TABLE = "CREATE TABLE assignments("
            + KEY_A_ID + "," + KEY_A_NAME + " text,"
            + KEY_A_DATE + " text," + KEY_A_COURSE + " text" +
            ", foreign key(course_name) references courses(course_name));";

    // course table
    public static final String COURSE_TABLE = "courses";
    public static final String KEY_C_NAME = "course_name";
    public static final String KEY_PROFESSOR = "professor";
    public static final String KEY_EMAIL = "email";

    // create course table query
    public static final String CREATE_C_TABLE = "CREATE TABLE courses("
            + KEY_C_NAME + " text primary key," + KEY_PROFESSOR + " text,"
            + KEY_EMAIL + " text);";

    private ContentValues values;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // called to create table
    // NB: this is not a lifecycle method because this class is not an Activity
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("pocket syllabus", "Creating SQL Helper");

        db.execSQL("DROP TABLE IF EXISTS " + ASSIGNMENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);

        String sql = CREATE_A_TABLE;
        Log.e("pocket syllabus", "onCreate: " + sql);
        db.execSQL(sql);

        Log.e("pocket syllabus", "sql helper created");

        String sql2 = CREATE_C_TABLE;
        Log.e("pocket syllabus", "onCreate: " + sql2);
        db.execSQL(sql2);
    }

    // called when database version mismatch
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion >= newVersion) return;

        Log.d("pocket syllabus", "onUpgrade: Version = " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + ASSIGNMENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
        onCreate(db);   //not calling a lifecycle method
    }

    // add assignment to database
    public void addAssignment(String name, String date, String course) {
        SQLiteDatabase db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(KEY_A_NAME, name);
        values.put(KEY_A_DATE, date);
        values.put(KEY_A_COURSE, course);

        db.insert(ASSIGNMENT_TABLE, null, values);
        Log.d("pocket syllabus", name + " added");
    }

    // add course to database
    public void addCourse(String name, String professor, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(KEY_C_NAME, name);
        values.put(KEY_PROFESSOR, professor);
        values.put(KEY_EMAIL, email);

        db.insert(COURSE_TABLE, null, values);
        Log.d("pocket syllabus", name + " added");

        db.close();
    }

    // update assignment name in database
    public void updateAssignment(int id, String name, String duedate, String course_name){
        SQLiteDatabase db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(KEY_A_NAME, name);
        values.put(KEY_A_DATE, duedate);
        values.put(KEY_A_DATE, course_name);

        db.update(ASSIGNMENT_TABLE, values, "id=?", new String[] {String.valueOf(id)});
        Log.d("pocket syllabus", name + " updated");

        db.close();
    }

    // update Animal name in database
    public void updateCourse(String course_name, String duedate){
        SQLiteDatabase db = this.getWritableDatabase();

        values = new ContentValues();
        values.put(KEY_A_NAME, course_name);
        values.put(KEY_A_DATE, duedate);

        db.update(ASSIGNMENT_TABLE, values, "course_name=?",
                new String[] {String.valueOf(course_name)});
        Log.d("pocket syllabus", course_name + " updated");
        db.close();
    }

    // delete assignment from database
    public void deleteAssignment(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ASSIGNMENT_TABLE, "id=?", new String[] {String.valueOf(id)});

        Log.d("pocket syllabus", id + " deleted");
        db.close();
    }

    // delete course from database
    public void deleteCourse(String course_name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(COURSE_TABLE,  "course_name=?", new String[] {course_name});

        Log.d("pocket syllabus", course_name + " deleted");
        db.close();
    }

    // retrieve course data to populate list view
    public Cursor getMainActivityData() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + COURSE_TABLE;

        Cursor data = db.rawQuery(query, null);

        return data;
    }

    // get course info for a course
    public Cursor getCourseInfo( String courseName ) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM Courses WHERE course_name=" + courseName;

        Cursor data = db.rawQuery( query, null );

        return data;
    }

    // get the assignments for a course
    public Cursor getCourseAssignments( String courseName) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM Assignments WHERE course_name=" + courseName;

        Cursor data = db.rawQuery( query, null );

        return data;
    }

}


