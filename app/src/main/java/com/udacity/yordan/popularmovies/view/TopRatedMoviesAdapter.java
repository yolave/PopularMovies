package com.udacity.yordan.popularmovies.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.yordan.popularmovies.R;
import com.udacity.yordan.popularmovies.json.Result;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;

import java.util.List;

/**
 * Recycler View Adapter used to show the list of the top rated movies retrieved from the server
 * Created by Yordan on 1/27/2017.
 */

public class TopRatedMoviesAdapter extends RecyclerView.Adapter<TopRatedMoviesAdapter.ViewHolder> {
    private final List<Result> mTopRatedMoviesRespList;
    private View.OnClickListener mListener;

    @Override
    public TopRatedMoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TopRatedMoviesAdapter.ViewHolder holder, int position) {
        final String moviePoster = mTopRatedMoviesRespList.get(position).getPosterPath();
        final Integer movieId = mTopRatedMoviesRespList.get(position).getId();
        holder.mMoviePoster.setTag(movieId);
        holder.mMoviePoster.setOnClickListener(this.mListener);
        Picasso.with(holder
                .mMoviePoster
                .getContext())
                .load(NetworkUtils.buildMoviePosterUrl(moviePoster).toString())
                .into(holder.mMoviePoster);
    }

    public TopRatedMoviesAdapter(List<Result> topRatedMoviesRespList){
        this.mTopRatedMoviesRespList = topRatedMoviesRespList;
    }

    public TopRatedMoviesAdapter(List<Result> topRatedMoviesRespList, View.OnClickListener listener){
        this.mTopRatedMoviesRespList = topRatedMoviesRespList;
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mTopRatedMoviesRespList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final ImageView mMoviePoster;
        ViewHolder(View itemView) {
            super(itemView);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.mListener = listener;
    }
}
