package amira.said.com.movieapp;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Getdata {
    @GET("/3/movie/{popular}?")
    Call<MoviesList> getMovies(@Path("popular") String IsPopular, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/{reviews}?")
    Call<ReviewListModel> getReviewsAndTrailers(@Path("id") int movieId,@Path("reviews") String queryKind, @Query("api_key") String apiKey);

}
