package xyz.encryptany.encryptany.services;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Created by max on 4/6/17.
 */

public class Subservice implements SubserviceListener {

    private SubserviceListener subListener;

    /* These will be streamlined in with our "real" service */

    public Subservice(SubserviceListener subListener) {
        this.subListener = subListener;
    }

    public void onCreate() {}

    public void onDestroy() {}

    /* All bottom below are forwarded to the listener */

    @Override
    public void onConfigurationChanged(Configuration config) {
        subListener.onConfigurationChanged(config);
    }

    @Override
    public Context getApplicationContext() {
        return subListener.getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return subListener.onStartCommand(intent, flags, startId);
    }


}
