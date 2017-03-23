package com.udacity.yordan.popularmovies.biz.impl;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.udacity.yordan.popularmovies.biz.MoviesBO;
import com.udacity.yordan.popularmovies.data.FavoriteMoviesContract;
import com.udacity.yordan.popularmovies.json.MovieDetailResp;
import com.udacity.yordan.popularmovies.json.MovieVideoResp;
import com.udacity.yordan.popularmovies.json.PopularMoviesResp;
import com.udacity.yordan.popularmovies.json.ReviewsMovieResp;
import com.udacity.yordan.popularmovies.json.TopRatedMoviesResp;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the services used to retrieve the data from the server
 * Created by Yordan on 1/27/2017.
 */

public class MoviesBOImpl implements MoviesBO{
    private static final String TAG = MoviesBOImpl.class.getSimpleName();

    @Override
    public PopularMoviesResp getPopularMovies(){
        PopularMoviesResp resp;
        try {
            URL url = NetworkUtils.buildGetPopularMoviesUrl();
            Log.d(TAG,"URL -> " + url.toString());
            String rawJson = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
            resp = gson.fromJson(rawJson,PopularMoviesResp.class);
        } catch (IOException e) {
            Log.e(TAG,"Error while popular movies were retrieved: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return resp;
    }

    @Override
    public TopRatedMoviesResp getTopRatedMovies(){
        TopRatedMoviesResp resp;

        try {
            URL url = NetworkUtils.buildGetTopRatedMoviesUrl();
            Log.d(TAG,"URL -> " + url.toString());
            String rawJson = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
            resp = gson.fromJson(rawJson,TopRatedMoviesResp.class);
        } catch (IOException e) {
            Log.e(TAG,"Error while top rated movies were retrieved: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return resp;
    }

    @Override
    public MovieDetailResp getMovieDetail(Integer movieId){
        MovieDetailResp resp;

        try {
            URL url = NetworkUtils.buildGetDetailsMovieUrl(movieId.toString());
            Log.d(TAG,"URL -> " + url.toString());
            String rawJson = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
            resp = gson.fromJson(rawJson,MovieDetailResp.class);
        } catch (IOException e) {
            Log.e(TAG,"Error while the detail of the movie id '" + movieId.toString() + "' was retrieved: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return resp;
    }

    @Override
    public MovieVideoResp getMovieVideos(final Integer movieId) {
        MovieVideoResp resp;

        try {
            URL url = NetworkUtils.buildVideosMovieUrl(movieId.toString());
            Log.d(TAG,"URL -> " + url.toString());
            String rawJson = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
            resp = gson.fromJson(rawJson,MovieVideoResp.class);
        } catch (IOException e) {
            Log.e(TAG,"Error ocurred while retrieving the videos for movie id '" + movieId.toString() + "': " + e.getMessage());
            throw new RuntimeException(e);
        }
        return resp;
    }

    @Override
    public ReviewsMovieResp getMovieReviews(Integer movieId) {
        ReviewsMovieResp resp;

        try {
            URL url = NetworkUtils.buildReviewsMovieUrl(movieId.toString());
            Log.d(TAG,"URL -> " + url.toString());
            String rawJson = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
            resp = gson.fromJson(rawJson,ReviewsMovieResp.class);
        } catch (IOException e) {
            Log.e(TAG,"Error ocurred while retrieving the reviews for movie id '" + movieId.toString() + "': " + e.getMessage());
            throw new RuntimeException(e);
        }
        return resp;
    }

    @Override
    public List<MovieDetailResp> getFavoritesMovies(Cursor cursor){

        List<MovieDetailResp> movieDetailResps = new ArrayList<>(0);
        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int movieIdIndex = cursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID);
                Integer id = cursor.getInt(movieIdIndex);
                MovieDetailResp movieDetailResp = getMovieDetail(id);
                movieDetailResps.add(movieDetailResp);
            }
            cursor.close();
        }
        return movieDetailResps;
    }
}
