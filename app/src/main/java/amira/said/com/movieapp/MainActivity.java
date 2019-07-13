package amira.said.com.movieapp;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    GridView gridview;
    boolean isTopRated;
    String moviesKind = "SORT_CRITRAION";
    String baseUrl = "http://api.themoviedb.org";
    MoviesList returnList;
    ReviewListModel returnResult;
    String apiKey;
    Intent mainIntent;
    MovieViewModel viewModel;
    List<MoviesConverter> moviesList;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiKey = getResources().getString(R.string.apikey);
        gridview = findViewById(R.id.gridview);
        if (CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            doConnection(baseUrl);
            Log.e("MainActivity", "network");
        } else {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            Toast.makeText(this, "No Internet Connection please open Wi_Fi", Toast.LENGTH_LONG).show();
        }
        if (savedInstanceState != null) {
            SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String restoredText = prefs.getString(moviesKind, null);
            if (restoredText.equals("popular")) {
                setTitle("Popular Movies");
                List<MoviesConverter> list = (List<MoviesConverter>) savedInstanceState.get("moviesList");
                gridview.setAdapter(new ImageAdapter(getApplicationContext(), list));
            } else if (restoredText.equals("top_rated")) {
                setTitle("Top Rated Movies");
                List<MoviesConverter> list = (List<MoviesConverter>) savedInstanceState.get("moviesList");
                gridview.setAdapter(new ImageAdapter(getApplicationContext(), list));
            } else if (restoredText.equals("favourit")) {
                setTitle("Favourit Movies");
                Toast.makeText(this, "favourit found", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        if (item.getItemId() == R.id.favourit_movies) {
            setTitle("Favourit");
            editor.putString(moviesKind, "favourit");
            editor.apply();
            Log.v("sortCriteria is ", "favourit");
            viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
            viewModel.getAllMovies().observe(this, new Observer<List<MoviesConverter>>() {
                @Override
                public void onChanged(final List<MoviesConverter> movies) {
                    if (movies.size() != 0) {
                        Toast.makeText(getApplicationContext(), "There are no favourit movies", Toast.LENGTH_LONG).show();
                        gridview.setAdapter(new ImageAdapter(getApplicationContext(), movies));
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mainIntent = new Intent(MainActivity.this, DetailActivity.class);
                                Bundle movieBundel = new Bundle();
                                String title = movies.get(position).getTitle();
                                double voteAVG = movies.get(position).getVote_average();
                                String releaseDate = movies.get(position).getRelease_date();
                                String posterPath = movies.get(position).getPoster_path();
                                String overView = movies.get(position).getOverview();
                                int movieId = movies.get(position).getId();
                                getMovieReviews(movieId, apiKey);
                                getMovieTrailers(movieId, apiKey);
                                movieBundel.putString("PosterPath", posterPath);
                                movieBundel.putString("Title", title);
                                movieBundel.putString("ReleaseDate", releaseDate);
                                movieBundel.putDouble("VoteAvg", voteAVG);
                                movieBundel.putString("Overview", overView);
                                movieBundel.putInt("Id", movieId);
                                mainIntent.putExtras(movieBundel);
                            }
                        });
                    }
                }
            });
        } else if (CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            if (item.getItemId() == R.id.most_popular) {
                setTitle("Popular Movies");
                Log.e("MainActivity itemKind", ":popular");
                editor.putString(moviesKind, "popular");
                editor.apply();
                Log.v("sortCriteria is ", "popular");
                doConnection(baseUrl);
            } else if (item.getItemId() == R.id.top_rated) {
                setTitle("Top Rated Movies");
                editor.putString(moviesKind, "top_rated");
                editor.apply();
                Log.v("sortCriteria is ", "top_rated");
                Log.e("MainActivity itemKind", ":toprated");
                doConnection(baseUrl);
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        return isTopRated;
    }

    public void doConnection(String baseUrl) {
        String sort_critetria;
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(moviesKind, null);
        if ((restoredText != null) && (restoredText.equals("favourit"))) {
            setTitle("Favourit Movies");
            Log.v("MovieKindis ", restoredText);
            viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
            viewModel.getAllMovies().observe(this, new Observer<List<MoviesConverter>>() {
                @Override
                public void onChanged(final List<MoviesConverter> movies) {
                    if (movies.size() != 0) {

                        gridview.setAdapter(new ImageAdapter(getApplicationContext(), movies));
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mainIntent = new Intent(MainActivity.this, DetailActivity.class);
                                Bundle movieBundel = new Bundle();
                                String title = movies.get(position).getTitle();
                                double voteAVG = movies.get(position).getVote_average();
                                String releaseDate = movies.get(position).getRelease_date();
                                String posterPath = movies.get(position).getPoster_path();
                                String overView = movies.get(position).getOverview();
                                int movieId = movies.get(position).getId();
                                getMovieReviews(movieId, apiKey);
                                getMovieTrailers(movieId, apiKey);
                                movieBundel.putString("PosterPath", posterPath);
                                movieBundel.putString("Title", title);
                                movieBundel.putString("ReleaseDate", releaseDate);
                                movieBundel.putDouble("VoteAvg", voteAVG);
                                movieBundel.putString("Overview", overView);
                                movieBundel.putInt("Id", movieId);
                                mainIntent.putExtras(movieBundel);
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "There are no favourit", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if ((restoredText != null) && ((restoredText.equals("popular")) || (restoredText.equals("top_rated")))) {
            sort_critetria = prefs.getString(moviesKind, "No name defined");//"No name defined" is the default value.
            if (sort_critetria.equals("popular")) {
                setTitle("Popular Movies");
            } else {
                setTitle("Top Rated Movies");
            }
            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            Getdata client = retrofit.create(Getdata.class);
            Call<MoviesList> call = client.getMovies(sort_critetria, apiKey);
            call.enqueue(new Callback<MoviesList>() {
                @Override
                public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                    Toast.makeText(getApplicationContext(), "loading success", Toast.LENGTH_LONG).show();
                    if (response.isSuccessful()) {
                        Log.v("MainActivity", "list success");
                        returnList = response.body();
                        moviesList = returnList.getMoviesList();
                        gridview.setAdapter(new ImageAdapter(getApplicationContext(), moviesList));
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mainIntent = new Intent(MainActivity.this, DetailActivity.class);
                                Bundle movieBundel = new Bundle();
                                String title = moviesList.get(position).getTitle();
                                double voteAVG = moviesList.get(position).getVote_average();
                                String releaseDate = moviesList.get(position).getRelease_date();
                                String posterPath = moviesList.get(position).getPoster_path();
                                String overView = moviesList.get(position).getOverview();
                                int movieId = moviesList.get(position).getId();
                                getMovieReviews(movieId, apiKey);
                                getMovieTrailers(movieId, apiKey);
                                movieBundel.putString("PosterPath", posterPath);
                                movieBundel.putString("Title", title);
                                movieBundel.putString("ReleaseDate", releaseDate);
                                movieBundel.putDouble("VoteAvg", voteAVG);
                                movieBundel.putString("Overview", overView);
                                movieBundel.putInt("Id", movieId);
                                mainIntent.putExtras(movieBundel);
                            }
                        });
                    }

                }

                @Override
                public void onFailure(Call<MoviesList> call, Throwable t) {
                    Log.e("MainActivity", "failMovies", t);
                }
            });
        } else if (restoredText == null) {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            setTitle("Popular Movies");
            editor.putString(moviesKind, "popular");
            editor.apply();

        }
    }


    public void getMovieReviews(int id, final String apiKey1) {
        final ArrayList<ReviewModel> reviewModelArrayList = new ArrayList<ReviewModel>();
        Log.e("MainActivity", "insidGetReviews succes");
        final Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        Getdata client = retrofit.create(Getdata.class);
        Call<ReviewListModel> call = client.getReviewsAndTrailers(id, "reviews", apiKey);
        call.enqueue(new Callback<ReviewListModel>() {
            @Override
            public void onResponse(Call<ReviewListModel> call, Response<ReviewListModel> response) {
                Log.e("MainActivity", "getReview msg  " + response.body());
                if (response.isSuccessful() && (response.body() != null)) {
                    returnResult = response.body();
                    int size = returnResult.getReviewModelList().size();
                    Log.e("MainActivity", "ReturnResultInside size: " + size);
                    ArrayList<ReviewModel> list = (ArrayList<ReviewModel>) returnResult.getReviewModelList();
                    int ListSize = list.size();
                    Log.e("MainActivity", "reviewslist size(): " + ListSize);
                    mainIntent.putParcelableArrayListExtra("reviewsList", list);
                    mainIntent.setExtrasClassLoader(list.getClass().getClassLoader());
                }
            }

            @Override
            public void onFailure(Call<ReviewListModel> call, Throwable t) {
                Log.e("MainActivity", "getReview()-loading loading failed", t);
                Toast.makeText(getApplicationContext(), "loading failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getMovieTrailers(int id, final String apiKey1) {
        final ArrayList<ReviewModel> reviewModelArrayList = new ArrayList<ReviewModel>();
        Log.e("MainActivity", "insidGetReviews succes");
        final Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        Getdata client = retrofit.create(Getdata.class);
        Call<ReviewListModel> call = client.getReviewsAndTrailers(id, "videos", apiKey);
        call.enqueue(new Callback<ReviewListModel>() {
            @Override
            public void onResponse(Call<ReviewListModel> call, Response<ReviewListModel> response) {
                Log.e("MainActivity", "getTrailers msg  " + response.body());
                if (response.isSuccessful() && (response.body() != null)) {
                    returnResult = response.body();
                    int size = returnResult.getReviewModelList().size();
                    Log.e("MainActivity", "ReturnResultInside size: " + size);
                    ArrayList<ReviewModel> list = (ArrayList<ReviewModel>) returnResult.getReviewModelList();
                    int ListSize = list.size();
                    ArrayList<String> keysList = new ArrayList<>();
                    Log.e("MainActivity", "trailerslist size(): " + ListSize);
                    for (int j = 0; j < list.size(); j++) {
                        Log.e("MainActivity", "key =  --" + list.get(j).getKey());
                        keysList.add(list.get(j).getKey());
                    }
                    mainIntent.putExtra("trailersList", keysList);
                    startActivity(mainIntent);
                }
            }

            @Override
            public void onFailure(Call<ReviewListModel> call, Throwable t) {
                Log.e("MainActivity", "getTrailers()-loading loading failed", t);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String restoredText = prefs.getString(moviesKind, null);
        if (restoredText.equals("favourit")) {
            Log.v("favouritList", "restoredText.equals(favourit)");
            viewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
            viewModel.getAllMovies().observe(this, new Observer<List<MoviesConverter>>() {
                @Override
                public void onChanged(final List<MoviesConverter> favMovies) {
                    outState.putParcelableArrayList("favList", new ArrayList<MoviesConverter>(favMovies));
                    Log.e("favouritList", "size" + favMovies.size());
                }
            });

        }
        else {
            outState.putParcelableArrayList("moviesList", new ArrayList<MoviesConverter>(moviesList));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {

                Toast.makeText(this,
                        "Wi-Fi not opend  ", Toast.LENGTH_SHORT).show();
            } else {
                doConnection(baseUrl);
            }
        }
    }
}







