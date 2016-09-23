package com.bitspice.locateme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Kahtaf on 9/19/2016.
 */
public class StartActivity extends AppCompatActivity {
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;

    private EditText nameInput;
    private EditText serverInput;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameInput = (EditText) findViewById(R.id.name);
        serverInput = (EditText) findViewById(R.id.server_url);
        startButton = (Button) findViewById(R.id.join_button);

        if (Utils.getServerURL(this) == null){ // Server URL not saved
            startButton.setEnabled(false);
        } else {
            serverInput.setText(Utils.getServerURL(this));
            startButton.setEnabled(true);
        }

        if (Utils.getUsername(this) == null){ // Username not saved
            startButton.setEnabled(false);
        } else {
            nameInput.setText(Utils.getUsername(this));
            startButton.setEnabled(true);
        }

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    startButton.setEnabled(false);
                } else {
                    startButton.setEnabled(true);
                    Utils.saveUsername(StartActivity.this, s.toString());
                }
            }
        });

        serverInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    startButton.setEnabled(false);
                } else {
                    startButton.setEnabled(true);
                    Utils.saveServerURL(StartActivity.this, s.toString());
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startButton.isEnabled()){
                    Intent intent = new Intent(StartActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startButton.setEnabled(true);
            } else {
                Toast.makeText(this, getString(R.string.location_denied), Toast.LENGTH_SHORT);
                startButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
