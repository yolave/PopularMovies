package com.udacity.yordan.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.yordan.popularmovies.biz.MoviesBO;
import com.udacity.yordan.popularmovies.biz.impl.MoviesBOImpl;
import com.udacity.yordan.popularmovies.exceptions.PopularMoviesExceptionHandler;
import com.udacity.yordan.popularmovies.json.MovieDetailResp;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailMovieActivity extends AppCompatActivity {
    @BindView(R.id.tv_movie_title)
    TextView mMovieTitle;
    @BindView(R.id.tv_tagline)
    TextView mTagline;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDate;
    @BindView(R.id.tv_user_rating)
    TextView mRating;
    @BindView(R.id.tv_duration)
    TextView mDuration;
    @BindView(R.id.tv_synopsis)
    TextView mSynopsis;
    @BindView(R.id.iv_detail_poster_back)
    ImageView mPosterBg;
    @BindView(R.id.iv_detail_poster)
    ImageView mPoster;
    @BindView(R.id.pb_loading)
    ProgressBar mProgressBar;
    @BindView(R.id.sv_inner_movie_detail)
    ScrollView mInnerMovieDetail;
    @BindView(R.id.ib_reload)
    ImageView mReloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new PopularMoviesExceptionHandler(this));
        setContentView(R.layout.activity_detail_movie);
        ButterKnife.bind(this);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performMovieDetailTask();
            }
        });
        performMovieDetailTask();
    }

    private void showError(){
        mReloadButton.setVisibility(View.VISIBLE);
    }

    private void hideError(){
        mReloadButton.setVisibility(View.INVISIBLE);
    }

    private void showResults(){
        mPosterBg.setVisibility(View.VISIBLE);
        mMovieTitle.setVisibility(View.VISIBLE);
        mInnerMovieDetail.setVisibility(View.VISIBLE);
    }

    private void hideResults(){
        mPosterBg.setVisibility(View.INVISIBLE);
        mMovieTitle.setVisibility(View.INVISIBLE);
        mInnerMovieDetail.setVisibility(View.INVISIBLE);
    }

    private void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void performMovieDetailTask(){
        Bundle bundle = getIntent().getExtras();
        Integer movieId = bundle.getInt(getString(R.string.MOVIE_ID_TAG));
        new PerformMovieDetailTask().execute(movieId);
    }

    private class PerformMovieDetailTask extends AsyncTask<Integer,Void,MovieDetailResp>{
        final MoviesBO moviesBO = new MoviesBOImpl();
        private final String TAG = PerformMovieDetailTask.class.getName();

        @Override
        protected void onPreExecute() {
            if(!NetworkUtils.isNetworkAvailable(DetailMovieActivity.this)){
                new AlertDialog.Builder(DetailMovieActivity.this)
                        .setTitle(R.string.no_network_title)
                        .setMessage(R.string.no_network_message)
                        .setPositiveButton(R.string.accept_button,null)
                        .show();
                cancel(true);
            }
            else {
                hideResults();
                hideError();
                showLoading();
            }
        }

        @Override
        protected MovieDetailResp doInBackground(Integer... params) {
            MovieDetailResp resp;
            try {
                if(!NetworkUtils.isOnline(NetworkUtils.buildGetPopularMoviesUrl())){
                    new AlertDialog.Builder(DetailMovieActivity.this)
                            .setTitle(R.string.no_internet_title)
                            .setMessage(R.string.no_internet_message)
                            .setPositiveButton(R.string.accept_button,null)
                            .show();
                    cancel(true);
                }
                Integer movieId = params[0];
                Log.d(TAG,"Movie id: " + movieId);
                resp = moviesBO.getMovieDetail(movieId);
            }
            catch (Exception e){
                Log.e(getClass().getSimpleName(),"Error: " + e.getMessage());
                throw e;
            }
            return resp;
        }

        @Override
        protected void onPostExecute(MovieDetailResp movieDetailResp) {
            hideLoading();
            //Set values onto the views
            mMovieTitle.setText(movieDetailResp.getOriginalTitle());
            mTagline.setText(movieDetailResp.getTagline());
            mSynopsis.setText(movieDetailResp.getOverview());
            mReleaseDate.setText(movieDetailResp.getReleaseDate());
            mRating.setText(String.valueOf(movieDetailResp.getVoteAverage()));
            final StringBuilder txtDuration = new StringBuilder(movieDetailResp.getRuntime().toString()).append(' ').append(getString(R.string.minutes));
            mDuration.setText(txtDuration);

            Picasso.with(DetailMovieActivity.this)
                    .load(NetworkUtils.buildMoviePosterUrl(movieDetailResp.getBackdropPath()).toString())
                    .into(mPosterBg);
            Picasso.with(DetailMovieActivity.this)
                    .load(NetworkUtils.buildMoviePosterUrl(movieDetailResp.getPosterPath()).toString())
                    .into(mPoster);
            showResults();
        }

        @Override
        protected void onCancelled() {
            hideResults();
            hideLoading();
            showError();
        }
    }
}
