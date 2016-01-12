package com.example.udacity.showme;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    
    // TODO : include your api key here
    public final String api_key = "";

    DBHandler dbHandler;
    Context mContext;
    onCompleteListener mListener;

    public FetchMovieTask(Context context){
        mContext = context;
        this.mListener = null;

    }
    public interface onCompleteListener{
        public void onCall();
    }
    public void setOnCompleteListener(onCompleteListener onSet){
        this.mListener = onSet;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mListener.onCall();
    }

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    public Movie[] movies;

    private void getMovieDataFromJSON(String movieJsonStr)
            throws JSONException {

        final String MovieDB_RESULTS = "results";
        final String MovieDB_POSTER_PATH = "poster_path";
        final String MovieDB_ID = "id";
        final String MovieDB_ORIGINAL_TITLE = "original_title";
        final String MovieDB_OVERVIEW = "overview";
        final String MovieDB_VOTE_AVERAGE = "vote_average";
        final String MovieDB_RELEASE_DATE = "release_date";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray moviesArray = movieJson.getJSONArray(MovieDB_RESULTS);
        movies = new Movie[moviesArray.length()];
        String tmpP,tmpId,tmpOT,tmpO,tmpVA,tmpRD;

        for(int i=0;i<moviesArray.length();i++){
            JSONObject tmp = moviesArray.getJSONObject(i);

            tmpP = tmp.getString(MovieDB_POSTER_PATH);
            tmpId = tmp.getString(MovieDB_ID);
            tmpOT = tmp.getString(MovieDB_ORIGINAL_TITLE);
            tmpO = tmp.getString(MovieDB_OVERVIEW);
            tmpVA = tmp.getString(MovieDB_VOTE_AVERAGE);
            tmpRD = tmp.getString(MovieDB_RELEASE_DATE);

            Movie tmpM = new Movie(tmpP,tmpId,tmpO,tmpVA,tmpOT,tmpRD,0);
            movies[i] = tmpM;
        }
        dbHandler = new DBHandler(mContext);
        dbHandler.clearDb();
        for (Movie m : movies) {
            Log.v(LOG_TAG, "Movie entry: " + m.getPoster());
            dbHandler.addMovie(m, dbHandler.TABLE_MOVIES);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        try {
            final String base_url = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(base_url).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0])
                    .appendQueryParameter(API_KEY_PARAM,api_key)
                    .build();

            Log.v(LOG_TAG, "Build URI " + builtUri.toString());

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) return null;
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) buffer.append(line + "\n");
            if (buffer.length() == 0) return null;

            movieJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Movie JSON string" + movieJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error " + e.getMessage(), e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            getMovieDataFromJSON(movieJsonStr);
        }catch (JSONException je) {
            Log.e(LOG_TAG, je.getMessage(), je);
        }
        return null;
    }
}