package br.com.gustavo.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by gustavomagalhaes on 6/16/17.
 */

public final class UtilsJsonMovie {


    public static final Movie[] convertJsonObjectToMovies(String strJsonMovies) {

        Movie[] movies = null;
        try {
            JSONObject jsonObject = new JSONObject(strJsonMovies);

            JSONArray jsonMovies = jsonObject.getJSONArray("results");

            movies = new Movie[jsonMovies.length()];

            for (int i = 0; i < jsonMovies.length(); i++) {
                JSONObject jsonMovie = jsonMovies.getJSONObject(i);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Movie movie = new Movie(
                        jsonMovie.getInt("id"),
                        jsonMovie.getString("title"),
                        jsonMovie.getString("overview"),
                        jsonMovie.getDouble("vote_average"),
                        dateFormat.parse(jsonMovie.getString("release_date")),
                        jsonMovie.getString("poster_path").replace("/","")
                );
                movies[i] = movie;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return movies;
    }
}
