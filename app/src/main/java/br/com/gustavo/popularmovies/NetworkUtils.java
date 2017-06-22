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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_KEY = "<API_KEY>;

    private static final String STATIC_MOVIE = "https://api.themoviedb.org/";

    private static final String STATIC_IMG = "http://image.tmdb.org/";

    public static final int URL_POPULAR = 0;
    public static final int URL_RATED = 1;


    /*
        Most Popular
        https://api.themoviedb.org/3/movie/popular?api_key=f8eaaf178b127b18ef4bf79a34264ddb

        Top Rated
        https://api.themoviedb.org/3/movie/top_rated?api_key=f8eaaf178b127b18ef4bf79a34264ddb

        Imagem
        http://image.tmdb.org/t/p/[w92|w154|w185|w342|w500|w780|original]/id_img
     */

    /**
     *
     * @param kindOrder The location that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrlMovieBySort(int kindOrder) {

        Uri uri = Uri.parse(STATIC_MOVIE).buildUpon()
                .path("3/discover/movie")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("sort_by", kindOrder==0?"popularity.desc":"vote_count.desc")
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

    public static URL buildUrlImageBy(Movie movie) {

        Uri uri = Uri.parse(STATIC_IMG).buildUpon()
                .path("t/p/w185")
                .appendPath(movie.getPosterPath())
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

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}