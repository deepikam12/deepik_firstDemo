package com.tobaccocessation.Utils;

/**
 * Created by Ayush.Dimri on 10/5/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {

        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivity.getActiveNetworkInfo() != null;

    }
}
