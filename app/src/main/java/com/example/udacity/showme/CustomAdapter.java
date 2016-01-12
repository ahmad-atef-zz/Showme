package com.example.udacity.showme;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class CustomAdapter extends ArrayAdapter<Movie>{

    public boolean checked = false;

    public CustomAdapter(Activity context, List<Movie> results) {
        super(context, 0, results);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie singleResult = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        String baseUrl = "http://image.tmdb.org/t/p/w342/";
        String posterUrl = baseUrl + singleResult.getPoster() ;

        final ImageView posterView = (ImageView) convertView.findViewById(R.id.grid_item_image);
        Picasso.with(getContext()).load(posterUrl).into(posterView);
        //final View myView = (View)convertView.findViewById(R.id.myView);


      /*  posterView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!checked){
                    checked = true;
                    myView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1f));
                    myView.
                }else{
                    checked = false;
                    myView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,0f));
                }
            }
        });*/
        return convertView;
    }
}
