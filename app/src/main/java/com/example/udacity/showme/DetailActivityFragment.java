package com.example.udacity.showme;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivityFragment extends Fragment {
    String LOG = "LOG";

    ArrayAdapter<String> Reviews;
    TrailerAdapter Trailers;
    Movie movie;
    ArrayList<String> trailers;
    ArrayList<String> reviews;
    ToggleButton favorite;
    DBHandler dbHandler;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey("MyMovie")){
            String id = getArguments().getString("MyMovie");

            dbHandler = new DBHandler(getContext());
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_by = pref.getString(getString(R.string.sort_by_key), getString(R.string.popular_key));

            if(sort_by == getString(R.string.favorite_key)){
                movie = dbHandler.getMovie(id,true);
            }else{
                movie = dbHandler.getMovie(id,false);
            }
            //TODO FIX
        }
    }

    @TargetApi(23)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if(movie != null){
            Log.v(LOG,"Movie_title " + movie.getOriginal_title());

            Trailers = new TrailerAdapter(getActivity(),new ArrayList<String>());
            Reviews = new ArrayAdapter<>(getContext(),
                    R.layout.review_item,
                    R.id.review_text,
                    new ArrayList<String>());

            ImageView posterThumbnail = (ImageView)rootView.findViewById(R.id.icon_view);
            TextView originalTitle = (TextView) rootView.findViewById(R.id.original_title_text);
            TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date_text);
            TextView rating = (TextView) rootView.findViewById(R.id.rate_text);
            TextView overview = (TextView) rootView.findViewById(R.id.overview_text);

            favorite = (ToggleButton)rootView.findViewById(R.id.favorite_button);
            favorite.setTextOff("Mark as Favorite");
            favorite.setTextOn("Favorite");
            favorite.setTextColor(Color.parseColor("#1e252b"));

            if(movie.getFavorite() == 1 || dbHandler.IsInDataBase(dbHandler.TABLE_FAVORITE_MOVIES,movie.getId())){
                favorite.setChecked(true);
            }else{
                favorite.setChecked(false);
            }

            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favorite.isChecked()) {
                        dbHandler.addToFavorite(movie);
                    } else {
                        dbHandler.deleteFromFavorite(movie.getId());
                    }
                }
            });

            ListView trailersList = (ListView)rootView.findViewById(R.id.trailer_list);
            final ListView reviewsList = (ListView)rootView.findViewById(R.id.reviews_list);

            String baseUrl = "http://image.tmdb.org/t/p/w342/";
            String posterUrl = baseUrl + movie.getPoster() ;
            Picasso.with(getActivity()).load(posterUrl).into(posterThumbnail);

            originalTitle.setText(movie.getOriginal_title());
            releaseDate.setText(movie.getRelease_date());
            rating.setText(movie.getVote_average());
            overview.setText(movie.getOverview());

            trailersList.setAdapter(Trailers);
            reviewsList.setAdapter(Reviews);

            trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String key = Trailers.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + key));
                    startActivity(intent);
                }
            });
            trailersList.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }

            });

            reviewsList.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

        }

        onSaveInstanceState(savedInstanceState);
        Log.v("LOG","onCreateView()");
        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("LOG","onViewCreated()");
    }

    private void fetchData(){
        if(isOnline()){
            FetchData fetchData = new FetchData(getContext());
            fetchData.execute(movie.getId());
            fetchData.setOnCompleteListener(new FetchData.onCompleteListener(){
                @Override
                public void onCall() {
                    loadData();
                }
            });
        }else{
            Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
        }
    }

    private void loadData() {
        trailers = new ArrayList<>();
        reviews = new ArrayList<>();
        trailers = dbHandler.get(dbHandler.TABLE_TRAILERS,movie.getId());
        reviews = dbHandler.get(dbHandler.TABLE_REVIEWS,movie.getId());

        if(trailers != null){
            Trailers.clear();
            for(String t : trailers){
                Trailers.add(t);
                Log.v("LOG","Im trailers and I have some content " + t );
            }
        }
        if(reviews != null){
            Reviews.clear();
            for(String r : reviews){
                Reviews.add(r);
            }
        }
    }

    public void updateData(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = pref.getString(getString(R.string.sort_by_key), getString(R.string.popular_key));

        if(sort_by == getString(R.string.favorite_key)){
            loadData();
        }else{
            fetchData();
        }
    }

    @Override
    public void onStart() {
        Log.v(LOG, "onStart()");
        super.onStart();
        updateData();
    }
}


