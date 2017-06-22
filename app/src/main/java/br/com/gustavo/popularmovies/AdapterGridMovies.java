package br.com.gustavo.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.net.URL;

/**
 * Created by gustavomagalhaes on 6/12/17.
 */

// TODO Implementar tratamento de erro nos caso em que não se tem conexão com a internet
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

        URL url = NetworkUtils.buildUrlImageBy(mMovies[position]);

        GlideApp.with(holder.itemView)
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivPoster);
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
