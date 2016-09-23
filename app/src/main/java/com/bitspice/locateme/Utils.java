package com.bitspice.locateme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Kahtaf on 9/23/2016.
 */

public class Utils {

    public static void saveUsername(Context context, String username){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.SAVED_USER_NAME, username);
        editor.commit();
    }

    public static String getUsername(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(Constants.SAVED_USER_NAME, null);
    }

    public static void saveServerURL(Context context, String url){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.SAVED_SERVER_URL, url);
        editor.commit();
    }

    public static String getServerURL(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(Constants.SAVED_SERVER_URL, null);
    }
}
