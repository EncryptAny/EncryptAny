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

import xyz.encryptany.encryptany.listeners.MessagesUpdatedListener;
import xyz.encryptany.encryptany.interfaces.AppAdapter;
import xyz.encryptany.encryptany.interfaces.Message;

public class AccessibilityAppAdapter extends AccessibilityService implements AppAdapter {

    private static final boolean annoyCory = false;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("MAXWELL", event.toString());

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        if (annoyCory && source != null & event.getClassName().equals("android.widget.EditText")) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo
                    .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "android");
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
    public boolean sendMessage(String message) {
        return false;
    }

    @Override
    public void setMessageUpdatedListener(MessagesUpdatedListener listener) {

    }

    @Override
    public boolean inputMessage(Message message) {
        return false;
    }
}