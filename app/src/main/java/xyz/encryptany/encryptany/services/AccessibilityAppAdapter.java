package xyz.encryptany.encryptany.services;

/**
 * Created by max on 1/26/17.
 */

// Guide Here: https://developer.android.com/guide/topics/ui/accessibility/services.html

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import xyz.encryptany.encryptany.concrete.JSONMessageCodecStrategy;
import xyz.encryptany.encryptany.listeners.AppListener;
import xyz.encryptany.encryptany.interfaces.AppAdapter;
import xyz.encryptany.encryptany.interfaces.Message;

public class AccessibilityAppAdapter extends AccessibilityService implements AppAdapter {

    private static final boolean ENABLE_AUTOFILL = true;
    private AppListener appListener = null;

    // App State
    private String currAppPkg = "";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("MAXWELL", event.toString());
        Log.d("MAXWELL", event.getClassName().toString());

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }
        String eventPkg = source.getPackageName().toString();
        if (this.currAppPkg.equals(eventPkg)) {

        }
        Log.d("MAXWELL", event.getClassName().toString());

        // Attempt to find an EditText (so we can try to auto-fill it!)
        // TODO determine if indiscriminate filling is bad.
        if (ENABLE_AUTOFILL && source != null & event.getClassName().equals("android.widget.EditText")) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(
                    AccessibilityNodeInfo
                    .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    JSONMessageCodecStrategy.EXAMPLE_JSON   // TODO Undo hardcoded text
            );
            source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }


        final int eventType = event.getEventType();
        String eventText = null;
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "Focused: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "Focused: ";
                break;
        }

        if (event.getContentDescription() != null) {
            Log.d("MAXWELL", event.getContentDescription().toString());
        }

        accessDFS(source, "", 0);
    }

    static private void accessDFS(AccessibilityNodeInfo ani, String depth, int depthInt) {
        if (ani == null) {
            return;
        }
        if (ani.getText() != null) {
            Log.d("MAXWELL", depth + ": " + ani.getText().toString());
        }
        for (int i=0; i < ani.getChildCount(); ++i) {
            ++depthInt;
            accessDFS(ani.getChild(i), depth + " " + depthInt, depthInt);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean sendMessage(Message message) {

        return false;
    }

    @Override
    public void setMessageUpdatedListener(AppListener listener) {
        this.appListener = listener;
    }

}