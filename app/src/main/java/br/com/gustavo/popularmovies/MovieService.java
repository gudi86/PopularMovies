package br.com.gustavo.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by gustavomagalhaes on 6/25/17.
 */

public interface MovieService {

    @GET("3/movie/popular")
    Call<Result> fetchMoviesPopular(@Query("api_key") String key);

    @GET("3/movie/top_rated")
    Call<Result> fetchMoviesTopRated(@Query("api_key") String key);
}
