package br.com.gustavo.popularmovies;

import android.content.DialogInterface;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static br.com.gustavo.popularmovies.R.id.tv_synopsis;

public class DetailsActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        if (savedInstanceState != null && savedInstanceState.containsKey(MainActivity.SAVE_MOVIE)) {
            movie = savedInstanceState.getParcelable(MainActivity.SAVE_MOVIE);
        } else {
            if (getIntent() != null && getIntent().hasExtra(MainActivity.SAVE_MOVIE)) {
                movie = getIntent().getParcelableExtra(MainActivity.SAVE_MOVIE);
                bindData(movie);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.txt_msg_error_load_movie);
                builder.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MainActivity.SAVE_MOVIE, movie);
        super.onSaveInstanceState(outState);
    }

    public void bindData(Movie movie) {

        TextView titleOriginal = (TextView) findViewById(R.id.tv_original_title);
        ImageView poster = (ImageView) findViewById(R.id.iv_poster);
        TextView releaseDate = (TextView) findViewById(R.id.tv_release_date);
        TextView rating = (TextView) findViewById(R.id.tv_rating);
        TextView synopsis = (TextView) findViewById(R.id.tv_synopsis);

        DateFormat df = new SimpleDateFormat("yyyy", Locale.getDefault());

        titleOriginal.setText(movie.getTitle());
        releaseDate.setText(df.format(movie.getReleaseDate()));
        rating.setText(String.format(Locale.getDefault(), "%.2f", movie.getRated()));
        synopsis.setText(movie.getOverview());

        GlideApp.with(this)
                .load(NetworkUtils.buildUrlImageBy(movie))
                .into(poster);

    }
}
