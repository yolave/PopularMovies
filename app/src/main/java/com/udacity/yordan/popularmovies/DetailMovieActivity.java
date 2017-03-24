package com.udacity.yordan.popularmovies;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.yordan.popularmovies.biz.MoviesBO;
import com.udacity.yordan.popularmovies.biz.impl.MoviesBOImpl;
import com.udacity.yordan.popularmovies.data.FavoriteMoviesContract;
import com.udacity.yordan.popularmovies.exceptions.PopularMoviesExceptionHandler;
import com.udacity.yordan.popularmovies.intents.FavMoviesIntentService;
import com.udacity.yordan.popularmovies.json.MovieDetailResp;
import com.udacity.yordan.popularmovies.json.MovieVideoResp;
import com.udacity.yordan.popularmovies.json.ReviewsMovieResp;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;
import com.udacity.yordan.popularmovies.view.MovieVideosAdapter;
import com.udacity.yordan.popularmovies.view.ReviewsMovieAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailMovieActivity extends AppCompatActivity {

    private final static MoviesBO MOVIES_BO = new MoviesBOImpl();
    private static final String TAG = DetailMovieActivity.class.getSimpleName();
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
    @BindView(R.id.rv_videos)
    RecyclerView mRvVideos;
    @BindView(R.id.rv_reviews)
    RecyclerView mRvReviews;
    @BindView(R.id.tv_no_reviews)
    TextView mTvNoReviews;
    @BindView(R.id.tv_no_videos)
    TextView mTvNoVideos;
    @BindView(R.id.iv_favorite)
    ImageView mIvFavorite;

    private Integer movieId;
    private boolean isFav;

    private static final int DETAIL_MOVIE_LOADER = 87;
    private static final int REVIEWS_SEARCH_LOADER = 88;
    private static final int VIDEOS_SEARCH_LOADER = 89;

    private LoaderManager.LoaderCallbacks<MovieDetailResp> detailLoader;
    private LoaderManager.LoaderCallbacks<ReviewsMovieResp> reviewsLoader;
    private LoaderManager.LoaderCallbacks<MovieVideoResp> videosLoader;

    private ResponseReceiver receiver;


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
        Bundle bundle = getIntent().getExtras();
        movieId = bundle.getInt(getString(R.string.MOVIE_ID_TAG));

        performMovieDetailTask();
        initFavImage();
        getMovieVideos();
        getMovieReviews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(receiver);
    }

    private void initFavImage(){
        String stringId = movieId.toString();

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver,filter);

        Intent msgIntent = new Intent(this, FavMoviesIntentService.class);
        msgIntent.putExtra(FavMoviesIntentService.PARAM_IN_MSG, stringId);
        startService(msgIntent);
    }

    private void getMovieVideos() {
        videosLoader = new LoaderManager.LoaderCallbacks<MovieVideoResp>() {
            @Override
            public Loader<MovieVideoResp> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<MovieVideoResp>(DetailMovieActivity.this) {
                    private MovieVideoResp movieVideoResp;

                    @Override
                    protected void onStartLoading() {
                        if (movieVideoResp != null) {
                            deliverResult(movieVideoResp);
                        }
                        else {
                            forceLoad();
                        }
                    }

                    @Override
                    public MovieVideoResp loadInBackground() {
                        MovieVideoResp movieVideoResp;
                        movieVideoResp = MOVIES_BO.getMovieVideos(movieId);
                        return movieVideoResp;
                    }

                    @Override
                    public void deliverResult(MovieVideoResp data) {
                        movieVideoResp = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<MovieVideoResp> loader, MovieVideoResp data) {
                if(data.getResults().isEmpty()) {
                    mRvVideos.setVisibility(View.INVISIBLE);
                    mTvNoVideos.setVisibility(View.VISIBLE);
                }
                else {
                    MovieVideosAdapter adapter = new MovieVideosAdapter(data.getResults());
                    mRvVideos.setAdapter(adapter);
                    mRvVideos.setHasFixedSize(true);
                    mRvVideos.setLayoutManager(new LinearLayoutManager(DetailMovieActivity.this));
                    mRvVideos.setVisibility(View.VISIBLE);
                    mTvNoVideos.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onLoaderReset(Loader<MovieVideoResp> loader) {
                Log.d(TAG,"onLoaderReset...");
            }
        };
        getSupportLoaderManager().initLoader(VIDEOS_SEARCH_LOADER, null, videosLoader);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<MovieVideoResp> reviewsSearchLoader = loaderManager.getLoader(VIDEOS_SEARCH_LOADER);
        if (reviewsSearchLoader == null) {
            loaderManager.initLoader(VIDEOS_SEARCH_LOADER, null, videosLoader);
        } else {
            loaderManager.restartLoader(VIDEOS_SEARCH_LOADER, null, videosLoader);
        }
    }

    private void getMovieReviews() {
        reviewsLoader = new LoaderManager.LoaderCallbacks<ReviewsMovieResp>() {
            @Override
            public Loader<ReviewsMovieResp> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<ReviewsMovieResp>(DetailMovieActivity.this) {
                    ReviewsMovieResp reviewsMovieResp;

                    @Override
                    protected void onStartLoading() {

                        if (reviewsMovieResp != null) {
                            deliverResult(reviewsMovieResp);
                        }
                        else {
                            forceLoad();
                        }
                    }

                    @Override
                    public ReviewsMovieResp loadInBackground() {
                        ReviewsMovieResp reviewsMovieResp;
                        reviewsMovieResp = MOVIES_BO.getMovieReviews(movieId);
                        return reviewsMovieResp;
                    }

                    @Override
                    public void deliverResult(ReviewsMovieResp data) {
                        reviewsMovieResp = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ReviewsMovieResp> loader, ReviewsMovieResp data) {
                if(data.getResults().isEmpty()){
                    mTvNoReviews.setVisibility(View.VISIBLE);
                    mRvReviews.setVisibility(View.INVISIBLE);
                }
                else {
                    ReviewsMovieAdapter adapter = new ReviewsMovieAdapter(data.getResults());
                    mRvReviews.setAdapter(adapter);
                    mRvReviews.setHasFixedSize(true);
                    mRvReviews.setLayoutManager(new LinearLayoutManager(DetailMovieActivity.this));
                    mTvNoReviews.setVisibility(View.INVISIBLE);
                    mRvReviews.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoaderReset(Loader<ReviewsMovieResp> loader) {
                Log.d(TAG,"onLoaderReset...");
            }
        };

        getSupportLoaderManager().initLoader(REVIEWS_SEARCH_LOADER, null, reviewsLoader);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ReviewsMovieResp> reviewsSearchLoader = loaderManager.getLoader(REVIEWS_SEARCH_LOADER);
        if (reviewsSearchLoader == null) {
            loaderManager.initLoader(REVIEWS_SEARCH_LOADER, null, reviewsLoader);
        } else {
            loaderManager.restartLoader(REVIEWS_SEARCH_LOADER, null, reviewsLoader);
        }
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
        mIvFavorite.setVisibility(View.VISIBLE);
    }

    private void hideResults(){
        mPosterBg.setVisibility(View.INVISIBLE);
        mMovieTitle.setVisibility(View.INVISIBLE);
        mInnerMovieDetail.setVisibility(View.INVISIBLE);
        mIvFavorite.setVisibility(View.INVISIBLE);
    }

    private void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void performMovieDetailTask(){
//        new PerformMovieDetailTask().execute(movieId);

        detailLoader = new LoaderManager.LoaderCallbacks<MovieDetailResp>() {
            @Override
            public Loader<MovieDetailResp> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<MovieDetailResp>(DetailMovieActivity.this) {
                    private MovieDetailResp movieDetailResp;

                    private void cancelOnError(){
                        new AlertDialog.Builder(DetailMovieActivity.this)
                                .setTitle(R.string.no_network_title)
                                .setMessage(R.string.no_network_message)
                                .setPositiveButton(R.string.accept_button,null)
                                .show();
                        hideResults();
                        hideLoading();
                        showError();
                        cancelLoad();
                    }
                    @Override
                    protected void onStartLoading() {
                        hideResults();
                        hideError();
                        showLoading();

                        if (movieDetailResp != null) {
                            deliverResult(movieDetailResp);
                        }
                        else {
                            if(!NetworkUtils.isNetworkAvailable(DetailMovieActivity.this)){
                                cancelOnError();
                            }
                            else {
                                forceLoad();
                            }
                        }
                    }

                    @Override
                    public MovieDetailResp loadInBackground() {
                        MovieDetailResp resp;
                        try {
                            if(!NetworkUtils.isOnline(NetworkUtils.buildGetPopularMoviesUrl())){
                                cancelOnError();
                            }
                            Log.d(TAG,"Movie id: " + movieId);
                            resp = MOVIES_BO.getMovieDetail(movieId);
                        }
                        catch (Exception e){
                            Log.e(getClass().getSimpleName(),"Error: " + e.getMessage());
                            throw e;
                        }
                        return resp;
                    }

                    @Override
                    public void deliverResult(MovieDetailResp data) {
                        movieDetailResp = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<MovieDetailResp> loader, MovieDetailResp data) {
                hideLoading();
                //Set values onto the views
                mMovieTitle.setText(data.getOriginalTitle());
                mTagline.setText(data.getTagline());
                mSynopsis.setText(data.getOverview());
                mReleaseDate.setText(data.getReleaseDate());
                mRating.setText(String.valueOf(data.getVoteAverage()));
                final StringBuilder txtDuration = new StringBuilder(data.getRuntime().toString()).append(' ').append(getString(R.string.minutes));
                mDuration.setText(txtDuration);

                Picasso.with(DetailMovieActivity.this)
                        .load(NetworkUtils.buildMoviePosterUrl(data.getBackdropPath()).toString())
                        .into(mPosterBg);
                Picasso.with(DetailMovieActivity.this)
                        .load(NetworkUtils.buildMoviePosterUrl(data.getPosterPath()).toString())
                        .into(mPoster);
                showResults();
            }

            @Override
            public void onLoaderReset(Loader<MovieDetailResp> loader) {
                Log.d(TAG,"onLoaderReset...");
            }
        };

        getSupportLoaderManager().initLoader(DETAIL_MOVIE_LOADER, null, detailLoader);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<MovieDetailResp> reviewsSearchLoader = loaderManager.getLoader(DETAIL_MOVIE_LOADER);
        if (reviewsSearchLoader == null) {
            loaderManager.initLoader(DETAIL_MOVIE_LOADER, null, detailLoader);
        } else {
            loaderManager.restartLoader(DETAIL_MOVIE_LOADER, null, detailLoader);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"Destroying loaders...");
        getLoaderManager().destroyLoader(DETAIL_MOVIE_LOADER);
        getLoaderManager().destroyLoader(REVIEWS_SEARCH_LOADER);
        getLoaderManager().destroyLoader(VIDEOS_SEARCH_LOADER);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().restartLoader(DETAIL_MOVIE_LOADER,null,detailLoader);
        getSupportLoaderManager().restartLoader(REVIEWS_SEARCH_LOADER, null, reviewsLoader);
        getSupportLoaderManager().restartLoader(VIDEOS_SEARCH_LOADER, null, videosLoader);
        super.onResume();
    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.udacity.yordan.popularmovies.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String stringId = movieId.toString();
            final Uri uri = FavoriteMoviesContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(stringId).build();
            isFav = intent.getBooleanExtra(FavMoviesIntentService.PARAM_OUT_MSG,false);
            //If movie is marked as favorite
            if(isFav){
                //Favorite image as default
                mIvFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
            //If not...
            else {
                //Unfavorite image as default
                mIvFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            }

            mIvFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isFav){
                        getContentResolver().delete(uri
                                ,null
                                ,null);
                        mIvFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                        isFav = false;
                    }
                    else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);
                        contentValues.put(FavoriteMoviesContract.FavoriteEntry.COLUMN_TITLE, mMovieTitle.getText().toString());

                        Uri uri = getContentResolver().insert(FavoriteMoviesContract.FavoriteEntry.CONTENT_URI, contentValues);

                        if(uri != null) {
                            mIvFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                            Toast.makeText(DetailMovieActivity.this
                                    ,getString(R.string.confirmation_movie_added_fav)
                                    ,Toast.LENGTH_LONG).show();
                            isFav = true;
                        }
                        else {
                            Toast.makeText(DetailMovieActivity.this
                                    ,getString(R.string.message_error_add_fav)
                                    ,Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }
}
