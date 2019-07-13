package amira.said.com.movieapp;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MoviesList {
    @SerializedName("results")
    @Expose
    private List<MoviesConverter> moviesList;

    public List<MoviesConverter> getMoviesList() {
        return moviesList;
    }

    public void setMoviesList(List<MoviesConverter> moviesList) {
        this.moviesList = moviesList;
    }

}