package br.com.gustavo.popularmovies;

import br.com.gustavo.popularmovies.model.Movie;
import br.com.gustavo.popularmovies.model.Result;
import br.com.gustavo.popularmovies.model.Review;
import br.com.gustavo.popularmovies.model.Trailer;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by gustavomagalhaes on 6/25/17.
 */

public interface MovieService {

    @GET("3/movie/popular")
    Call<Result<Movie>> fetchMoviesPopular();

    @GET("3/movie/top_rated")
    Call<Result<Movie>> fetchMoviesTopRated();

    @GET("3/movie/{id}/videos")
    Call<Result<Trailer>> fetchMoviesTrailers(@Path("id") String id);

    @GET("3/movie/{id}/reviews")
    Call<Result<Review>> fetchMoviesReviews(@Path("id") String id);
}
