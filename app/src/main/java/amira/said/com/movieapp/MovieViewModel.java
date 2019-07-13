package amira.said.com.movieapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private MovieRepository movieRepository;
    private LiveData<List<MoviesConverter>> allMovies;
    public MovieViewModel(@NonNull Application application) {
        super(application);
        movieRepository = new MovieRepository(application);
        allMovies = movieRepository.getMovies();
    }
    public void insert(MoviesConverter movie){

                movieRepository.insert(movie);

    }
    public void update(MoviesConverter movie){
        movieRepository.update(movie);
    }
    public void  delete(MoviesConverter movie ){
        movieRepository.delete(movie);
    }
    public LiveData<List<MoviesConverter>> getAllMovies(){
        return allMovies;
    }
}
