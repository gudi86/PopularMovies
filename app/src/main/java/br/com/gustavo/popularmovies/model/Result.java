package br.com.gustavo.popularmovies.model;


/**
 * Created by gustavomagalhaes on 6/25/17.
 */

public class Result<E> {
    private E[] results;

    public Result(){

    }

    public E[] getResults() {
        return results;
    }

    public void setResults(E[] results) {
        this.results = results;
    }
}
