package com.example.udacity.showme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity
        implements MovieFragment.Callback {
    private static final String TAG = "TAGG";

    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Activity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            ((MovieFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_movie))
                    .setActivateOnItemClick(true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putString("MyMovie", id);
            final DetailActivityFragment detailFragment = new DetailActivityFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,detailFragment)
                    .addToBackStack(null)
                    .commit();

        }else {
            Intent detailIntent = new Intent(this,DetailActivity.class);
            detailIntent.putExtra("MyMovie", id);
            startActivity(detailIntent);
        }
    }
}