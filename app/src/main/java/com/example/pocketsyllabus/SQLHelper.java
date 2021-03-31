package com.example.pocketsyllabus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

/** Helper to the database, manages versions and creation */
public class SQLHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pocketsyllabus.db";
    public static final int DATABASE_VERSION = 4;
    public static final String TABLE_NAME = "assignments";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "duedate";
    public static final String KEY_ID = "id integer primary key autoincrement";
    public static final String CREATE_TABLE = "CREATE TABLE assignments ("
            + KEY_ID + "," + KEY_NAME + " text,"
            + KEY_DATE + " integer);";

    private ContentValues values;
    //private ArrayList<Assignments> animalList;
    private Cursor cursor;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //called to create table
    //NB: this is not a lifecycle method because this class is not an Activity
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = CREATE_TABLE;
        Log.d("SQLiteDemo", "onCreate: " + sql);
        db.execSQL(sql);
    }

    //called when database version mismatch
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion >= newVersion) return;

        Log.d("SQLiteDemo", "onUpgrade: Version = " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);   //not calling a lifecycle method
    }

    //add assignment to database
    public void addAssignment(String name, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DATE, date);
        db.insert(TABLE_NAME, null, values);
        Log.d("SQLiteDemo", name + " added");

        db.close();
    }

    //update Animal name in database
    public void updateAnimal(Animal item, Animal newItem){
        SQLiteDatabase db = this.getWritableDatabase();
        values = new ContentValues();
        values.put(KEY_NAME, newItem.getName());
        db.update(TABLE_NAME, values, KEY_NAME + "=?", new String[] {item.getName()});
        Log.d("SQLiteDemo", item.getName() + " updated");
        db.close();
    }

    //delete animal from database
    public void deleteAnimal(Animal item){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_NAME + "=?", new String[] {item.getName()});
        Log.d("SQLiteDemo", item.getName() + " deleted");
        db.close();
    }

    //query database and return ArrayList of all animals
    public ArrayList<Animal> getAnimalList () {

        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.query(TABLE_NAME,
                new String[] {KEY_NAME, KEY_Q},
                null, null, null, null, KEY_NAME);

        //write contents of Cursor to list
        animalList = new ArrayList<Animal>();
        while (cursor.moveToNext()) {
            String str = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            int count = cursor.getInt(cursor.getColumnIndex(KEY_Q));
            animalList.add(new Animal(str, count));
        };
        db.close();
        return animalList;

    }
}


