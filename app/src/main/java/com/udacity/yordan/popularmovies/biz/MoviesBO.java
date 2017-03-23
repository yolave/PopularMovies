package com.udacity.yordan.popularmovies.biz;

import android.database.Cursor;

import com.udacity.yordan.popularmovies.json.MovieDetailResp;
import com.udacity.yordan.popularmovies.json.MovieVideoResp;
import com.udacity.yordan.popularmovies.json.PopularMoviesResp;
import com.udacity.yordan.popularmovies.json.ReviewsMovieResp;
import com.udacity.yordan.popularmovies.json.TopRatedMoviesResp;

import java.util.List;

/**
 * Contract that define the operations that a user can perform.
 * Created by Yordan on 1/29/2017.
 */
public interface MoviesBO {
    /**
     *  Method that return the list of the popular movies in a JSON object.
     * @return Object populated with the popular movies data
     */
    PopularMoviesResp getPopularMovies();

    /**
     * Method that return the list of the top rated movies in a JSON object.
     * @return Object populated with the top rated movies data
     */
    TopRatedMoviesResp getTopRatedMovies();

    /**
     * Method that return the detail of specific movie, identified by its id in a JSON object.
     * @param movieId id of the movie to fetch
     * @return Object populated with the selected movie data
     */
    MovieDetailResp getMovieDetail(Integer movieId);

    MovieVideoResp getMovieVideos(Integer movieId);

    ReviewsMovieResp getMovieReviews(Integer movieId);

    List<MovieDetailResp> getFavoritesMovies(Cursor cursor);
}
