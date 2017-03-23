package com.udacity.yordan.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.udacity.yordan.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    /**
     * Base URL to connect to themoviedb.org
     */
    public static final String MOVIE_DB_URL =
            "https://api.themoviedb.org/3/movie/";

    private static final String MOVIE_DB_BASE_URL = "https://www.themoviedb.org";

    final private static String POSTER_BASE_URL = "http://image.tmdb.org/t/p";

    final private static String POSTER_DEFAULT_SIZE = "w185";
    /**
     * Part of the URL to append in order to get the top rated movies
     */
    final private static String TOP_RATED_MOVIES_SECTION = "top_rated";
    /**
     * Part of the URL to append in order to get the most popular movies
     */
    final private static String POPULAR_MOVIES_SECTION = "popular";

    final private static String REVIEWS_MOVIE = "reviews";

    final private static String VIDEOS_MOVIE = "videos";
    /**
     * Parameter to construct the URL: api_key
     */
    final private static String PARAM_API = "api_key";
    /**
     * App API key to make the calls to themoviedb.org site. It's setted on gradle.properties file, so avoid writing this explicitly in
     * any part of the code. For test purposes only.
     */
    public static final String API_KEY = BuildConfig.API_KEY;
    /**
     * Parameter to construct the URL: language
     */
    final private static String PARAM_LANGUAGE = "language";
    /**
     * Parameter to construct the URL: page
     */
    final private static String PARAM_PAGE = "page";
    /**
     * Default value for the "language" param
     */
    final private static String VALUE_LANGUAGE = "en-US";
    /**
     * Default value for the "page" param
     */
    final private static String VALUE_PAGE = "1";
    /**
     *
     */
    private static final String YOUTUBE_THUMBNAIL_WILDCARD_URL = "https://img.youtube.com/vi/%s/hqdefault.jpg";

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String response = null;
        try {
            switch (urlConnection.getResponseCode()){
                case 200:
                    InputStream in = urlConnection.getInputStream();

                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");

                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {
                        response = scanner.next();
                    }
                    break;
                case 401:
                    throw new RuntimeException("Invalid API key: You must be granted a valid key");
                case 404:
                    throw new RuntimeException("The resource you requested could not be found");
            }
        } finally {
            urlConnection.disconnect();
        }
        return response;
    }

    /**
     * Builds the URL used to query the server (themoviedb.org in this case). Although, this method can be used to construct any
     * URL with params (generic use), this is themoviedb.org oriented as the api key is validated before construct it.
     * @param baseUrl base URL of the server
     * @param params params to construct the query
     * @return The URL to use to query the server
     */
    public static URL buildUrl(final String baseUrl, final Map<String,String> params){
        if(params != null && params.containsKey(PARAM_API) && (params.get(PARAM_API) == null || params.get(PARAM_API).isEmpty())){
            throw new RuntimeException("No api key informed. Check your gradle.properties file (API_KEY=YOUR_API_KEY)");
        }

        Uri.Builder builder =
                Uri.parse(baseUrl).buildUpon();
        if(params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Uri builtUri = builder.build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL used to query the list of popular movies at themoviedb.org
     * @return URL to use to query the list of popular movies.
     */
    public static URL buildGetPopularMoviesUrl(){
        Map<String,String> params = new HashMap<>(0);
        params.put(PARAM_API,API_KEY);
        params.put(PARAM_LANGUAGE,VALUE_LANGUAGE);
        params.put(PARAM_PAGE,VALUE_PAGE);
        return buildUrl(MOVIE_DB_URL + POPULAR_MOVIES_SECTION,params);
    }

    /**
     * Builds the URL used to query the list of the top rated movies at themoviedb.org
     * @return URL to use to query the list of the top rated movies.
     */
    public static URL buildGetTopRatedMoviesUrl(){
        Map<String,String> params = new HashMap<>(0);
        params.put(PARAM_API,API_KEY);
        params.put(PARAM_LANGUAGE,VALUE_LANGUAGE);
        params.put(PARAM_PAGE,VALUE_PAGE);
        return buildUrl(MOVIE_DB_URL + TOP_RATED_MOVIES_SECTION,params);
    }

    /**
     * Builds the URL used to query to get the detailed info of a movie at themoviedb.org
     * @param movieId unique ID of the movie used to consult for.
     * @return URL to use to get the detailed info for the movie ID.
     */
    public static URL buildGetDetailsMovieUrl(final String movieId){
        Map<String,String> params = new HashMap<>(0);
        params.put(PARAM_API,API_KEY);
        params.put(PARAM_LANGUAGE,VALUE_LANGUAGE);
        return buildUrl(MOVIE_DB_URL + movieId,params);
    }

    public static URL buildReviewsMovieUrl(final String movieId){
        Map<String,String> params = new HashMap<>(0);
        params.put(PARAM_API,API_KEY);
        params.put(PARAM_LANGUAGE,VALUE_LANGUAGE);
        return buildUrl(MOVIE_DB_URL + movieId + '/' + REVIEWS_MOVIE,params);
    }

    public static URL buildVideosMovieUrl(final String movieId){
        Map<String,String> params = new HashMap<>(0);
        params.put(PARAM_API,API_KEY);
        params.put(PARAM_LANGUAGE,VALUE_LANGUAGE);
        return buildUrl(MOVIE_DB_URL + movieId + '/' + VIDEOS_MOVIE,params);
    }

    public static boolean isOnline(@NonNull URL url){
        boolean result = false;
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            if(conn.getResponseCode() == 200) {
                result = true;
            }
        }
        catch (IOException ignored){}
        finally {
            if(conn != null){
                conn.disconnect();
            }
        }
        return result;
    }

    public static boolean isOnline(){
        boolean result = false;
        HttpURLConnection conn = null;
        URL url = buildUrl(MOVIE_DB_BASE_URL,null);

        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            if(conn.getResponseCode() == 200) {
                result = true;
            }
        }
        catch (IOException ignored){}
        finally {
            if(conn != null){
                conn.disconnect();
            }
        }
        return result;
    }

    /**
     *
     * @param path
     * @return
     */
    public static URL buildMoviePosterUrl(final String path){
        return buildUrl(POSTER_BASE_URL +
                        '/' +
                        POSTER_DEFAULT_SIZE +
                        '/' +
                        path
                ,null);
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean rdo = false;
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            Network[] info = connectivity.getAllNetworks();
            if (info != null) {
                for (Network anInfo : info) {
                    NetworkInfo ntInfo = connectivity.getNetworkInfo(anInfo);
                    if (ntInfo.getState() == NetworkInfo.State.CONNECTED) {
                        rdo = true;
                    }
                }
            }
        }
        return rdo;
    }

    public static String generateYoutubeImageThumbUrl(@NonNull String key){
        return String.format(YOUTUBE_THUMBNAIL_WILDCARD_URL,key);
    }
}