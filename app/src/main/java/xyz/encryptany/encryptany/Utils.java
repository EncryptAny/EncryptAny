package xyz.encryptany.encryptany;

/**
 * Created by Cory on 1/30/2017.
 */

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class Utils {
    public static String LogTag = "xyz.encryptany";
    public static String EXTRA_MSG = "extra_msg";


    public static boolean canDrawOverlays(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }else{
            return Settings.canDrawOverlays(context);
        }
    }
}

