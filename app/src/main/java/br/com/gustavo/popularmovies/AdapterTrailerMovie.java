package br.com.gustavo.popularmovies;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.net.MalformedURLException;
import java.net.URL;

import br.com.gustavo.popularmovies.model.Trailer;

/**
 * Created by gustavomagalhaes on 10/1/17.
 */

public class AdapterTrailerMovie extends RecyclerView.Adapter<AdapterTrailerMovie.InfoViewHolder>{


    private OnClickTrailerMovie onClickTrailerMovie;

    private Trailer[] trailers = null;

    interface OnClickTrailerMovie {
        void onClickTrailer(Trailer trailer);
    }


    public AdapterTrailerMovie(Trailer[] trailers, OnClickTrailerMovie onClickTrailerMovie) {
        this.trailers = trailers;
        this.onClickTrailerMovie = onClickTrailerMovie;
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_trailer, parent, false));
    }

    @Override
    public void onBindViewHolder(InfoViewHolder holder, int position) {
        Uri uri = Uri.parse("https://img.youtube.com/vi/" + trailers[position].getKey() + "/default.jpg");

        try {
            Glide.with(holder.itemView)
                    .load(new URL(uri.toString()))
                    .apply(RequestOptions.centerCropTransform()
                            .placeholder(R.mipmap.ic_launcher) // TODO Trocar imagem do place holder
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.ivPoster);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        holder.tvTitle.setText(trailers[position].getName());
    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        }
        return trailers.length;
    }


    public class InfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivPoster;
        TextView tvTitle;

        private InfoViewHolder(View itemView) {
            super(itemView);
            ivPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickTrailerMovie.onClickTrailer(trailers[getAdapterPosition()]);
        }
    }
}
