package br.com.gustavo.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.gustavo.popularmovies.model.Review;

/**
 * Created by gustavomagalhaes on 10/1/17.
 */

public class AdapterReviewMovie extends RecyclerView.Adapter<AdapterReviewMovie.InfoViewHolder>{

    private Review[] reviews = null;

    public AdapterReviewMovie(Review[] reviews) {
        this.reviews = reviews;
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(InfoViewHolder holder, int position) {
        holder.tvAuthor.setText(reviews[position].getAuthor());
        holder.tvDescription.setText(reviews[position].getContent());
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        }
        return reviews.length;
    }


    public class InfoViewHolder extends RecyclerView.ViewHolder {

        TextView tvAuthor;
        TextView tvDescription;

        private InfoViewHolder(View itemView) {
            super(itemView);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
        }
    }
}
