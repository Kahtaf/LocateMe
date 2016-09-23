package com.bitspice.locateme;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Kahtaf on 9/19/2016.
 */
public class SocketManager {
    private boolean isConnected = false;
    private Socket socket;
    private Activity activity;

    public SocketManager(Socket socket, Activity activity){
        this.socket = socket;
        this.activity = activity;
    }

    public void connect(){
        if (socket != null){
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.on(Constants.RECEIVE_LOCATION_UPDATE, receiveLocationUpdate);
            socket.on(Constants.USER_JOINED, receiveUserJoinedUpdate);
            socket.connect();
        }
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.off(Socket.EVENT_CONNECT, onConnect);
            socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            socket.off(Constants.RECEIVE_LOCATION_UPDATE, receiveLocationUpdate);
            socket.off(Constants.USER_JOINED, receiveUserJoinedUpdate);
        }
    }

    /**
     * Send location data as JSON to the server in the format:
     * {
     *   username : Riley,
     *   latitude : 43.741866,
     *   longitude : -79.307277
     * }
     * @param username
     * @param longitude
     * @param latitude
     */
    public void sendLocation(String username, double longitude, double latitude){
        JSONObject locationData = new JSONObject();
        try{
            locationData.put("username", username);
            locationData.put("longitude", longitude);
            locationData.put("latitude", latitude);
            socket.emit(Constants.SEND_LOCATION_UPDATE, locationData);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void addUser(String username){
        socket.emit(Constants.ADD_USER, username);
    }

    /**
     * Received JSON from server of user locations in the format:
     * {
     *     locations: [
     *          {
     *            username : Riley,
     *            latitude : 43.741866,
     *            longitude : -79.3072776
     *          },
     *          {
     *            username : Kahtaf,
     *            latitude : 43.741866,
     *             longitude : -79.3072776
     *          },
     *          ...
     *     ]
     * }
     */
    private Emitter.Listener receiveLocationUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        JSONObject data = (JSONObject) args[0];
                        String username = data.optString("username", null).toString();
                        double latitude = data.optDouble("latitude", 0);
                        double longitude = data.optDouble("longitude", 0);
                        Log.i("Socket Manager: ", String.format("%s has moved to (%f, %f)" , username, latitude, longitude));

                        if (activity instanceof MapsActivity){
                            ((MapsActivity) activity).updateUserLocation(username, latitude, longitude);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener receiveUserJoinedUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String username = data.optString("username", null);
                        if(username != null) {
                            Toast.makeText(activity, activity.getString(R.string.user_joined, username), Toast.LENGTH_LONG).show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        Toast.makeText(activity, activity.getString(R.string.connected), Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isConnected = false;
                        Toast.makeText(activity, activity.getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                    }
                });
            }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String errorMsg = args[0].toString();
                        Toast.makeText(activity, activity.getString(R.string.error_connecting, errorMsg), Toast.LENGTH_LONG).show();
                    }
                });
            }
    };
}
