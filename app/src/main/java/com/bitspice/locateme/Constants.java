package com.bitspice.locateme;

/**
 * Created by Kahtaf on 9/19/2016.
 */
public class Constants {

    // Socket constants
    public static final String SERVER_URL = "http://192.168.0.17:3000/";                   // Websocket location
    public static final String SEND_LOCATION_UPDATE = "SEND_LOCATION_UPDATE";           // Listen for this event on server
    public static final String RECEIVE_LOCATION_UPDATE = "RECEIVE_LOCATION_UPDATE";     // Send this event from server
    public static final String ADD_USER = "ADD_USER";                                   // send to server. Add user event
    public static final String USER_JOINED = "USER_JOINED";                             // sent from server after ADD_USER

    // Location updates
    public static final int MINIMUM_DISTANCE_BEFORE_LOCATION_UPDATE = 10;              // meters
    public static final int MINIMUM_TIME_BEFORE_LOCATION_UPDATE = 2 * 6 * 1000;        // 0.2 mins
}
