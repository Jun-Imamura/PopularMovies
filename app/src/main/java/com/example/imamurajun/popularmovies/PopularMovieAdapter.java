package com.example.imamurajun.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by imamurajun on 2015/11/28.
 */

public class PopularMovieAdapter extends BaseAdapter {
        private Context mContext;
        private final ArrayList<PopularMovie> mMovies;
        private final int mHeight;
        private final int mWidth;

        public PopularMovieAdapter(Context c, ArrayList<PopularMovie> popularMovies) {
            mContext = c;
            mMovies = new ArrayList<PopularMovie>(popularMovies);
            mHeight = Math.round(mContext.getResources().getDimension(R.dimen.poster_height));
            mWidth = Math.round(mContext.getResources().getDimension(R.dimen.poster_width));
        }

        public void addAll(Collection<PopularMovie> xs) {
            mMovies.addAll(xs);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mMovies.size();
        }

        @Override
        public PopularMovie getItem(int position) {
            if (position < 0 || position >= mMovies.size()) {
                return null;
            }
            return mMovies.get(position);
        }

        @Override
        public long getItemId(int position) {
            PopularMovie movie = getItem(position);
            if (movie == null) {
                return -1L;
            }

            return movie.getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PopularMovie movie = getItem(position);
            if (movie == null) {
                return null;
            }

            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView = (ImageView) convertView;
            }

            Uri posterUri = movie.buildPosterUri(mContext.getString(R.string.api_poster_default_size));
            Picasso.with(mContext)
                    .load(posterUri)
                    .into(imageView);

            return imageView;
        }
    }

