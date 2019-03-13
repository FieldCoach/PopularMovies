package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;


/**
 * Created by ioutd on 11/9/2017.
 *
 * MovieContentProvider is deprecated.
 */
@Deprecated
public class MovieContentProvider  extends ContentProvider {
    // private MovieDbHelper movieDbHelper;

    // public static final int MOVIES = 100;
    // public static final int MOVIE_WITH_ID = 101;

    // public static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }*/

    @Override
    public boolean onCreate() {
        //onCreate instantiate the DbHelper
        // movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //final SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        // int match = sUriMatcher.match(uri);
        // TODO 1: Cursor assigned to null
        Cursor returnCursor = null;

        /**switch (match) {
            case MOVIES:
                returnCursor = db.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri); */

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        // int match = sUriMatcher.match(uri);
        // TODO 2: Uri assigned to null
        Uri returnUri = null;

        /**switch (match){
            case MOVIES:
                long id = db.insert(MovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed ot insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);*/

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] args) {
        // final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        // int match = sUriMatcher.match(uri);
        // TODO 3: moviesDeleted assigned to 0
        int moviesDeleted = 0;

        /**switch (match){
            case MOVIES:
                moviesDeleted = db.delete(MovieEntry.TABLE_NAME, whereClause, args);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        } */
        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
