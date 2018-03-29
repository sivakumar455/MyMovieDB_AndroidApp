package com.example.android.mymovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author  Siva Kumar Padala
 * @version 1.0
 * @since   29/03/18
 */

public class MovieContentProvider extends ContentProvider{

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieDbContract.AUTHORITY,MovieDbContract.PATH_TASKS,TASKS);
        uriMatcher.addURI(MovieDbContract.AUTHORITY,MovieDbContract.PATH_TASKS+"/#",TASK_WITH_ID);
        return uriMatcher;
    }

    private MoviesDbHelper moviesDbHelper;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        moviesDbHelper = new MoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor retCursor;
        switch (match){
            case TASKS:
                retCursor = db.query(MovieDbContract.MovieDb.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1);
                break;
            case TASK_WITH_ID:
                // Get the id from the URI
                String id = uri.getPathSegments().get(1);

                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                // Construct a query as you would normally, passing in the selection/args
                retCursor =  db.query(MovieDbContract.MovieDb.TABLE_NAME,
                        strings,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        s1);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported  query "+ uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        //return null;
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri retrunUri;

        switch (match){
            case TASKS:
                long id = db.insert(MovieDbContract.MovieDb.TABLE_NAME,null,contentValues);
                if (id > 0){
                    retrunUri = ContentUris.withAppendedId(MovieDbContract.MovieDb.CONTENT_URI,id);
                }else{
                    throw new UnsupportedOperationException("Failed to insert  "+ uri);
                }

                break;
            default:
                throw new UnsupportedOperationException("Default query in Insert "+ uri);
        }
        //return null;
        getContext().getContentResolver().notifyChange(uri,null);
        return  retrunUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        String id = uri.getPathSegments().get(1);
        String mWhere = "_id=?";
        String[] mWhereArgs = new String[]{id};

        int res=0;
        switch (match){
            case TASK_WITH_ID:
                res = db.delete(MovieDbContract.MovieDb.TABLE_NAME,mWhere,mWhereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported delete"+ uri);
        }
        //return null;
        getContext().getContentResolver().notifyChange(uri,null);
        return res;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
