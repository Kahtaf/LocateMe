package com.bitspice.locateme;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONArray;
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
     *            longitude : -79.3072776
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
                        JSONArray userData = data.optJSONArray("locations");
                        if (userData != null) {
                            for (int x = 0; x < data.length(); x++) {
                                String username = userData.getJSONObject(x).optString("username", null);
                                String latitude = userData.getJSONObject(x).optString("latitude", null);
                                String longitude = userData.getJSONObject(x).optString("longitude", null);

                                // Do something with this data
                            }
                        }
                    } catch (JSONException e) {
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
                        Toast.makeText(activity, "Connected to web server", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(activity, "Disconnected from web server", Toast.LENGTH_LONG).show();
                    }
                });
            }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Error connecting to web server", Toast.LENGTH_LONG).show();
                    }
                });
            }
    };
}
