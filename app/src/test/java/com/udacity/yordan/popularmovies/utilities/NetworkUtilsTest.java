package com.udacity.yordan.popularmovies.utilities;

import junit.framework.Assert;

import org.junit.Test;

import java.net.URL;

/**
 * Created by Yordan on 2/2/2017.
 */
public class NetworkUtilsTest {
    private static final String TAG = NetworkUtilsTest.class.getSimpleName();
    @Test
    public void getResponseFromHttpUrl() throws Exception {

    }

    @Test
    public void buildUrl() throws Exception {

    }

    @Test
    public void buildGetPopularMoviesUrl() throws Exception {

    }

    @Test
    public void buildGetTopRatedMoviesUrl() throws Exception {

    }

    @Test
    public void buildGetDetailsMovieUrl() throws Exception {

    }

    @Test
    public void isOnline_isCorrect() throws Exception {
        Assert.assertTrue(NetworkUtils.isOnline());
    }

    @Test
    public void isOnlineWithParams_isCorrect() throws Exception {
        URL url = new URL("http://www.google.es");
        boolean result = NetworkUtils.isOnline(url);
        System.out.println("Resultado validación '" + url + "' -> " + result);
        Assert.assertTrue(result);
    }

    @Test
    public void isOnlineWithParams_isIncorrect() throws Exception {
        URL url = new URL("http://www.mandanga.cl");
        boolean result = NetworkUtils.isOnline(url);
        System.out.println("Resultado validación '" + url + "' -> " + result);
        Assert.assertFalse(result);
    }

    @Test
    public void buildMoviePosterUrl() throws Exception {

    }

    @Test
    public void isNetworkAvailable() throws Exception {

    }

}