package xyz.encryptany.encryptany.services;

/**
 * Created by max on 1/26/17.
 */

// Guide Here: https://developer.android.com/guide/topics/ui/accessibility/services.html

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import xyz.encryptany.encryptany.Mediator;
import xyz.encryptany.encryptany.concrete.JSONMessageCodecStrategy;
import xyz.encryptany.encryptany.interfaces.MessageCodecStrategy;
import xyz.encryptany.encryptany.listeners.AppListener;
import xyz.encryptany.encryptany.interfaces.AppAdapter;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.testing.NoOpArchiver;
import xyz.encryptany.encryptany.testing.NoOpEncryptor;

public class AccessibilityAppAdapter extends AccessibilityService implements AppAdapter, SubserviceListener {

    // Mediator-Related Stuff
    private UIService uiService = new UIService(this);

    // App Adapter Helpers
    private static boolean DEBUG = true;
    private static final String TAG = "AccessibilityAppAdapter";
    private AppListener appListener = null;
    private MessageCodecStrategy msgCodec = new JSONMessageCodecStrategy();

    // Accessibility App Adapter Variables
    private String textToFill = null; // stores text to fill after message has been sent.
    private String openAppPkg = "";
    private CurrentWindow currentWindow = new CurrentWindow();


    static private class CurrentWindow {
        private AccessibilityEvent currTextViewEvent = null;
        private AccessibilityNodeInfo currTextViewNodeInfo = null;
        private final String currWindowActivity;
        private final String currWindowPkg;

        // just to make sure CW can never be null. Don't use this except for initalization!
        private CurrentWindow() {
            this("", "");
        }
        private CurrentWindow(String currWindowActivity, String currWindowPkg) {
            this.currWindowActivity = currWindowActivity;
            this.currWindowPkg = currWindowPkg;
        }

        /* General State Funcs */
        public String getCurrWindowActivity() {
            return currWindowActivity;
        }
        public String getCurrWindowPkg() {
            return currWindowPkg;
        }
        public boolean isCurrentPackage(String pkg) {
            return currWindowPkg.equals(pkg);
        }
        public boolean isCurrentActivity(String activity) {
            return currWindowActivity.equals(activity);
        }

        /* Text View Funcs */

        public AccessibilityEvent getCurrTextViewEvent() {
            return currTextViewEvent;
        }
        public AccessibilityNodeInfo getCurrTextViewNodeInfo() {
            return currTextViewNodeInfo;
        }
        public void setCurrTextView(AccessibilityEvent currTextViewEvent, AccessibilityNodeInfo currTextViewNodeInfo) {
            this.currTextViewEvent = currTextViewEvent;
            this.currTextViewNodeInfo = currTextViewNodeInfo;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        uiService.onCreate();
    }

    @Override
    public void onDestroy() {
        uiService.onDestroy();
        super.onDestroy();
    }

    // This is where we want to put all of our initalization code
    @Override
    protected void onServiceConnected() {
        Mediator m = new Mediator(this, uiService, new NoOpEncryptor(), new NoOpArchiver());
        super.onServiceConnected();
        uiService.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        uiService.onConfigurationChanged(newConfig);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        /* Update Current Package */
        currentWindow = refreshCurrentWindow(event, getPackageManager(), currentWindow);
        // if the currentWindow is null, we really can't do anything without causing exceptions...
        if(currentWindow == null) {
            return;
        }

        /* Update Context Variables */
        // Check if this event is relevant to the current window (filters out notifications, systemui, etc.)
        // If not, let's just ignore it because it isn't relevant
        // (and will clog up our attempts to grab other elements)
        if (!currentWindow.isCurrentPackage(event.getPackageName().toString())) {
            return;
        }
        // check for any EditTexts...
        if(event.getClassName().equals("android.widget.EditText")) {
            if (DEBUG) {
                Log.d(TAG, "Found EditText!");
            }
            currentWindow.setCurrTextView(event, source);
            sendMessageIfApplicable();
        }




//        final int eventType = event.getEventType();
//        String eventText = null;
//        switch(eventType) {
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                eventText = "Focused: ";
//                break;
//            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
//                eventText = "Focused: ";
//                break;
//        }

//        if (event.getContentDescription() != null) {
//            //Log.d("MAXWELL", event.getContentDescription().toString());
//        }

        //accessDFS(source, "", 0);
    }

    /**
     * Refreshes current window depending on whether the window state has changed! :)
     * @param event
     * @param currentWindow
     * @return either a new or old currentWindow depending on wether things have changed.
     */
    private static CurrentWindow refreshCurrentWindow(AccessibilityEvent event, PackageManager packageManager, CurrentWindow currentWindow) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );

                ActivityInfo activityInfo;
                try {
                    activityInfo = packageManager.getActivityInfo(componentName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    activityInfo = null;
                }

                boolean isActivity = activityInfo != null;
                if (isActivity) {
                    String activityName = componentName.flattenToString();
                    if (!currentWindow.isCurrentActivity(activityName)) {
                        if (DEBUG) {
                            Log.d(TAG, "Activity Switch Detected. New Activity Package: " + activityName);
                        }
                        currentWindow = new CurrentWindow(activityName, componentName.getPackageName());
                    }
                }
            }
        }
        return currentWindow;
    }

    static private boolean autofillMessage(CurrentWindow cw, String rawMessageTxt) {
        if (rawMessageTxt == null || rawMessageTxt.isEmpty()) {
            throw new IllegalArgumentException("method called with empty string!");
        }
        AccessibilityEvent accTextView = cw.getCurrTextViewEvent();
        if (accTextView == null) {
            // TextView was not found. returning false.
            return false;
        }
        Bundle arguments = new Bundle();
        arguments.putCharSequence(
                AccessibilityNodeInfo
                        .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                rawMessageTxt
        );
        cw.getCurrTextViewNodeInfo().performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        // TODO attempt to press send automatically!
        return true;
    }

    // built in, must-be-overrriden accessibility service method.
    @Override
    public void onInterrupt() {}

    static private void accessDFS(AccessibilityNodeInfo ani, String depth, int depthInt) {
        if (ani == null) {
            return;
        }
        if (ani.getText() != null) {
            //Log.d("MAXWELL", depth + ": " + ani.getText().toString());
        }
        for (int i=0; i < ani.getChildCount(); ++i) {
            ++depthInt;
            accessDFS(ani.getChild(i), depth + " " + depthInt, depthInt);
        }
    }

    @Override
    public boolean sendMessage(Message message) {
        textToFill = msgCodec.encodeText(message);
        if (DEBUG) {
            Log.d(TAG, "setting textToFill. Ready to fill!");
        }
        //sendMessageIfApplicable();
        // TODO determine if this is useless
        return false;
    }

    private static boolean isEmptyOrNull(String str) {
        return str == null || str.isEmpty();
    }

    private void sendMessageIfApplicable() {
        if (isEmptyOrNull(textToFill)) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "Valid Message to Fill. Attempting fill!");
        }
        boolean fillSuccessful = autofillMessage(currentWindow, textToFill);
        if (fillSuccessful) {
            textToFill = null;
        }
    }

    @Override
    public void setMessageUpdatedListener(AppListener listener) {
        this.appListener = listener;
    }

    @Override
    public Context getServiceContext() {
        return this;
    }
}