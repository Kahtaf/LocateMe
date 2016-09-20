package com.bitspice.locateme;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Kahtaf on 9/19/2016.
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    private static final String SAVED_USER_NAME = "SAVED_USER_NAME";

    private LocationManager locationManager;
    private EditText nameInput;
    private Switch locationSwitch;
    private SocketManager socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameInput = (EditText) findViewById(R.id.name);
        locationSwitch = (Switch) findViewById(R.id.locationSwitch);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        socketManager = new SocketManager(((LocateMeApplication) getApplication()).getSocket(), this);

        if (getUsername() == null){ // Username not saved
            locationSwitch.setEnabled(false);
        } else {
            nameInput.setText(getUsername());
            locationSwitch.setEnabled(true);
        }

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    locationSwitch.setEnabled(false);
                } else {
                    locationSwitch.setEnabled(true);
                    saveUsername(s.toString());
                }
            }
        });

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT);
                locationSwitch.setChecked(false);
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopLocationUpdates();
    }

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            socketManager.sendLocation(getUsername(), location.getLongitude(), location.getLatitude());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    private void startLocationUpdates(){
        // Check if user has granted location permission and start location updates
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
            locationSwitch.setChecked(false);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constants.MINIMUM_TIME_BEFORE_LOCATION_UPDATE, Constants.MINIMUM_DISTANCE_BEFORE_LOCATION_UPDATE, locationListener);
            socketManager.connect();
        }

    }

    private void stopLocationUpdates(){
        if (hasLocationPermission()) {
            locationManager.removeUpdates(locationListener);
        }
        socketManager.disconnect();
    }

    private boolean hasLocationPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void saveUsername(String username){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SAVED_USER_NAME, username);
        editor.commit();
    }

    private String getUsername(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(SAVED_USER_NAME, null);
    }
}
