package com.example.imamurajun.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collection;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    public MainActivityFragment() {
    }


    public static final int MAX_PAGES = 100;
    private boolean mIsLoading = false;
    private int mPagesLoaded = 0;
    private TextView mLoading;
    private PopularMovieAdapter mMovieAdapter;


    private ArrayList<PopularMovie> mMovieList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mMovieList = new ArrayList<>();
        }
        else {
            mMovieList = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new PopularMovieAdapter(getActivity(), mMovieList);
        mLoading = (TextView) view.findViewById(R.id.loading);

        initGrid(view);
        startLoading();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", mMovieList);
        super.onSaveInstanceState(outState);
    }

    private class FetchPageTask extends AsyncTask<Integer, Void, Collection<PopularMovie>> {

        public  final String LOG_TAG = FetchPageTask.class.getSimpleName();

        @Override
        protected Collection<PopularMovie> doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            int page = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;

            try {
                final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_PARAM_PAGE = "page";
                final String API_PARAM_KEY = "api_key";
                final String API_SORTING = PreferenceManager
                        .getDefaultSharedPreferences(getActivity())
                        .getString(
                                getString(R.string.pref_sorting_key),
                                getString(R.string.pref_sorting_default_value)
                        );

                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendPath(API_SORTING)
                        .appendQueryParameter(API_PARAM_PAGE, String.valueOf(page))
                        .appendQueryParameter(API_PARAM_KEY, getString(R.string.api_key))
                        .build();


                Log.d(LOG_TAG, "QUERY URI: " + builtUri.toString());
                URL url = new URL(builtUri.toString());

                // Create the request to themoviedb api, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                responseJsonStr = buffer.toString();

            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error", ex);
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
                return fetchMoviesFromJson(responseJsonStr);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, "Can't parse JSON: " + responseJsonStr, ex);
                return null;
            }
        }

        private Collection<PopularMovie> fetchMoviesFromJson(String jsonStr) throws JSONException {
            final String KEY_MOVIES = "results";

            JSONObject json  = new JSONObject(jsonStr);
            JSONArray movies = json.getJSONArray(KEY_MOVIES);
            ArrayList result = new ArrayList<>();

            for (int i = 0; i < movies.length(); i++) {
                result.add(PopularMovie.fromJson(movies.getJSONObject(i)));
            }

            return result;
        }

        @Override
        protected void onPostExecute(Collection<PopularMovie> xs) {
            if (xs == null) {
                Toast.makeText(
                        getActivity(),
                        getString(R.string.msg_server_error),
                        Toast.LENGTH_SHORT
                ).show();

                stopLoading();
                return;
            }

            mPagesLoaded++;

            stopLoading();
            mMovieList = (ArrayList<PopularMovie>)xs;
            mMovieAdapter.addAll(xs);
        }

    }

    private void startLoading() {
        if (mIsLoading) {
            return;
        }
        if (mPagesLoaded >= MAX_PAGES) {
            return;
        }
        mIsLoading = true;

        if (mLoading != null) {
            mLoading.setVisibility(View.VISIBLE);
        }
        new FetchPageTask().execute(mPagesLoaded + 1);
    }

    private void stopLoading() {
        if (!mIsLoading) {
            return;
        }

        mIsLoading = false;

        if (mLoading != null) {
            mLoading.setVisibility(View.GONE);
        }
    }




    private void initGrid(View view) {
        final GridView gridview = (GridView) view.findViewById(R.id.grid_view);

        if (gridview == null) {
            return;
        }

        gridview.setAdapter(mMovieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v,
                                    int position,
                                    long id) {

                PopularMovie movie = mMovieAdapter.getItem(position);

                if (movie == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(PopularMovie.EXTRA_MOVIE, movie.toBundle());
                getActivity().startActivity(intent);
            }
        });


        gridview.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if (lastInScreen == totalItemCount) {
                            startLoading();
                        }
                    }
                }

        );
    }
}
