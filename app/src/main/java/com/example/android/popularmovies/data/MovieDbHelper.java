package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by ioutd on 11/9/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movieDb.db";
    private static int VERSION = 1;
    public static final String TAG = MovieDbHelper.class.getSimpleName();

    MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_POSTER    + " BLOB NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP  + " BLOB NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_ID  + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE     + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_PLOT      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING    + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE   + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_TABLE);

        Log.d(TAG, "onCreate: CREATE TABLE" + CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
