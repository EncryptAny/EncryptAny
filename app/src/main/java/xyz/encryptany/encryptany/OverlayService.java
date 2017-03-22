package xyz.encryptany.encryptany;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import xyz.encryptany.encryptany.concrete.EncryptedMessage;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private RelativeLayout overlayView, removeView;
    private LinearLayout txtView, txt_linearlayout;
    private TextView  txt1;
    private ImageView removeImg;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = true;
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

        Log.d(Utils.LogTag, "Inflate removeView");
        removeView = (RelativeLayout)inflater.inflate(R.layout.remove, null);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                //PixelFormat.TRANSLUCENT);
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.RGB_888);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        Log.d(Utils.LogTag, "removeView.setVisibility(View.GONE)");
        removeView.setVisibility(View.GONE);
        removeImg = (ImageView)removeView.findViewById(R.id.remove_img);
        Log.d(Utils.LogTag, "addView removeView");
        windowManager.addView(removeView, paramRemove);


        //chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
        overlayView = (RelativeLayout) inflater.inflate(R.layout.overlay, null);

        recyclerView = (RecyclerView) inflater.inflate(R.layout.overlay_recycler_view, null);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new OverlayRecyclerViewAdapter(GenDummyData());
        recyclerView.setAdapter(mAdapter);

        overlayView.addView(recyclerView);
        //chatheadImg = (ImageView)chatheadView.findViewById(R.id.chathead_img);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                300,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        Log.d(Utils.LogTag, "Add chatheadView");
        windowManager.addView(overlayView, params);

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
                    removeView.setVisibility(View.VISIBLE);
                    chathead_longclick();
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

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        if(txtView != null){
                            txtView.setVisibility(View.GONE);
                            myHandler.removeCallbacks(myRunnable);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if(isLongclick){
                            Log.d(Utils.LogTag, "isLongclick");
                            int x_bound_left = szWindow.x / 2 - (int)(remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 +  (int)(remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int)(remove_img_height * 1.5);

                            if((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top){
                                inBounded = true;

                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight() ));

                                if(removeImg.getLayoutParams().height == remove_img_height){
                                    removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeView, param_remove);
                                }

                                layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - overlayView.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - overlayView.getHeight())) / 2 ;

                                windowManager.updateViewLayout(overlayView, layoutParams);
                                break;
                            }else{
                                inBounded = false;
                                removeImg.getLayoutParams().height = remove_img_height;
                                removeImg.getLayoutParams().width = remove_img_width;

                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;

                                windowManager.updateViewLayout(removeView, param_remove);
                            }

                        }


                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(overlayView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(Utils.LogTag, "ACTION_UP");
                        isLongclick = false;
                        Log.d(Utils.LogTag, "removeView.setVisibility(View.GONE)");
                        removeView.setVisibility(View.GONE);
                        removeImg.getLayoutParams().height = remove_img_height;
                        removeImg.getLayoutParams().width = remove_img_width;
                        Log.d(Utils.LogTag, "Remove call backs for runnable_longClick");
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if(inBounded){
                            if(Overlay.active){
                                Overlay.overlayActivity.finish();
                            }
                            Log.d(Utils.LogTag, "Stop ChatHeadService");
                            stopService(new Intent(OverlayService.this, OverlayService.class));
                            inBounded = false;
                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if(Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5){
                            time_end = System.currentTimeMillis();
                            if((time_end - time_start) < 300){
                                chathead_click();
                            }
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int BarHeight =  getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (overlayView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (overlayView.getHeight() + BarHeight );
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        resetPosition(x_cord);

                        break;
                    default:
                        Log.d(Utils.LogTag, "chatheadView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }
                return true;
            }
        });


        txtView = (LinearLayout)inflater.inflate(R.layout.txt, null);
        txt1 = (TextView) txtView.findViewById(R.id.txt1);
        txt_linearlayout = (LinearLayout)txtView.findViewById(R.id.txt_linearlayout);


        WindowManager.LayoutParams paramsTxt = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramsTxt.gravity = Gravity.TOP | Gravity.LEFT;

        Log.d(Utils.LogTag, "txtView.setVisibility(View.GONE)");
        txtView.setVisibility(View.GONE);
        windowManager.addView(txtView, paramsTxt);
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

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) overlayView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(Utils.LogTag, "ChatHeadService.onConfigurationChanged -> landscap");

            if(txtView != null){
                txtView.setVisibility(View.GONE);
            }

            if(layoutParams.y + (overlayView.getHeight() + getStatusBarHeight()) > szWindow.y){
                layoutParams.y = szWindow.y- (overlayView.getHeight() + getStatusBarHeight());
                windowManager.updateViewLayout(overlayView, layoutParams);
            }

            if(layoutParams.x != 0 && layoutParams.x < szWindow.x){
                resetPosition(szWindow.x);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d(Utils.LogTag, "ChatHeadService.onConfigurationChanged -> portrait");

            if(txtView != null){
                txtView.setVisibility(View.GONE);
            }

            if(layoutParams.x > szWindow.x){
                resetPosition(szWindow.x);
            }

        }

    }

    private void resetPosition(int x_cord_now) {
        Log.d(Utils.LogTag, "resetPosition()");
        if(x_cord_now <= szWindow.x / 2){
            isLeft = true;
            moveToLeft(x_cord_now);

        } else {
            isLeft = false;
            moveToRight(x_cord_now);

        }

    }
    private void moveToLeft(final int x_cord_now){
        final int x = szWindow.x - x_cord_now;
        Log.d(Utils.LogTag, "moveToLeft()");
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            public void onTick(long t) {
                long step = (500 - t)/5;
                mParams.x = 0 - (int)(double)bounceValue(step, x );
                windowManager.updateViewLayout(overlayView, mParams);
            }
            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(overlayView, mParams);
            }
        }.start();
    }
    private  void moveToRight(final int x_cord_now){
        Log.d(Utils.LogTag, "moveToRight()");
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            public void onTick(long t) {
                long step = (500 - t)/5;
                mParams.x = szWindow.x + (int)(double)bounceValue(step, x_cord_now) - overlayView.getWidth();
                windowManager.updateViewLayout(overlayView, mParams);
            }
            public void onFinish() {
                mParams.x = szWindow.x - overlayView.getWidth();
                windowManager.updateViewLayout(overlayView, mParams);
            }
        }.start();
    }

    private double bounceValue(long step, long scale){
        double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
        return value;
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    private void chathead_click(){
        Log.d(Utils.LogTag, "chathead_click()");
        //if(overlay.active){
            //Log.d(Utils.LogTag, "myDialog.finish()");
            //overlay.overlayActivity.finish();
        //}else{
            Log.d(Utils.LogTag, "Open myDialog");
            //Intent it = new Intent(this,overlay.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(it);
            //startService(new Intent(this, OverlayService.class));
        //}
    }

    private void chathead_longclick(){
        Log.d(Utils.LogTag, "Into ChatHeadService.chathead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }

    Handler myHandler = new Handler();
    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(txtView != null){
                txtView.setVisibility(View.GONE);
            }
        }
    };

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

        if(txtView != null){
            windowManager.removeView(txtView);
        }

        if(removeView != null){
            windowManager.removeView(removeView);
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(Utils.LogTag, "ChatHeadService.onBind()");
        return null;
    }


}
