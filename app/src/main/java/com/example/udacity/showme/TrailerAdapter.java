package com.example.udacity.showme;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hussam eldien on 1/1/2016.
 */
public class TrailerAdapter extends ArrayAdapter<String> {
    public TrailerAdapter(Activity context, List<String> results){
        super(context,0,results);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);
        }

        TextView trailer = (TextView) convertView.findViewById(R.id.trailer_text);
        String num = Integer.toString(position + 1);
        trailer.setText("Trailer " + num);
        return convertView;
    }
}
