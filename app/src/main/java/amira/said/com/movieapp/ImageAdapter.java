package amira.said.com.movieapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<MoviesConverter> mMoviesList;
    int moviesNum ;
    String imagBaseUrl = "http://image.tmdb.org/t/p/w185/";
    public ImageAdapter(Context c ,List<MoviesConverter> moviesList){
        mMoviesList = moviesList;
        moviesNum = moviesList.size();
        context = c;
    }
    @Override
    public int getCount() {
        return moviesNum;
    }

    @Override
    public Object getItem(int position) {
        return mMoviesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MoviesConverter moviesConverter = mMoviesList.get(position);
        final ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        Glide.with(context).load(imagBaseUrl+moviesConverter.getPoster_path()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.loading).error(R.drawable.error).centerCrop().into(imageView);
        return imageView;
    }
}
