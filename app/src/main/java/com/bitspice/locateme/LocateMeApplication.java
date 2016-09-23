package com.bitspice.locateme;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Kahtaf on 9/19/2016.
 */
public class LocateMeApplication extends Application {

    private Socket mSocket;
    {
        try {
            IO.Options options = new IO.Options();
            options.timeout = 20000;
            options.forceNew = true;
            options.port = 3000;
            mSocket = IO.socket(Constants.SERVER_URL, options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
