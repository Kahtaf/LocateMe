package com.bitspice.locateme;

/**
 * Created by Kahtaf on 9/19/2016.
 */
public class Constants {

    // Socket constants
    public static final String SERVER_URL = "http://chat.socket.io/";                   // Websocket location
    public static final String SEND_LOCATION_UPDATE = "SEND_LOCATION_UPDATE";           // Listen for this event on server
    public static final String RECEIVE_LOCATION_UPDATE = "RECEIVE_LOCATION_UPDATE";     // Send this event from server

    // Location updates
    public static final int MINIMUM_DISTANCE_BEFORE_LOCATION_UPDATE = 100;              // meters
    public static final int MINIMUM_TIME_BEFORE_LOCATION_UPDATE = 2 * 60 * 1000;        // 2 mins
}
