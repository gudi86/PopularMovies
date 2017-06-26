package br.com.gustavo.popularmovies;

/**
 * Created by gustavomagalhaes on 6/25/17.
 */

public class Result {
    private Movie[] results;

    public Result(){

    }

    public Movie[] getResults() {
        return results;
    }

    public void setResults(Movie[] results) {
        this.results = results;
    }
}
