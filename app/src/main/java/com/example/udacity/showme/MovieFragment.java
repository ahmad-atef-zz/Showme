package com.example.udacity.showme;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class MovieFragment extends Fragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callback mCallbacks = sDummyCallback;
    private int mActivatedPosition = GridView.INVALID_POSITION;


    private static String TAG = "TAGG";
    public CustomAdapter mMovieAdapter;
    DBHandler dbHandler;
    GridView gridView;
    ArrayList<Movie> results = new ArrayList<Movie>();
    boolean getJustFavorites = true , getAll = false;
    boolean TwoPane = false;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    private static Callback sDummyCallback = new Callback() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Log.v(TAG, "Fragment - onCreateView");

        final View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        mMovieAdapter = new CustomAdapter(getActivity(), new ArrayList<Movie>());

        gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Movie MovieData = mMovieAdapter.getItem(position);

                mCallbacks.onItemSelected(MovieData.getId());
                onSaveInstanceState(savedInstanceState);
            }
        });
        return rootView;
    }

    public void autoSelectFirstItem(){
        if(TwoPane){
            gridView.performItemClick(gridView.getAdapter().getView(0, null, null), 0, 0);
            gridView.setDrawSelectorOnTop(true);
            gridView.setSelector(R.drawable.my_shap);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(!(activity instanceof Callback)){
            throw  new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callback)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mActivatedPosition != GridView.INVALID_POSITION){
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION,mActivatedPosition);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, GridView will automatically
        // give items the 'activated' state when touched.
        gridView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
        TwoPane = activateOnItemClick;
    }


    private void setActivatedPosition(int position){
        if(position == GridView.INVALID_POSITION){
            gridView.setItemChecked(mActivatedPosition, false);
        }else{
            gridView.setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    public void loadData(ArrayList<Movie> res) {
        if (res != null) {
            mMovieAdapter.clear();
            for (Movie MovieData : res) {
                mMovieAdapter.add(MovieData);
            }
                autoSelectFirstItem();
        }
    }

    public void updateData(){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = pref.getString(getString(R.string.sort_by_key), getString(R.string.popular_key));
        dbHandler = new DBHandler(getContext());
        results.clear();
        if (sort_by == getString(R.string.favorite_key)){
            results = dbHandler.getALLMovies(getJustFavorites);
            // Load to the Adapter
            loadData(results);
        }else{
            FetchMovieTask movieTask = new FetchMovieTask(getContext());
            movieTask.execute(sort_by);
            // Load to the adapter
            movieTask.setOnCompleteListener(new FetchMovieTask.onCompleteListener() {
                @Override
                public void onCall() {
                    results = dbHandler.getALLMovies(getAll);
                    loadData(results);
                }
            });
        }
    }
}
