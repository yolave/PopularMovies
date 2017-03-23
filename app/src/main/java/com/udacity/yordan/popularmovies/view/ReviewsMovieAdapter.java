package com.udacity.yordan.popularmovies.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.yordan.popularmovies.R;
import com.udacity.yordan.popularmovies.json.ResultReviewsMovie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yordan on 3/18/2017.
 */
public class ReviewsMovieAdapter extends RecyclerView.Adapter<ReviewsMovieAdapter.AdapterViewHolder> {

    private List<ResultReviewsMovie> list;

    public ReviewsMovieAdapter(List<ResultReviewsMovie> list){
        this.list = list;
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_reviews,parent,false);
        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        ResultReviewsMovie item = list.get(position);

        holder.mTvContentReview.setText(item.getContent());
        holder.mTvUser.setText(item.getAuthor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_review_content)
        TextView mTvContentReview;
        @BindView(R.id.tv_user_name)
        TextView mTvUser;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
