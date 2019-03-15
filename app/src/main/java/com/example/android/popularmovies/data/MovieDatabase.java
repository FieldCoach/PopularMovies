package com.example.android.popularmovies.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class}, version=1)
public abstract class MovieDatabase extends RoomDatabase {
    private static MovieDatabase INSTANCE;
    public abstract MovieDao movieDao();

    public static MovieDatabase getDatabase(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MovieDatabase.class,
                    "movie-db")
                    .build();
        }
        return INSTANCE;
    }
}
