package com.example.udacity.showme;

import android.content.Context;
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
import java.util.ArrayList;

public class FetchData extends AsyncTask<String,Void,Void> {

    // TODO : include your api key here
    public final String api_key = "";

    private final String FETCH_TAG = "FetchDataFilter";
    DBHandler dbHandler ;
    private final Context mContext;
    String[] trailers ;
    String[] reviews;
    private onCompleteListener mListener;

    public FetchData(Context context){
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

    private void getDataFromJSON(String dataJsonStr,String dataRJsonStr , String id)
            throws JSONException {
        final String RESULTS = "results";
        final String VIEDO_KEY = "key";
        final String REVIEW = "content";

        JSONObject video = new JSONObject(dataJsonStr);
        JSONArray array = video.getJSONArray(RESULTS);
        String tmpkey;

        trailers = new String[array.length()];
        for(int i =0;i<array.length();i++){
            JSONObject tmp = array.getJSONObject(i);
            tmpkey = tmp.getString(VIEDO_KEY);
            trailers[i] = tmpkey;
        }

        JSONObject review = new JSONObject(dataRJsonStr);
        JSONArray array1 = review.getJSONArray(RESULTS);
        String tmpContent;

        reviews = new String[array1.length()];
        for (int j=0;j<array1.length();j++){
            JSONObject tmp = array1.getJSONObject(j);
            tmpContent = tmp.getString(REVIEW);
            reviews[j] = tmpContent;
        }

        dbHandler = new DBHandler(mContext);
        for(String s : trailers){
            dbHandler.add(dbHandler.TABLE_TRAILERS, id, s,0);
            Log.v("LOG","Key : " + s);
        }
        for(String s : reviews){
            int x = s.hashCode();
            dbHandler.add(dbHandler.TABLE_REVIEWS,id,s,x);
            Log.v("LOG", "Review : " + s);
        }
    }

    public String connect(String final_url){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JsonStr = null;

        try{
            URL url = new URL(final_url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) return null;
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) buffer.append(line + "\n");
            if (buffer.length() == 0) return null;

            JsonStr = buffer.toString();

        }catch(IOException e){
            Log.e(FETCH_TAG, "Error ", e);
            return null;
        }finally {
            if(urlConnection != null) urlConnection.disconnect();
            if(reader != null){
                try{
                    reader.close();
                }catch(final IOException e){
                    Log.e(FETCH_TAG, "Error closing stream", e);
                }
            }
        }
        return JsonStr;
    }

    @Override
    protected Void doInBackground(String... params) {
        String videoJsonStr = null;
        String reviewJsonStr = null;
        final String base_url = "http://api.themoviedb.org/3/movie/";

        final String video_url_end = "/videos?api_key=";
        String video_url = base_url + params[0] + video_url_end + api_key;
        videoJsonStr = connect(video_url);

        final String review_url_end = "/reviews?api_key=";
        String review_url = base_url + params[0] + review_url_end + api_key;
        reviewJsonStr = connect(review_url);

        try {
             getDataFromJSON(videoJsonStr,reviewJsonStr,params[0]);
        }catch (JSONException je) {
            Log.e(FETCH_TAG, je.getMessage(), je);
        }
        return null;
    }
}