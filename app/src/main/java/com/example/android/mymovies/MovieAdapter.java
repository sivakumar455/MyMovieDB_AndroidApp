package com.example.android.mymovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author  Siva Kumar Padala
 * @version 1.0
 * @since   04/01/18
 */

class MovieAdapter extends BaseAdapter{

    private final Context mContext;
    private final ArrayList<String> mArrayMovieList;

    public MovieAdapter(Context context, ArrayList<String> arr){
        mContext = context;
        mArrayMovieList = arr;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return mArrayMovieList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgView;
        if(convertView == null){
            imgView = new ImageView(mContext);
            imgView.setLayoutParams(new GridView.LayoutParams(550, 550));
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else {
            imgView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(mArrayMovieList.get(position)).into(imgView);
        return imgView;
    }
}
