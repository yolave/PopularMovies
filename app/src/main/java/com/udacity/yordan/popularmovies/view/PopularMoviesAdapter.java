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
 * Recycler View Adapter used to show the list of popular movies retrieved from the server
 * Created by Yordan on 1/27/2017.
 */
public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.ViewHolder> {
    private final List<Result> mPopularMoviesRespList;
    private View.OnClickListener mListener;

    @Override
    public PopularMoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PopularMoviesAdapter.ViewHolder holder, int position) {
        final String moviePoster = mPopularMoviesRespList.get(position).getPosterPath();
        final Integer movieId = mPopularMoviesRespList.get(position).getId();
        holder.mMoviePoster.setTag(movieId);
        holder.mMoviePoster.setOnClickListener(this.mListener);
        Picasso.with(holder
                .mMoviePoster
                .getContext())
                .load(NetworkUtils.buildMoviePosterUrl(moviePoster).toString())
                .into(holder.mMoviePoster);
    }

    public PopularMoviesAdapter(List<Result> popularMoviesRespList){
        this.mPopularMoviesRespList = popularMoviesRespList;
    }

    public PopularMoviesAdapter(List<Result> popularMoviesRespList, View.OnClickListener listener){
        this.mPopularMoviesRespList = popularMoviesRespList;
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mPopularMoviesRespList.size();
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
