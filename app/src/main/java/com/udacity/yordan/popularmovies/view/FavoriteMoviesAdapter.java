package com.udacity.yordan.popularmovies.view;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.yordan.popularmovies.R;
import com.udacity.yordan.popularmovies.biz.MoviesBO;
import com.udacity.yordan.popularmovies.biz.impl.MoviesBOImpl;
import com.udacity.yordan.popularmovies.json.MovieDetailResp;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yordan on 3/18/2017.
 */

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.AdapterViewHolder> {
    private List<MovieDetailResp> list;
    private View.OnClickListener mListener;
    private Cursor mCursor;
    private MoviesBO moviesBO = new MoviesBOImpl();

    public FavoriteMoviesAdapter(List<MovieDetailResp> list, View.OnClickListener listener){
        this.list = list;
        this.mListener = listener;
    }

    public FavoriteMoviesAdapter(Cursor cursor, View.OnClickListener listener){
        this.mCursor = cursor;
        this.mListener = listener;
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list,parent,false);
        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
//        if(mCursor != null) {
//            mCursor.moveToPosition(position);

//            int movieIdIdx = mCursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID);

//            Integer movieId = mCursor.getInt(movieIdIdx);
//            MovieDetailResp movieDetailResp = moviesBO.getMovieDetail(movieId);
//            final String moviePoster = movieDetailResp.getPosterPath();
        final String moviePoster = list.get(position).getPosterPath();
        final Integer movieId = list.get(position).getId();
            holder.mMoviePoster.setTag(movieId);
            holder.mMoviePoster.setOnClickListener(this.mListener);

            Picasso.with(holder
                    .mMoviePoster
                    .getContext())
                    .load(NetworkUtils.buildMoviePosterUrl(moviePoster).toString())
                    .into(holder.mMoviePoster);
//        }
    }

    @Override
    public int getItemCount() {
        int count = list.size();
//        int count = 0;
//        if(mCursor != null){
//            count = mCursor.getCount();
//        }
        return count;
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_movie_poster)
        ImageView mMoviePoster;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
