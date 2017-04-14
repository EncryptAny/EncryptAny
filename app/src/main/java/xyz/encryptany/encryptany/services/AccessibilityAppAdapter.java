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

import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.LinkedList;
import java.util.List;

import xyz.encryptany.encryptany.Mediator;
import xyz.encryptany.encryptany.concrete.OTREncryptor;
import xyz.encryptany.encryptany.listeners.AppListener;
import xyz.encryptany.encryptany.interfaces.AppAdapter;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.testing.MapArchiver;
import xyz.encryptany.encryptany.testing.NoOpArchiver;
import xyz.encryptany.encryptany.testing.NoOpEncryptor;

public class AccessibilityAppAdapter extends AccessibilityService implements AppAdapter, SubserviceListener {

    // Mediator-Related Stuff
    private UIService uiService = new UIService(this);

    // App Adapter Helper Objects
    private static Gson gson = new Gson();

    // App Adapter helper variables
    private static boolean DEBUG = true;
    private static final String TAG = "AccessibilityAppAdapter";
    private static final int DATE_RADIX = 10;


    private AppListener appListener = null;

    // Accessibility App Adapter Variables
    private CurrentWindow currentWindow = new CurrentWindow();
    private FillState fillState = null;

    private static class FillState {
        private AppListener appListener;
        private FillStateStatus prevState = FillStateStatus.NOT_FILLABLE;
        private FillStateStatus state = FillStateStatus.NOT_FILLABLE;
        private String textToFill = null; // stores text to fill after message has been sent.

        public FillState(AppListener appListener) {
            this.appListener = appListener;
        }
        private enum FillStateStatus {
            NOT_FILLABLE,
            READY,
            TEXT_RECEIVED,
            FILLED
        }

        // returns true if the new state was actually new. (and thus action should be taken)
        private boolean advanceState(FillStateStatus acceptablePrevState, FillStateStatus newState, boolean strictTransition) {
            // check the prev state for potential errors
            boolean isValidPrevState = (acceptablePrevState == null) || (state == acceptablePrevState);
            boolean isValidSameState = state == newState;
            if (!isValidPrevState && !isValidSameState) {
                if (strictTransition) {
                    // On a strict transition, we throw if there is an issue
                    throw new IllegalStateException("Erroneous state change: " + state.toString() + " to " + newState.toString());
                } else {
                    // on a non-strict transition, we simply do not perform the given transition.
                    if (DEBUG) {
                        Log.d(TAG, "State not changed from " + state.toString() + " to " + newState.toString() + " due to invalid state change");
                    }
                    return false;
                }
            }
            if (DEBUG) {
                Log.d(TAG, "State changed from " + state.toString() + " to " + newState.toString());
            }

            // Advance the state
            prevState = state;
            state = newState;
            // returns true if this was a novel state change
            return state != prevState;
        }

        public void reset() {
            // prev state is null since we can advance from any state
            if (advanceState(null, FillStateStatus.NOT_FILLABLE, true)) {
                appListener.resetStatus();
            }
        }

        public void readyToSend() {

            if (advanceState(FillStateStatus.NOT_FILLABLE, FillStateStatus.READY, false)) {
                appListener.readyForMessage();
            }
        }

        public void textReceived(String textToFill) {
            advanceState(FillStateStatus.READY, FillStateStatus.TEXT_RECEIVED, true);
            this.textToFill = textToFill;
        }

        public String getTextToFill() {
            if (state == FillStateStatus.TEXT_RECEIVED) {
                return textToFill;
            }
            return null;
        }

        public void appFilled() {
            if(advanceState(FillStateStatus.TEXT_RECEIVED, FillStateStatus.FILLED, true)) {
                appListener.waitingForSend();
            }
        }

        // returns the text that should be verified if it has been sent.
        public String getTextToVerify() {
            if (state == FillStateStatus.FILLED) {
                return textToFill;
            }
            return null;
        }

        public void userSentMessage() {
            if (advanceState(FillStateStatus.FILLED, FillStateStatus.READY, true)) {
                appListener.messageSent();
            }
        }


    }

    private static class CurrentWindow {
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

    private static class AdapterMessage {
        private transient String originalJSON = null;
        // these variables kept short to make the JSON reasonably sized.
        private String msg; //Full Message Text
        private String app; //Originating App
        private String dte; //Unix Timestamp
        private String op;  //"Other Participant"

        private AdapterMessage() {}

        public static AdapterMessage fromUIMessage(Message msg, String appPkg, String uniqueID) {
            AdapterMessage adapterMessage = new AdapterMessage();
            // Good data
            adapterMessage.msg = msg.getMessage();
            adapterMessage.dte = Long.toString(msg.getDate(), DATE_RADIX);
            // Garbage Data to be Replaced
            adapterMessage.app = appPkg;
            adapterMessage.op = uniqueID;
            // TODO Determine what to do about garbage data
            return adapterMessage;
        }
        public static AdapterMessage fromJson(String rawText) {
            AdapterMessage je;
            try {
                je = gson.fromJson(rawText, AdapterMessage.class);
            } catch (JsonSyntaxException e) {
                je = null;
            }
            if (je != null) {
                je.originalJSON = rawText;
            }
            return je;
        }
        public String toJson() {
            return gson.toJson(this);
        }

        public String getMessage() {
            return msg;
        }

        public String getApp() {
            return app;
        }

        public long getDate() {
            return Long.parseLong(dte, DATE_RADIX);
        }

        public String getAuthorID() {
            return op;
        }

        public String getOriginalJSON() {
            return originalJSON;
        }
    }

