package com.udacity.yordan.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
//import android.widget.TextView;

import com.udacity.yordan.popularmovies.biz.MoviesBO;
import com.udacity.yordan.popularmovies.biz.impl.MoviesBOImpl;
import com.udacity.yordan.popularmovies.exceptions.PopularMoviesExceptionHandler;
import com.udacity.yordan.popularmovies.json.Result;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;
import com.udacity.yordan.popularmovies.view.PopularMoviesAdapter;
import com.udacity.yordan.popularmovies.view.TopRatedMoviesAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private int selectedItem = 0;
    private static final int POPULAR_MOVIES_OPTION = 0;
    private static final int TOP_RATED_MOVIES_OPTION = 1;
    private static final int SPAN_COUNT = 2;
    private RecyclerView mMovieList;
    private ProgressBar mProgressBar;
    private ImageView mReloadButton;
//    private TextView mErrorMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new PopularMoviesExceptionHandler(this));
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar)findViewById(R.id.pb_loading);
        mMovieList = (RecyclerView) findViewById(R.id.rv_movies);
//        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mReloadButton = (ImageButton)findViewById(R.id.ib_reload);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performMovieFetch(selectedItem);
            }
        });
        performMovieFetch(POPULAR_MOVIES_OPTION);
    }

    private void performMovieFetch(Integer option){
        new MovieFetchCallTask().execute(option);
    }

    private void showError(){
//        mErrorMessage.setText(getString(R.string.no_internet_message));
//        mErrorMessage.setVisibility(View.VISIBLE);
        mReloadButton.setVisibility(View.VISIBLE);
    }

    private void hideError(){
//        mErrorMessage.setVisibility(View.INVISIBLE);
        mReloadButton.setVisibility(View.INVISIBLE);
    }

    private void showResults(){
        mMovieList.setVisibility(View.VISIBLE);
    }

    private void hideResults(){
        mMovieList.setVisibility(View.INVISIBLE);
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
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel_button,null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
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
            default:
                break;
        }
    }

    private class MovieFetchCallTask extends AsyncTask<Integer, Void, RecyclerView.Adapter> {
        private final MoviesBO moviesBO = new MoviesBOImpl();
        private final String TAG = getClass().getSimpleName();

        @Override
        protected RecyclerView.Adapter doInBackground(Integer... params) {
            RecyclerView.Adapter adapter = null;
            List<Result> result;
            switch (params[0]) {
                case MainActivity.POPULAR_MOVIES_OPTION:
                    if(!NetworkUtils.isOnline()){
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.no_internet_title)
                                .setMessage(R.string.no_internet_message)
                                .setPositiveButton(R.string.accept_button,null)
                                .show();
                        cancel(true);
                    }
                    result = moviesBO.getPopularMovies().getResults();
                    adapter = new PopularMoviesAdapter(result,MainActivity.this);
                    break;
                case MainActivity.TOP_RATED_MOVIES_OPTION:
                    if(!NetworkUtils.isOnline()){
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.no_internet_title)
                                .setMessage(R.string.no_internet_message)
                                .setPositiveButton(R.string.accept_button,null)
                                .show();
                        cancel(true);
                    }
                    result = moviesBO.getTopRatedMovies().getResults();
                    adapter = new TopRatedMoviesAdapter(result,MainActivity.this);
                    break;
            }
            return adapter;
        }

        @Override
        protected void onPreExecute() {
            if(!NetworkUtils.isNetworkAvailable(MainActivity.this)){
                new AlertDialog.Builder(MainActivity.this)
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
        protected void onPostExecute(RecyclerView.Adapter s) {
            hideLoading();
            mMovieList.setAdapter(s);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, SPAN_COUNT);
            mMovieList.setLayoutManager(mLayoutManager);
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
