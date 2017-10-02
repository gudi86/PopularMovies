/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.gustavo.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import br.com.gustavo.popularmovies.model.Movie;
import br.com.gustavo.popularmovies.model.Result;
import br.com.gustavo.popularmovies.model.Review;
import br.com.gustavo.popularmovies.model.Trailer;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * These utilities will be used to communicate with the weather servers.
 */
final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    static final int URL_POPULAR = 0;
    static final int URL_RATED = 1;
    static final int FAVORITE = 2;


    /*
        Most Popular
        https://api.themoviedb.org/3/movie/popular?api_key=<API_KEY>

        Top Rated
        https://api.themoviedb.org/3/movie/top_rated?api_key=<API_KEY>

        Imagem
        http://image.tmdb.org/t/p/[w92|w154|w185|w342|w500|w780|original]/id_img
     */

    private static URL buildHost() {
        Uri uri = Uri.parse(BuildConfig.URL_MOVIE).buildUpon().build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static Call<Result<Review>> fetchReviewBy(int id) {
        Retrofit retrofit = createRetrofitObj();
        return retrofit.create(MovieService.class).fetchMoviesReviews(String.valueOf(id));
    }

    public static Call<Result<Trailer>> fetchTrailerBy(int id) {
        Retrofit retrofit = createRetrofitObj();
        return retrofit.create(MovieService.class).fetchMoviesTrailers(String.valueOf(id));
    }

    static Call<Result<Movie>> createMovieServiceBySort(int kindOrder) {

        Retrofit retrofit = createRetrofitObj();

        Call<Result<Movie>> callMovies;
        if (kindOrder == 0) {
            callMovies = retrofit.create(MovieService.class).fetchMoviesPopular();
        } else {
            callMovies = retrofit.create(MovieService.class).fetchMoviesTopRated();
        }
        return callMovies;
    }

    @NonNull
    private static Retrofit createRetrofitObj() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", BuildConfig.API_KEY)
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkUtils.buildHost().toString())
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .client(
                        builder.build()
                )
                .build();

        return retrofit;
    }

    static Uri buildUriImageBy(Movie movie, int dpi) {

        if (movie.getPosterPath() == null || movie.getPosterPath().equals("")) {
            return null;
        }

        int size;
//        switch (dpi) {
//            case 80: size = 92; break;
//            case 160: size = 154; break;
//            case 240: size = 185; break;
//            case 320: size = 342; break;
//            case 480: size = 500; break;
//            case 640: size = 780; break;
//            default: size = 0;
//        }

        switch (dpi) {
            case 160: size = 92; break;
            case 240: size = 154; break;
            case 320: size = 185; break;
            case 480: size = 342; break;
            case 640: size = 500; break;
            default: size = 0;
        }

        return Uri.parse(BuildConfig.URL_IMG).buildUpon()
                .path("t/p/w" + size)
                .appendPath(movie.getPosterPath().replace("/", ""))
                .build();
    }

    static URL buildUrlImageBy(Movie movie, int dpi) {

        Uri uri = buildUriImageBy(movie, dpi);

        if (uri == null) {
            return null;
        }

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    static void openVideoYouTube(Context context, String key) {
        Intent intentAppYouTube = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent intentWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));

        try {
            context.startActivity(intentAppYouTube);
        } catch(ActivityNotFoundException e) {
            context.startActivity(intentWeb);
        }
    }
}