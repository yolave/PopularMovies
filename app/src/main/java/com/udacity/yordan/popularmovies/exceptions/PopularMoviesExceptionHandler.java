package com.udacity.yordan.popularmovies.exceptions;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.udacity.yordan.popularmovies.R;

/**
 * Created by Yordan on 2/9/2017.
 */

public class PopularMoviesExceptionHandler implements Thread.UncaughtExceptionHandler {
    Activity activity;
    @Override
    public void uncaughtException(Thread t, final Throwable e) {
        Log.e(getClass().getSimpleName(),"Error ocurred in app execution: " + e.getMessage());
        Log.e(getClass().getSimpleName(),Log.getStackTraceString(e));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = String.format(activity.getString(R.string.error_message),e.getCause());
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.error_title)
                        .setMessage(message)
                        .setPositiveButton(R.string.exit_app_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        })
                        .show();
            }
        });
    }

    public PopularMoviesExceptionHandler(Activity activity) {
        this.activity = activity;
    }
}
