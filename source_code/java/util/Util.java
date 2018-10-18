package com.georgeneokq.dressup.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Util {

    /*
     * Static versions of the methods
     */

    public static int convertDpToPixels(Context ctx, int sizeInDp) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp * scale + 0.5f); // Pixels
    }

    public static int convertPixelsToDp(Context ctx, int sizeInPx) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (sizeInPx / scale + 0.5f); // Dp
    }

    public static void toast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    /*
     * Different variations of the Log methods to conveniently print strings and even integers without converting to string type.
     * If the logMethod (debug, error, info, warn, verbose) is not specified, defaults to "warn".
     */

    public static void log(String tag, String message) {
        Log.w(tag, String.valueOf(message));
    }

    public static void log(String tag, int message) {
        Log.w(tag, String.valueOf(message));
    }

    public static void log(String tag, String message, int logMethod) {

        switch(logMethod) {

            case Log.WARN:
                Log.w(tag, String.valueOf(message));
                break;

            case Log.ERROR:
                Log.e(tag, String.valueOf(message));
                break;

            case Log.INFO:
                Log.i(tag, String.valueOf(message));
                break;

            case Log.DEBUG:
                Log.d(tag, String.valueOf(message));
                break;

            case Log.VERBOSE:
                Log.v(tag, String.valueOf(message));
                break;

            default:
                Log.w(tag, String.valueOf(message));
        }
    }

    public static void log(String tag, int message, int logMethod) {

        switch(logMethod) {

            case Log.WARN:
                Log.w(tag, String.valueOf(message));
                break;

            case Log.ERROR:
                Log.e(tag, String.valueOf(message));
                break;

            case Log.INFO:
                Log.i(tag, String.valueOf(message));
                break;

            case Log.DEBUG:
                Log.d(tag, String.valueOf(message));
                break;

            case Log.VERBOSE:
                Log.v(tag, String.valueOf(message));
                break;

            default:
                Log.w(tag, String.valueOf(message));
        }
    }
}
