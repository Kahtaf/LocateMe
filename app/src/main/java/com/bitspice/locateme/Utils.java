package com.bitspice.locateme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kahtaf on 9/23/2016.
 */

public class Utils {

    public static void saveUsername(Activity activity, String username){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.SAVED_USER_NAME, username);
        editor.commit();
    }

    public static String getUsername(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.SAVED_USER_NAME, null);
    }
}
