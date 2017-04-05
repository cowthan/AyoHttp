
package com.android.volley.error;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.R;


public class VolleyErrorHelper {
    /**
     * Returns appropriate message which is to be displayed to the user against
     * the specified error object.
     * 
     * @param error
     * @param context
     * @return
     */
    public static String getMessage(Object error, Context context) {
        if (context == null || error == null || !(error instanceof VolleyError))
            return "请求失败！";
        if (error instanceof TimeoutError) {
            return context.getResources().getString(R.string.time_out);
        } else if (isNetworkProblem(error)) {
            return context.getResources().getString(R.string.internet_notgood);
        } else
            return handleServerError(error, context);
    }

    /**
     * Determines whether the error is related to network
     * 
     * @param error
     * @return
     */
    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }

    /**
     * Determines whether the error is related to server
     * 
     * @param error
     * @return
     */
    public static boolean isServerProblem(Object error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

    /**
     * Handles the server error, tries to determine whether to show a stock
     * message or to show a message retrieved from the server.
     * 
     * @param err
     * @param context
     * @return
     */
    private static String handleServerError(Object err, Context context) {
        if (context == null || err == null || !(err instanceof VolleyError))
            return "请求失败！";
        VolleyError error = (VolleyError)err;
        NetworkResponse response = error.networkResponse;

        if (response != null) {
            switch (response.statusCode) {
                case 404:
                case 422:
                case 401:
                case 400:
                case 500:
                case 405:
                    return "网络连接失败 (" + response.statusCode + ")";
                default:
                    return "网络连接失败";
            }
        }
        return context.getResources().getString(R.string.generic_error);
    }
}
