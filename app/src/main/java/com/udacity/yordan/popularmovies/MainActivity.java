package com.udacity.yordan.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.yordan.popularmovies.biz.MoviesBO;
import com.udacity.yordan.popularmovies.biz.impl.MoviesBOImpl;
import com.udacity.yordan.popularmovies.data.FavoriteMoviesContract;
import com.udacity.yordan.popularmovies.exceptions.PopularMoviesExceptionHandler;
import com.udacity.yordan.popularmovies.json.MovieDetailResp;
import com.udacity.yordan.popularmovies.json.MovieVideoResp;
import com.udacity.yordan.popularmovies.json.ResultPopularMovie;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;
import com.udacity.yordan.popularmovies.view.FavoriteMoviesAdapter;
import com.udacity.yordan.popularmovies.view.PopularMoviesAdapter;
import com.udacity.yordan.popularmovies.view.TopRatedMoviesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getName();
    private int selectedItem = 0;
    private static final int POPULAR_MOVIES_OPTION = 0;
    private static final int TOP_RATED_MOVIES_OPTION = 1;
    private static final int FAVORITES_MOVIES_OPTION = 2;
    private static final int SPAN_COUNT = 2;
    @BindView(R.id.rv_movies)
    RecyclerView mMovieList;
    @BindView(R.id.pb_loading)
    ProgressBar mProgressBar;
    @BindView(R.id.ib_reload)
    ImageButton mReloadButton;
    @BindView(R.id.tv_no_movies)
    TextView mTvNoMovies;

    private static final int MOVIES_LOADER = 111;
    private LoaderManager.LoaderCallbacks<RecyclerView.Adapter> moviesLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            selectedItem = savedInstanceState.getInt(getString(R.string.SELECTED_OPTION_KEY));
        }
        Thread.setDefaultUncaughtExceptionHandler(new PopularMoviesExceptionHandler(this));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mReloadButton.setOnClickListener(this);
        performMovieFetch(POPULAR_MOVIES_OPTION);
    }

    private void performMovieFetch(Integer option){
        moviesLoader = new LoaderManager.LoaderCallbacks<RecyclerView.Adapter>() {
            @Override
            public Loader<RecyclerView.Adapter> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<RecyclerView.Adapter>(MainActivity.this) {
                    private RecyclerView.Adapter adapter;
                    private Bundle bundle;
                    private MoviesBO moviesBO = new MoviesBOImpl();

                    private void cancelOnError(){
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.no_network_title)
                                .setMessage(R.string.no_network_message)
                                .setPositiveButton(R.string.accept_button,null)
                                .show();
                        hideResults();
                        hideLoading();
                        hideMessage();
                        showError();
                        cancelLoad();
                    }

                    @Override
                    protected void onStartLoading() {
                        hideResults();
                        hideError();
                        hideMessage();
                        showLoading();
                        if(adapter != null){
                            deliverResult(adapter);
                        }
                        else {
                            if(!NetworkUtils.isNetworkAvailable(MainActivity.this)){
                                cancelOnError();
                            }
                            else {
                                bundle = args;
                                forceLoad();
                            }
                        }
                    }

                    @Override
                    public RecyclerView.Adapter loadInBackground() {
                        RecyclerView.Adapter adapter = null;
                        List<ResultPopularMovie> resultPopularMovie;
                        int option = bundle.getInt(getString(R.string.SELECTED_OPTION_KEY));

                        switch (option) {
                            //POPULAR MOVIES
                            case MainActivity.POPULAR_MOVIES_OPTION:
                                resultPopularMovie = moviesBO.getPopularMovies().getResultPopularMovies();
                                adapter = new PopularMoviesAdapter(resultPopularMovie,MainActivity.this);
                                break;
                            //TOP RATED MOVIES
                            case MainActivity.TOP_RATED_MOVIES_OPTION:
                                resultPopularMovie = moviesBO.getTopRatedMovies().getResultPopularMovies();
                                adapter = new TopRatedMoviesAdapter(resultPopularMovie,MainActivity.this);
                                break;
                            //FAVORITES (LOCAL) MOVIES
                            case MainActivity.FAVORITES_MOVIES_OPTION:
                                Cursor cursor = getContentResolver().query(FavoriteMoviesContract.FavoriteEntry.CONTENT_URI
                                        ,null
                                        ,null
                                        ,null
                                        ,null);

                                List<MovieDetailResp> movieDetailResps = moviesBO.getFavoritesMovies(cursor);
                                adapter = new FavoriteMoviesAdapter(movieDetailResps,MainActivity.this);
                                break;
                        }
                        return adapter;
                    }

                    @Override
                    public void deliverResult(RecyclerView.Adapter data) {
                        adapter = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<RecyclerView.Adapter> loader, RecyclerView.Adapter data) {
                hideLoading();
                if(data.getItemCount() > 0) {
                    mMovieList.setAdapter(data);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, SPAN_COUNT);
                    mMovieList.setLayoutManager(mLayoutManager);
                    showResults();
                }
                else {
                    hideResults();
                    showMessage();
                }
            }

            @Override
            public void onLoaderReset(Loader<RecyclerView.Adapter> loader) {

            }
        };

        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.SELECTED_OPTION_KEY),option);
        getSupportLoaderManager().initLoader(MOVIES_LOADER, null, moviesLoader);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<MovieVideoResp> reviewsSearchLoader = loaderManager.getLoader(MOVIES_LOADER);
        if (reviewsSearchLoader == null) {
            loaderManager.initLoader(MOVIES_LOADER, bundle, moviesLoader);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER, bundle, moviesLoader);
        }
    }

    private void showError(){
        mReloadButton.setVisibility(View.VISIBLE);
    }

    private void hideError(){
        mReloadButton.setVisibility(View.INVISIBLE);
    }

    private void showResults(){
        mMovieList.setVisibility(View.VISIBLE);
    }

    private void hideResults(){
        mMovieList.setVisibility(View.INVISIBLE);
    }

    private void showMessage(){
        mTvNoMovies.setVisibility(View.VISIBLE);
    }

    private void hideMessage(){
        mTvNoMovies.setVisibility(View.INVISIBLE);
    }

    private void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = true;
        try {
            getMenuInflater().inflate(R.menu.main, menu);
        } catch (final Exception e) {
            result = super.onCreateOptionsMenu(menu);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == R.id.mi_sort) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sort_by_title)
                    .setSingleChoiceItems(R.array.sort_options
                            , selectedItem
                            , null)
                    .setIcon(R.drawable.ic_sort_white_24dp)
                    .setPositiveButton(R.string.accept_button,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                            switch (position) {
                                case POPULAR_MOVIES_OPTION:
                                    selectedItem = POPULAR_MOVIES_OPTION;
                                    performMovieFetch(POPULAR_MOVIES_OPTION);
                                    break;
                                case TOP_RATED_MOVIES_OPTION:
                                    selectedItem = TOP_RATED_MOVIES_OPTION;
                                    performMovieFetch(TOP_RATED_MOVIES_OPTION);
                                    break;
                                case FAVORITES_MOVIES_OPTION:
                                    selectedItem = FAVORITES_MOVIES_OPTION;
                                    performMovieFetch(FAVORITES_MOVIES_OPTION);
                                    break;
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel_button,null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"Destroying loaders...");
        getLoaderManager().destroyLoader(MOVIES_LOADER);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.SELECTED_OPTION_KEY), selectedItem);
        getSupportLoaderManager().restartLoader(MOVIES_LOADER, bundle, moviesLoader);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.SELECTED_OPTION_KEY),selectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_movie_poster:
                Intent i = new Intent(MainActivity.this,DetailMovieActivity.class);
                Bundle bundle = new Bundle(1);
                bundle.putInt(getString(R.string.MOVIE_ID_TAG),(Integer) v.getTag());
                i.putExtras(bundle);
                startActivity(i);
                break;
            case R.id.ib_reload:
                performMovieFetch(selectedItem);
                break;
            default:
                break;
        }
    }
}
