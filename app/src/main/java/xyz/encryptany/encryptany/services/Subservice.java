package xyz.encryptany.encryptany.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Created by max on 4/6/17.
 */

public abstract class Subservice implements SubserviceListener {

    private SubserviceListener subListener;
    protected final String WINDOW_SERVICE;
    protected final String LAYOUT_INFLATER_SERVICE;


    public Subservice(SubserviceListener subListener) {
        this.WINDOW_SERVICE = Service.WINDOW_SERVICE;
        this.LAYOUT_INFLATER_SERVICE = Service.LAYOUT_INFLATER_SERVICE;
        this.subListener = subListener;
    }

    // represents how a subservice starts itself, since we cannot guarantee some calls for all
    //  service types (e.g. onStartCommand does not apply to AccessibilityServices
    abstract void start();

    /* Helper Methods */

     /* These will be streamlined in with our "real" service */

    public void onCreate() {}

    public void onDestroy() {}

    public void onConfigurationChanged(Configuration config) {}

    int onStartCommand(Intent intent, int flags, int startId) {
        // Hopefully this doesn't break things....
        return 0;
    }

    /* All bottom below are forwarded to the listener */

    @Override
    public Context getApplicationContext() {
        return subListener.getApplicationContext();
    }

    @Override
    public Object getSystemService(String service) {
        return subListener.getSystemService(service);
    }

    @Override
    public Context getServiceContext() {
        return subListener.getServiceContext();
    }


}
