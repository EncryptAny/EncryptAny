package xyz.encryptany.encryptany.services;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

/**
 * Created by max on 4/6/17.
 */

public interface SubserviceListener {

    // Real service stuff
    Context getApplicationContext();
    Object getSystemService(String service);

    // Fake service stuff
    Context getServiceContext();
}
