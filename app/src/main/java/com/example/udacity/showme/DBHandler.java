package com.example.udacity.showme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "ShowMe.db";

    public static final String TABLE_MOVIES = "movies";
    public static final String TABLE_TRAILERS = "trailers";
    public static final String TABLE_REVIEWS = "reviews";
    public static final String TABLE_FAVORITE_MOVIES = "favoritemovies";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_RATE = "rate";
    private static final String COLUMN_POSTER = "poster";
    private static final String COLUMN_MOVIE_ID = "movieid";
    private static final String COLUMN_OVERVIEW = "overview";
    private static final String COLUMN_RELEASE_DATE = "releasedate";
    private static final String COLUMN_ORIGINAL_TITLE = "originaltitle";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_FAVORITE = "favorite";
    private static final String COLUMN_HASHCODE = "hashcode";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_MOVIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOVIE_ID + " INTEGER, " +
                COLUMN_POSTER + " TEXT, " +
                COLUMN_ORIGINAL_TITLE + " TEXT, " +
                COLUMN_RELEASE_DATE + " TEXT, " +
                COLUMN_RATE + " TEXT, " +
                COLUMN_OVERVIEW + " TEXT, "+
                COLUMN_FAVORITE + " INTEGER DEFAULT 0 "+
                ");";
        db.execSQL(query);

        String query4 = "CREATE TABLE " + TABLE_FAVORITE_MOVIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOVIE_ID + " INTEGER, " +
                COLUMN_POSTER + " TEXT, " +
                COLUMN_ORIGINAL_TITLE + " TEXT, " +
                COLUMN_RELEASE_DATE + " TEXT, " +
                COLUMN_RATE + " TEXT, " +
                COLUMN_OVERVIEW + " TEXT, "+
                COLUMN_FAVORITE + " INTEGER DEFAULT 1 "+
                ");";
        db.execSQL(query4);

        String query1 = " CREATE TABLE " + TABLE_TRAILERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOVIE_ID + " INTEGER, " +
                COLUMN_CONTENT + " TEXT " +
                " );";
        db.execSQL(query1);

        String query2 = " CREATE TABLE " + TABLE_REVIEWS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOVIE_ID + " INTEGER, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_HASHCODE + " TEXT " +
                " );";
        db.execSQL(query2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TRAILERS);
        onCreate(db);
    }

    // NOTICE : methods start
    // NOTICE : add and get here ----------!
    public void add(String table_name ,String id ,String content, int x){
        SQLiteDatabase db = getWritableDatabase();
        if(!isContentInDatabase(table_name,id,content,x)){
            ContentValues values = new ContentValues();
            values.put(COLUMN_MOVIE_ID,id);
            values.put(COLUMN_CONTENT,content);
            if(table_name == TABLE_REVIEWS)values.put(COLUMN_HASHCODE,x);
            db.insert(table_name,null,values);
            db.close();
        }
    }

    public ArrayList<String> get(String table_name ,String id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + table_name + " WHERE " + COLUMN_MOVIE_ID + " = " + Integer.parseInt(id) +" ;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int counter = 0;
        ArrayList<String> results = new ArrayList<>() ;

        while (!c.isAfterLast()){
            results.add(counter, c.getString(c.getColumnIndex(COLUMN_CONTENT)));
            counter++;
            c.moveToNext();
        }
        db.close();
        return results;
    }
    // NOTICE : addMovie and getALLMovies and getMovie here ----------!
    public void addMovie(Movie movie,String table_name){
        if(!IsInDataBase(table_name,movie.getId())){
            ContentValues values = new ContentValues();
            values.put(COLUMN_MOVIE_ID, movie.getId());
            values.put(COLUMN_POSTER, movie.getPoster());
            values.put(COLUMN_ORIGINAL_TITLE, movie.getOriginal_title());
            values.put(COLUMN_RELEASE_DATE, movie.getRelease_date());
            values.put(COLUMN_RATE, movie.getVote_average());
            values.put(COLUMN_OVERVIEW, movie.getOverview());
            values.put(COLUMN_FAVORITE,movie.getFavorite());
            SQLiteDatabase db = getWritableDatabase();
            db.insert(table_name, null, values);
            db.close();
        }
    }

    public ArrayList<Movie> getALLMovies(boolean getFavorites){
        SQLiteDatabase db = getWritableDatabase();
        String query ;
        if(getFavorites){
            query ="SELECT * FROM " + TABLE_FAVORITE_MOVIES + " ;";
        }else{
            query ="SELECT * FROM " + TABLE_MOVIES + " ;";
        }
        ArrayList<Movie> movies = new ArrayList<>();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            movies.add(new Movie(
                    c.getString(c.getColumnIndex(COLUMN_POSTER)),
                    c.getString(c.getColumnIndex(COLUMN_MOVIE_ID)),
                    c.getString(c.getColumnIndex(COLUMN_OVERVIEW)),
                    c.getString(c.getColumnIndex(COLUMN_RATE)),
                    c.getString(c.getColumnIndex(COLUMN_ORIGINAL_TITLE)),
                    c.getString(c.getColumnIndex(COLUMN_RELEASE_DATE)),
                    c.getInt(c.getColumnIndex(COLUMN_FAVORITE))
            ));
            c.moveToNext();
        }
        db.close();
        return movies;
    }

    public Movie getMovie(String id,boolean getFavorite){
        SQLiteDatabase db = getWritableDatabase();
        String query;
        if(getFavorite){
            query = "SELECT * FROM " + TABLE_FAVORITE_MOVIES + " WHERE " + COLUMN_MOVIE_ID + " = " + Integer.parseInt(id) + ";";
        }else{
            query = "SELECT * FROM " + TABLE_MOVIES + " WHERE " + COLUMN_MOVIE_ID + " = " + Integer.parseInt(id) + ";";
        }

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Movie tmp = new Movie(
                c.getString(c.getColumnIndex(COLUMN_POSTER)),
                c.getString(c.getColumnIndex(COLUMN_MOVIE_ID)),
                c.getString(c.getColumnIndex(COLUMN_OVERVIEW)),
                c.getString(c.getColumnIndex(COLUMN_RATE)),
                c.getString(c.getColumnIndex(COLUMN_ORIGINAL_TITLE)),
                c.getString(c.getColumnIndex(COLUMN_RELEASE_DATE)),
                c.getInt(c.getColumnIndex(COLUMN_FAVORITE))
        );
        db.close();
        return tmp;
    }

    public void clearDb(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_MOVIES;
        db.execSQL(query);
        db.close();
    }

    // NOTICE : CHECK Movie isInDataBase here
    public boolean IsInDataBase(String table_name ,String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + table_name + " WHERE " + COLUMN_MOVIE_ID + " = " + Integer.parseInt(id) + " ;", null);
        int x = c.getCount();
        db.close();
        c.close();
        if (x == 0) return false;
        else return true;
    }

    public boolean isContentInDatabase(String table_name ,String id ,String content, int x){
        SQLiteDatabase db = getWritableDatabase();
        String query;
        if(x == 0){
             query = "SELECT * FROM " + table_name + " WHERE " + COLUMN_MOVIE_ID + "= " + Integer.parseInt(id) +
                    " AND " + COLUMN_CONTENT + " = \"" + content + "\" ;";
        }else{
             query = "SELECT * FROM " + table_name + " WHERE " + COLUMN_MOVIE_ID + "= " + Integer.parseInt(id) +
                    " AND " + COLUMN_HASHCODE + " = \"" + Integer.toString(x) + "\" ;";
        }
        Cursor c = db.rawQuery(query, null);
        int y = c.getCount();
        if(y > 0)return true;
        else return false;
    }
    // NOTICE : add and delete into/from FAVORITES
    public void addToFavorite(Movie movie){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + TABLE_MOVIES + " SET " + COLUMN_FAVORITE + " = 1 " + " WHERE " + COLUMN_MOVIE_ID + " = " + Integer.parseInt(movie.getId()) + " ;";
        db.execSQL(query);
        addMovie(movie, TABLE_FAVORITE_MOVIES);
    }
    public void deleteFromFavorite(String id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + TABLE_MOVIES + " SET " + COLUMN_FAVORITE + " = 0 " + " WHERE " + COLUMN_MOVIE_ID + " = " + Integer.parseInt(id) + " ;";
        db.execSQL(query);
        deleteMovie(id);
    }
    public void deleteMovie(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FAVORITE_MOVIES + " WHERE " + COLUMN_MOVIE_ID + " = " + Integer.parseInt(id) + " ;");
    }
}
