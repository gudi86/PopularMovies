package br.com.gustavo.popularmovies;

import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import br.com.gustavo.popularmovies.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        if (savedInstanceState != null && savedInstanceState.containsKey(MainActivity.SAVE_MOVIE)) {
            movie = savedInstanceState.getParcelable(MainActivity.SAVE_MOVIE);
            binding.setMovie(movie);
        } else {
            if (getIntent() != null && getIntent().hasExtra(MainActivity.SAVE_MOVIE)) {
                movie = getIntent().getParcelableExtra(MainActivity.SAVE_MOVIE);
                binding.setMovie(movie);
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

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, Movie movie) {
        GlideApp.with(view.getContext())
                .load(NetworkUtils.buildUrlImageBy(movie))
                .into(view);
    }
}
