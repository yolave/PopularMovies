package com.udacity.yordan.popularmovies.biz.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.udacity.yordan.popularmovies.biz.MoviesBO;
import com.udacity.yordan.popularmovies.json.MovieDetailResp;
import com.udacity.yordan.popularmovies.json.PopularMoviesResp;
import com.udacity.yordan.popularmovies.json.TopRatedMoviesResp;
import com.udacity.yordan.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Implementation of the services used to retrieve the data from the server
 * Created by Yordan on 1/27/2017.
 */

public class MoviesBOImpl implements MoviesBO{
    private static final String TAG = MoviesBOImpl.class.getSimpleName();

    public PopularMoviesResp getPopularMovies(){
       PopularMoviesResp resp = null;
        try {
            URL url = NetworkUtils.buildGetPopularMoviesUrl();
            String rawJson = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
            resp = gson.fromJson(rawJson,PopularMoviesResp.class);
        } catch (IOException e) {
            Log.e(TAG,"Error while popular movies were retrieved: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return resp;
    }

    public TopRatedMoviesResp getTopRatedMovies(){
        TopRatedMoviesResp resp = null;

        try {
            URL url = NetworkUtils.buildGetTopRatedMoviesUrl();
            String rawJson = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
            resp = gson.fromJson(rawJson,TopRatedMoviesResp.class);
        } catch (IOException e) {
            Log.e(TAG,"Error while top rated movies were retrieved: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return resp;
    }

    public MovieDetailResp getMovieDetail(Integer movieId){
        MovieDetailResp resp = null;

        try {
            URL url = NetworkUtils.buildGetDetailsMovieUrl(movieId.toString());
            String rawJson = NetworkUtils.getResponseFromHttpUrl(url);
            Gson gson = new Gson();
           resp = gson.fromJson(rawJson,MovieDetailResp.class);
        } catch (IOException e) {
            Log.e(TAG,"Error while the detail of the movie id '" + movieId.toString() + "' was retrieved: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return resp;
    }
}
