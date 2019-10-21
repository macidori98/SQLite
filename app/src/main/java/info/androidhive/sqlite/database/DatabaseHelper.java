package info.androidhive.sqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.database.model.Hobby;



public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "hobbies_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Hobby.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Hobby.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertHobby(String hobby) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Hobby.COLUMN_HOBBY, hobby);

        // insert row
        long id = db.insert(Hobby.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Hobby getHobby(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Hobby.TABLE_NAME,
                new String[]{Hobby.COLUMN_ID, Hobby.COLUMN_HOBBY, Hobby.COLUMN_TIMESTAMP},
                Hobby.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare hobby object
        Hobby hobby = new Hobby(
                cursor.getInt(cursor.getColumnIndex(Hobby.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Hobby.COLUMN_HOBBY)),
                cursor.getString(cursor.getColumnIndex(Hobby.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return hobby;
    }

    public List<Hobby> getAllHobbies() {
        List<Hobby> hobbies = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Hobby.TABLE_NAME + " ORDER BY " +
                Hobby.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Hobby hobby = new Hobby();
                hobby.setId(cursor.getInt(cursor.getColumnIndex(Hobby.COLUMN_ID)));
                hobby.setHobby(cursor.getString(cursor.getColumnIndex(Hobby.COLUMN_HOBBY)));
                hobby.setTimestamp(cursor.getString(cursor.getColumnIndex(Hobby.COLUMN_TIMESTAMP)));

                hobbies.add(hobby);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return hobbies;
    }

    public int getHobbiesCount() {
        String countQuery = "SELECT  * FROM " + Hobby.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateHobby(Hobby hobby) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Hobby.COLUMN_HOBBY, hobby.getHobby());

        // updating row
        return db.update(Hobby.TABLE_NAME, values, Hobby.COLUMN_ID + " = ?",
                new String[]{String.valueOf(hobby.getId())});
    }

    public void deleteHobby(Hobby hobby) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Hobby.TABLE_NAME, Hobby.COLUMN_ID + " = ?",
                new String[]{String.valueOf(hobby.getId())});
        db.close();
    }
}
