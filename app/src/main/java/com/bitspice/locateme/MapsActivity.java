package com.bitspice.locateme;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Kahtaf on 9/22/2016.
 */

public class MapsActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    private FloatingActionButton floatingActionButton;
    private Location lastLocation;
    private String username;

    private LocationManager locationManager;
    private SocketManager socketManager;
    private Socket socket;
    private HashMap<String, Marker> markerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.map_access_token));
        setContentView(R.layout.activity_map);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            socket = IO.socket(Utils.getServerURL(this));
        } catch (URISyntaxException e) {
            Toast.makeText(this, getString(R.string.invalid_server), Toast.LENGTH_SHORT);
            finish();
        }
        socketManager = new SocketManager(socket, this);

        username = Utils.getUsername(this);
        markerMap = new HashMap<>();

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setMyLocationEnabled(true);
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    centerCamera(lastLocation);
                }
            }
        });

        startLocationUpdates();
    }

    /**
     * Received user location from server, update corresponding marker
     * @param username
     * @param lat
     * @param lng
     */
    public void updateUserLocation(String username, double lat, double lng){
        if (username != null && map != null){
            Marker userMarker;
            if (markerMap.containsKey(username)){
                userMarker = markerMap.get(username);
            } else {
                MarkerOptions markerOptions = new MarkerOptions().setPosition(new LatLng(lat, lng)).setTitle(username);
                userMarker = map.addMarker(markerOptions);
                markerMap.put(username, userMarker);
            }

            if (userMarker != null){
                userMarker.setPosition(new LatLng(lat, lng));
            }
        }
    }

    /**
     * Location listener to keep track of our location
     */
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (lastLocation == null){
                centerCamera(location);
            }

            lastLocation = location;
            socketManager.sendLocation(username, location.getLongitude(), location.getLatitude());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    private void startLocationUpdates(){
        // Check if user has granted location permission and start location updates
        if (hasLocationPermission()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constants.MINIMUM_TIME_BEFORE_LOCATION_UPDATE, Constants.MINIMUM_DISTANCE_BEFORE_LOCATION_UPDATE, locationListener);
            socketManager.connect();

            socketManager.addUser(username);
        }
    }

    private void stopLocationUpdates(){
        if (hasLocationPermission()) {
            locationManager.removeUpdates(locationListener);
            // TODO - remove user here

            socketManager.disconnect();
        }
    }

    private void centerCamera(Location location){
        if (location != null && map != null){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), Constants.MAP_ZOOM));
        }
    }

    private boolean hasLocationPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
