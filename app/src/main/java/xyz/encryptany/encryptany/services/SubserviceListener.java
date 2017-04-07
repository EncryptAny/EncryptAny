package xyz.encryptany.encryptany.services;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Created by max on 4/6/17.
 */

public interface SubserviceListener {
    void onConfigurationChanged(Configuration config);

    Context getApplicationContext();
    int onStartCommand(Intent intent, int flags, int startId);
}
