package com.bestspa.spa.client.Utiles;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetCheck {

    private Context context;

    public InternetCheck(Context context) {
        this.context = context;
    }

    public boolean isNetworkAvalable() {
        ConnectivityManager connec = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connec.getNetworkInfo(1);
        NetworkInfo mobile = connec.getNetworkInfo(0);
        if (wifi.isConnected() || mobile.isConnected()) {
            return true;
        }
        return false;
    }

}
