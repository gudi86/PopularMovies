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

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * These utilities will be used to communicate with the weather servers.
 */
final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String STATIC_MOVIE = "https://api.themoviedb.org/";

    private static final String STATIC_IMG = "http://image.tmdb.org/";

    static final int URL_POPULAR = 0;
    static final int URL_RATED = 1;


    /*
        Most Popular
        https://api.themoviedb.org/3/movie/popular?api_key=f8eaaf178b127b18ef4bf79a34264ddb

        Top Rated
        https://api.themoviedb.org/3/movie/top_rated?api_key=f8eaaf178b127b18ef4bf79a34264ddb

        Imagem
        http://image.tmdb.org/t/p/[w92|w154|w185|w342|w500|w780|original]/id_img
     */

    private static URL buildHost() {
        Uri uri = Uri.parse(STATIC_MOVIE).buildUpon().build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    static Call<Result> createMovieServiceBySort(int kindOrder) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkUtils.buildHost().toString())
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .client(
                        new OkHttpClient.Builder().build()
                )
                .build();

        Call<Result> callMovies;
        if (kindOrder == 0) {
            callMovies = retrofit.create(MovieService.class).fetchMoviesPopular(BuildConfig.API_KEY);
        } else {
            callMovies = retrofit.create(MovieService.class).fetchMoviesTopRated(BuildConfig.API_KEY);
        }
        return callMovies;
    }

    static URL buildUrlImageBy(Movie movie) {

        Uri uri = Uri.parse(STATIC_IMG).buildUpon()
                .path("t/p/w185")
                .appendPath(movie.getPosterPath().replace("/", ""))
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }
}