    private String myAuthorID() {
        String token = InstanceID.getInstance(getServiceContext()).getId();
        if (DEBUG) {
            Log.d(TAG, "InstanceID: " + token);
        }
        return token;
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
        // TODO change away from testing encryptor
        Mediator m = new Mediator(this, uiService, new NoOpEncryptor(), new MapArchiver());
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
        // see if the mediator has hooked us up with a reference!
        if (appListener == null) {
            if (DEBUG) {
                Log.d(TAG, "no reference to appListener yet!");
            }
            return;
        } else if (fillState == null) {
            // init the fill state if this our first call with a valid appListener
            if (DEBUG) {
                Log.d(TAG, "Instantiating FillState!");
            }
            fillState = new FillState(appListener);
        }

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        CurrentWindow oldCurrentWindow = currentWindow;
        /* Update Current Package */
        currentWindow = refreshCurrentWindow(event, getPackageManager(), currentWindow);
        // if the current window object has changed, reset the status of the encryption system.
        if (currentWindow != oldCurrentWindow) {
            fillState.reset();
        }
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
        boolean isEditText = event.getClassName().equals("android.widget.EditText");
        if(isEditText) {
            if (DEBUG) {
                Log.d(TAG, "Found EditText!");
            }
            currentWindow.setCurrTextView(event, source);
            // set ourselves doneWaiting to send since we found an edit text!
            fillState.readyToSend();
        }

        attemptMessageFill();

        // if we are an edit text, after filling we return to make sure that no further processing
        // is done on it.
        if (isEditText) {
            return;
        }


        // now the fun stuff! We look for any messages in the given source
        List<AdapterMessage> foundMessages = lookForMessages(source);
        // we also obtain the message that we are going to verify is sent
        String msgToVerify = fillState.getTextToVerify();
        for (AdapterMessage foundMessage : foundMessages) {
            // if the message is from ourselves, then we do NOT send it on through the system.
            if (foundMessage.getAuthorID().equals(myAuthorID())) {
                if (msgToVerify != null && msgToVerify.equals(foundMessage.getOriginalJSON())) {
                    fillState.userSentMessage();
                }
                if (DEBUG) {
                    Log.d(TAG, "skipping msg since it matches our tag: " + foundMessage.getMessage());
                }
                continue;
            }
            if (DEBUG) {
                Log.d(TAG, "Firing message to appListener: " + foundMessage.getMessage());
            }
            // otherwise, we do!
            String msg = foundMessage.getMessage();
            String otherParticipant = foundMessage.getAuthorID();
            String pkg = currentWindow.getCurrWindowPkg();
            long date = foundMessage.getDate();
            appListener.setMessageReceived(msg, otherParticipant, pkg, date);
        }
    }

    /**
     * Refreshes current window depending on whether the window state has changed! :)
     * @param event
     * @param currentWindow
     * @return either a new or old currentWindow depending on wether things have changed.
     */
    private static CurrentWindow refreshCurrentWindow(AccessibilityEvent event, PackageManager packageManager, CurrentWindow currentWindow) {
        // Check if this is even an event that we care about at all.
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return currentWindow;
        }
        // even if we do care about it, we need to make sure it has the information we need.
        if (event.getPackageName() == null || event.getClassName() == null) {
            return currentWindow;
        }

        // OK! Now we know we're gucci! Let's get more information about the current window and what changed
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

        return currentWindow;
    }

    private void attemptMessageFill() {
        String textToFill = fillState.getTextToFill();
        if (textToFill != null) {
            boolean fillSuccessful = autofillMessage(currentWindow, textToFill);
            if (fillSuccessful) {
                fillState.appFilled();
            }
        }
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


    // attempts to look for messages given a source node.
    private List<AdapterMessage> lookForMessages(AccessibilityNodeInfo source) {
        List<AdapterMessage> foundMsgs = new LinkedList<>();
        lookForMessagesHelper(source, foundMsgs, "", 0);
        if (DEBUG) {
            for (AdapterMessage foundMsg : foundMsgs) {
                Log.d(TAG, "Found Encoded Message: " + foundMsg.getMessage());
            }
        }
        return foundMsgs;
    }

    // recursive call to look for and add messages
    private void lookForMessagesHelper(AccessibilityNodeInfo ani, List<AdapterMessage> foundMessages, String depth, int depthInt) {
        if (ani == null) {
            return;
        }
        // if we are an edit text, we ignore it
        boolean isEditText = ani.getClassName().equals("android.widget.EditText");
        if (isEditText) {
            return;
        }
        if (ani.getText() != null) {
            String txt = ani.getText().toString();
            AdapterMessage msg = AdapterMessage.fromJson(txt);
            // TODO add date to msg so it doesn't pick up the same one
            if (msg != null) {
                foundMessages.add(msg);
            }
        }
        for (int i=0; i < ani.getChildCount(); ++i) {
            ++depthInt;
            lookForMessagesHelper(ani.getChild(i), foundMessages, depth + " " + depthInt, depthInt);
        }
    }

    @Override
    public void sendMessage(Message message) {
        AdapterMessage adapterMessage = AdapterMessage.fromUIMessage(
                message,
                currentWindow.getCurrWindowPkg(),
                myAuthorID()
        );
        fillState.textReceived(adapterMessage.toJson());
        if (DEBUG) {
            Log.d(TAG, "Ready to fill!");
        }
        // we don't attempt a message fill right away due to issues with
        // the ui interfering with our fill.
    }

    private static boolean isEmptyOrNull(String str) {
        return str == null || str.isEmpty();
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