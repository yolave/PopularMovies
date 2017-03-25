package com.udacity.yordan.popularmovies.intents;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.udacity.yordan.popularmovies.DetailMovieActivity;
import com.udacity.yordan.popularmovies.data.FavoriteMoviesContract;

/**
 * Created by Yordan on 3/24/2017.
 */

public class FavMoviesIntentService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";

    public FavMoviesIntentService(){
        super("FavMoviesIntentService");
    }

    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        String stringId = intent.getStringExtra(PARAM_IN_MSG);
        final Uri uri = FavoriteMoviesContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(stringId).build();

        Cursor cursor = getContentResolver().query(uri
                ,null
                ,null
                ,null
                ,null);

        boolean isFav = cursor != null && cursor.getCount() > 0;
        if(cursor != null){
            cursor.close();
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DetailMovieActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, isFav);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(broadcastIntent);
    }
}
