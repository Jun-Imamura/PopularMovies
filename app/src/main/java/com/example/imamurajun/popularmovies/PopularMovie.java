package com.example.imamurajun.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by imamurajun on 2015/11/28.
 */
public class PopularMovie implements Parcelable{

    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_VOTE_COUNT = "vote_count";
    public static final String KEY_RELEASE_DATE = "release_date";

    private long id;
    private String title;
    private String overview;
    private String poster_path;
    private double vote_average;
    private long vote_count;
    private String release_date;


    public PopularMovie(long id,
        String title, String overview, String poster_path,
        double vote_average, long vote_count, String release_date) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.release_date = release_date;
    }


    public PopularMovie(Bundle bundle) {
        this(
                bundle.getLong(KEY_ID),
                bundle.getString(KEY_TITLE),
                bundle.getString(KEY_OVERVIEW),
                bundle.getString(KEY_POSTER_PATH),
                bundle.getDouble(KEY_VOTE_AVERAGE),
                bundle.getLong(KEY_VOTE_COUNT),
                bundle.getString(KEY_RELEASE_DATE)
        );
    }

    private PopularMovie(Parcel in){
        id = in.readLong();
        title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
        vote_count = in.readLong();
        release_date = in.readString();
    }

    public static PopularMovie fromJson(JSONObject jsonObject) throws JSONException {
        return new PopularMovie(
                jsonObject.getLong(KEY_ID),
                jsonObject.getString(KEY_TITLE),
                jsonObject.getString(KEY_OVERVIEW),
                jsonObject.getString(KEY_POSTER_PATH),
                jsonObject.getDouble(KEY_VOTE_AVERAGE),
                jsonObject.getLong(KEY_VOTE_COUNT),
                jsonObject.getString(KEY_RELEASE_DATE)
        );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putLong(KEY_ID, id);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_OVERVIEW, overview);
        bundle.putString(KEY_POSTER_PATH, poster_path);
        bundle.putDouble(KEY_VOTE_AVERAGE, vote_average);
        bundle.putLong(KEY_VOTE_COUNT, vote_count);
        bundle.putString(KEY_RELEASE_DATE, release_date);


        return bundle;
    }


    public long   getId(){return id;}
    public String getTitle() {return title;}
    public String getOverview() {return overview;}
    public String getReleaseDate() {return release_date;}
    public String getRating() {
        return "" + vote_average + " / 10";
    }

    public Uri buildPosterUri(String size) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(poster_path)
                .build();

        return builtUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return id +  "--" + title + "--" + overview +
            "--" + poster_path + "--" + vote_average + "--" + vote_count + "--" + release_date; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(poster_path);
        parcel.writeDouble(vote_average);
        parcel.writeLong(vote_count);
        parcel.writeString(release_date);
    }

    public final Parcelable.Creator<PopularMovie> CREATOR = new Parcelable.Creator<PopularMovie>() {
        @Override
        public PopularMovie createFromParcel(Parcel parcel) {
            return new PopularMovie(parcel);
        }

        @Override
        public PopularMovie[] newArray(int i) {
            return new PopularMovie[i];
        }

    };
}
