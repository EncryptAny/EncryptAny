package xyz.encryptany.encryptany;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import xyz.encryptany.encryptany.concrete.EncryptedMessage;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private RelativeLayout overlayView, editTextView;
    private EditText overlayEditText;
    private Button overlayEditTextSend, overlayShowEditText;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private String sMsg = "";
    private RecyclerView recyclerView;
    private OverlayRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @SuppressWarnings("deprecation")

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.d(Utils.LogTag, "ChatHeadService.onCreate()");

    }

    private EncryptedMessage[] GenDummyData()
    {
        final int dummyAmount = 20;
        EncryptedMessage[] dummydata = new EncryptedMessage[dummyAmount];
        for (int i = 1; i != dummyAmount+1; ++i)
        {
            dummydata[i-1] = new EncryptedMessage("Dummy encrypted text " + i,"Dummy source " + i,"Dummy app " + i, new Date());
        }
        return dummydata;
    }

    private void handleStart(){
        Log.d(Utils.LogTag,"handleStart()");

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);


        overlayView = (RelativeLayout) inflater.inflate(R.layout.overlay, null);
        recyclerView = (RecyclerView) inflater.inflate(R.layout.overlay_recycler_view, null);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new OverlayRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);

        overlayView.addView(recyclerView);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }
        //TODO: Modify WindowManager size
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                600,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        //params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 0;
        params.y = 100;
        Log.d(Utils.LogTag, "Add chatheadView");
        windowManager.addView(overlayView, params);

        overlayShowEditText = (Button)overlayView.findViewById(R.id.overlayEditTextShow);
        overlayShowEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editTextView.setVisibility(View.VISIBLE);
                    }
                }
        );

        // WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        // Combined with
        // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        // Makes the edit text always receive text
        editTextView = (RelativeLayout)inflater.inflate(R.layout.overlay_edit_text, null);
        overlayEditText = (EditText) editTextView.findViewById(R.id.editText);
        editTextView.setVisibility(View.GONE);
        overlayEditTextSend = (Button)editTextView.findViewById(R.id.overlayTextSend);
        overlayEditTextSend.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Grab text first
                        editTextView.clearFocus();
                        overlayEditText.setText("");
                        // Have to do this, no other way
                        editTextView.setVisibility(View.GONE);
                    }
                }
        );

        WindowManager.LayoutParams params2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                200,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSPARENT);
        params2.gravity = Gravity.BOTTOM;
        params2.dimAmount = (float)0.2;
        windowManager.addView(editTextView, params2);

        editTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_OUTSIDE:
                        Log.d(Utils.LogTag, "!!!Clearing focus!!!");
                        editTextView.clearFocus();
                        // Have to do this, no other way
                        editTextView.setVisibility(View.GONE);

                }
                return true;
            }
        });


        overlayView.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.d(Utils.LogTag, "Into runnable_longClick");

                    isLongclick = true;
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) overlayView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(Utils.LogTag, "ACTION_DOWN");
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 600);


                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(overlayView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        Log.d(Utils.LogTag, "chatheadView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }
                return true;
            }
        });

    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d(Utils.LogTag, "ChatHeadService.onStartCommand() -> startId=" + startId);

        if(intent != null){
            Bundle bd = intent.getExtras();

            if(bd != null)
                sMsg = bd.getString(Utils.EXTRA_MSG);

            if(sMsg != null && sMsg.length() > 0){
                if(startId == Service.START_STICKY){
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                        }
                    }, 300);

                }else{
                }

            }

        }

        if(startId == Service.START_STICKY) {
            handleStart();
            return super.onStartCommand(intent, flags, startId);
        }else{
            return  Service.START_NOT_STICKY;
        }

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if(overlayView != null){
            windowManager.removeView(overlayView);
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(Utils.LogTag, "ChatHeadService.onBind()");
        return null;
    }

    static final int MSG_UPDATE_RECYCLER_VIEW = 1;
    static final int MSG_ENCRYPTION_READY = 2;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_RECYCLER_VIEW:

                    break;
                case MSG_ENCRYPTION_READY:

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());


}
