package br.com.gustavo.popularmovies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import br.com.gustavo.popularmovies.model.Movie;

/**
 * Created by gustavomagalhaes on 6/12/17.
 */

// TODO Verificar bug ao realizar o scroll e as imagens estão ficando com tamanho errado, o problema esta com a lib do glide.

class AdapterGridMovies extends RecyclerView.Adapter<AdapterGridMovies.MovieViewHolder> {

    private static final String TAG = AdapterGridMovies.class.getSimpleName();

    private final MovieOnClickAdapter onClickAdapter;

    interface MovieOnClickAdapter {
        void onClickAdapter(Movie movie);
    }

    private Movie[] mMovies;

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivPoster;

        MovieViewHolder(View itemView) {
            super(itemView);
            ivPoster = (ImageView) itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "Event onCLick was executed in class ViewHolder");
            onClickAdapter.onClickAdapter(mMovies[getAdapterPosition()]);
        }
    }

    AdapterGridMovies(MovieOnClickAdapter movieOnClickAdapter) {
        onClickAdapter = movieOnClickAdapter;
    }

    @Override
    public AdapterGridMovies.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating View Holder");
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_poster, parent, false));
    }

    @Override
    public void onBindViewHolder(AdapterGridMovies.MovieViewHolder holder, int position) {
        Log.d(TAG, "Position holder: " + position);

        if (mMovies[position].getImage() != null && mMovies[position].getImage().length > 0) {


            Bitmap bitmap = BitmapFactory.decodeByteArray(mMovies[position].getImage(), 0, mMovies[position].getImage().length);

            holder.ivPoster.setImageBitmap(bitmap);

//            Glide.with(holder.itemView)
//                    .asBitmap()
//                    .load(bitmap)
//                    .apply(RequestOptions.centerCropTransform()
//                            .placeholder(R.mipmap.ic_launcher))
//                    .into(holder.ivPoster);
        } else {
            URL url = NetworkUtils.buildUrlImageBy(mMovies[position], holder.itemView.getContext().getResources().getDisplayMetrics().densityDpi);

            Glide.with(holder.itemView)
                    .load(url)
                    .apply(RequestOptions.centerCropTransform()
                            .placeholder(R.mipmap.ic_launcher)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.ivPoster);

        }

    }

    @Override
    public int getItemCount() {
        if (mMovies == null) return 0;
        return mMovies.length;
    }

    void setNewMovies(Movie[] movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }
}
