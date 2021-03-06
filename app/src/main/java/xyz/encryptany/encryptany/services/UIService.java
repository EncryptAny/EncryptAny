package xyz.encryptany.encryptany.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xyz.encryptany.encryptany.OverlayRecyclerViewAdapter;
import xyz.encryptany.encryptany.R;
import xyz.encryptany.encryptany.Utils;
import xyz.encryptany.encryptany.concrete.MessageFactory;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.UIAdapter;
import xyz.encryptany.encryptany.listeners.UIListener;


public class UIService extends Subservice implements UIAdapter {
    private WindowManager windowManager;
    private RelativeLayout chatheadView, removeView, overlayView, editTextView;
    private EditText overlayEditText;
    private Button overlayEditTextSend, overlayShowEditText;
    private LinearLayout txtView, txt_linearlayout;
    private ImageView chatheadImg, removeImg;
    private TextView   txt1;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = true;
    private String sMsg = "";
    private RecyclerView recyclerView;
    private OverlayRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private UIListener uiListener;
    private String activeApp;
    private String authorName;
    private String recipientName;
    private MessageFactory messageFactory;
    private Message newMessage;
    private UIStatus uiStatus;
    private UIWindowState uiWindowState;

    public UIService(SubserviceListener subListener) {
        super(subListener);
    }

    private void handleStart(){
        messageFactory = new MessageFactory();
        authorName = "You";
        recipientName = "Recipient";
        activeApp = "Application";
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        mAdapter = new OverlayRecyclerViewAdapter();

        overlayView = (RelativeLayout) inflater.inflate(R.layout.overlay, null);

        recyclerView = (RecyclerView) inflater.inflate(R.layout.overlay_recycler_view, null);
        recyclerView.setAdapter(mAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getServiceContext());
        recyclerView.setLayoutManager(mLayoutManager);


        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.ABOVE,R.id.overlayEditTextShow);

        overlayView.addView(recyclerView,relativeParams);

        removeView = (RelativeLayout)inflater.inflate(R.layout.remove, null);

        removeView.setVisibility(View.GONE);
        removeImg = (ImageView)removeView.findViewById(R.id.remove_img);


        chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
        chatheadImg = (ImageView)chatheadView.findViewById(R.id.chathead_img);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        chatheadView.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    isLongclick = true;
                    removeView.setVisibility(View.VISIBLE);
                    chathead_longclick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
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

                                layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - chatheadView.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - chatheadView.getHeight())) / 2 ;

                                windowManager.updateViewLayout(chatheadView, layoutParams);
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

                        windowManager.updateViewLayout(chatheadView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        isLongclick = false;
                        removeView.setVisibility(View.GONE);
                        removeImg.getLayoutParams().height = remove_img_height;
                        removeImg.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if(inBounded){
                            if (uiStatus != UIStatus.BUSY && uiStatus != UIStatus.AWAITING_CLICK) {
                                setUIWindowState_Minimized();
                                chatheadView.setVisibility(View.GONE);
                                inBounded = false;
                                uiWindowState = UIWindowState.CLOSED;
                            }
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
                        } else if (y_cord_Destination + (chatheadView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (chatheadView.getHeight() + BarHeight );
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;

                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        txtView = (LinearLayout)inflater.inflate(R.layout.txt, null);
        txt1 = (TextView) txtView.findViewById(R.id.txt1);
        txt_linearlayout = (LinearLayout)txtView.findViewById(R.id.txt_linearlayout);

        txtView.setVisibility(View.GONE);

        WindowManager.LayoutParams params_overlayView = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                600,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params_overlayView.x = 0;
        params_overlayView.y = 100;
        windowManager.addView(overlayView, params_overlayView);
        overlayView.setVisibility(View.GONE);

        overlayShowEditText = (Button)overlayView.findViewById(R.id.overlayEditTextShow);
        overlayShowEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editTextView.setVisibility(View.VISIBLE);
                        uiWindowState = UIWindowState.EDITING_TEXT;
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
                        String userTxt = overlayEditText.getText().toString();
                        newMessage = messageFactory.createNewMessage(userTxt, authorName, activeApp);
                        editTextView.clearFocus();
                        overlayEditText.setText("");
                        // Have to do this, no other way
                        editTextView.setVisibility(View.GONE);
                        setUIWindowState_Minimized();
                        mAdapter.addMessage(newMessage);
                        uiListener.sendMessageFromUIAdapter(userTxt, authorName, activeApp);
                    }
                }
        );

        createEditTextWindow();
        createRemoveViewWindow();
        createChatheadTxtViewWindow();
        createChatheadWindow();

        editTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_OUTSIDE:
                        editTextView.clearFocus();
                        // Have to do this, no other way
                        editTextView.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });


        overlayView.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0;
            boolean isLongclick = false;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
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
                        break;
                }
                return true;
            }
        });

        setUIStatus(UIStatus.INACTIVE);
        setUIWindowState_Minimized();

    }//HandleStart

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if(txtView != null){
                txtView.setVisibility(View.GONE);
            }

            if(layoutParams.y + (chatheadView.getHeight() + getStatusBarHeight()) > szWindow.y){
                layoutParams.y = szWindow.y- (chatheadView.getHeight() + getStatusBarHeight());
                windowManager.updateViewLayout(chatheadView, layoutParams);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if(txtView != null){
                txtView.setVisibility(View.GONE);
            }

        }

    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    private void chathead_click(){
        if (uiWindowState == UIWindowState.SHOWING || uiWindowState == UIWindowState.EDITING_TEXT) {
            setUIWindowState_Minimized();
            return;
        }
        if(uiStatus == UIStatus.READY || uiStatus == UIStatus.ACTIVE) {
            if (uiListener.isConversationReady()) {
                overlayView.setVisibility(View.VISIBLE);
                uiWindowState = UIWindowState.SHOWING;
                if (uiStatus == UIStatus.READY)
                    setUIStatus(UIStatus.ACTIVE);
            }
            else {
                uiListener.startEncryptionProcess(authorName,activeApp);
            }
        }
    }



    private void chathead_longclick(){
        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }

    private void showMsg(String sMsg){
        if(txtView != null && chatheadView != null ){
            txt1.setText(sMsg);
            myHandler.removeCallbacks(myRunnable);

            WindowManager.LayoutParams param_chathead = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
            WindowManager.LayoutParams param_txt = (WindowManager.LayoutParams) txtView.getLayoutParams();

            txt_linearlayout.getLayoutParams().height = chatheadView.getHeight();
            txt_linearlayout.getLayoutParams().width = szWindow.x / 2;

            if(isLeft){
                param_txt.x = param_chathead.x + chatheadImg.getWidth();
                param_txt.y = param_chathead.y;

                txt_linearlayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }else{
                param_txt.x = param_chathead.x - szWindow.x / 2;
                param_txt.y = param_chathead.y;

                txt_linearlayout.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            }
            txtView.setVisibility(View.VISIBLE);
            windowManager.updateViewLayout(txtView, param_txt);

            myHandler.postDelayed(myRunnable,5000);
        }

    }

    Handler myHandler = new Handler();
    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {
            if(txtView != null){
                txt1.setText("");
                txtView.setVisibility(View.GONE);
            }
        }
    };

    private void createChatheadWindow()
    {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        windowManager.addView(chatheadView, params);
    }
    private void createRemoveViewWindow()
    {
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;
        windowManager.addView(removeView, paramRemove);
    }
    private void createEditTextWindow()
    {
        WindowManager.LayoutParams params_editTextView = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                300,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSPARENT);
        params_editTextView.gravity = Gravity.BOTTOM;
        params_editTextView.dimAmount = (float)0.2;
        windowManager.addView(editTextView, params_editTextView);
    }
    private void createChatheadTxtViewWindow()
    {
        WindowManager.LayoutParams paramsTxt = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramsTxt.gravity = Gravity.TOP | Gravity.LEFT;
        windowManager.addView(txtView, paramsTxt);
    }

    @Override
    void start() {
        onStartCommand(null, 0, Service.START_STICKY);
    }

    //@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            Bundle bd = intent.getExtras();

            if(bd != null)
                sMsg = bd.getString(Utils.EXTRA_MSG);

            if(sMsg != null && sMsg.length() > 0){
                if(startId == Service.START_STICKY){
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            showMsg(sMsg);
                        }
                    }, 300);

                }else{
                    showMsg(sMsg);
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
        super.onDestroy();

        if(chatheadView != null){
            windowManager.removeView(chatheadView);
        }

        if(overlayView != null){
            windowManager.removeView(overlayView);
        }

        if(editTextView != null){
            windowManager.removeView(editTextView);
        }

        if(txtView != null){
            windowManager.removeView(txtView);
        }

        if(removeView != null){
            windowManager.removeView(removeView);
        }

    }

    @Override
    public void setUIListener(UIListener uiListener) {
        this.uiListener = uiListener;
    }

    @Override
    public void giveMessage(Message msg) {
        mAdapter.addMessage(msg);
        setUIStatus(UIStatus.READY);
        showMsg("Message Received.");
    }

    @Override
    public void updateMessages(Message[] msgs) {
        mAdapter.updateMessages(msgs);
        showMsg("Conversation Retrieved.");
    }

    @Override
    public void setActiveAppName(String new_app_name) {
        this.activeApp = new_app_name;
    }

    @Override
    public void setAuthorName(String author_name) {
        this.authorName = author_name;
    }

    @Override
    public void setRecipientName(String recipient_name) {
        this.recipientName = recipient_name;
    }

    @Override
    public UIWindowState getUIWindowState() {
        return this.uiWindowState;
    }

    @Override
    public UIStatus getUIStatus() {
        return this.uiStatus;
    }

    private void setUIStatus(UIStatus uistatus) {
        switch(uistatus)
        {
            case ACTIVE:
                chatheadImg.setImageResource(R.drawable.encryptany_logo_blue);
                chatheadImg.setAlpha((float)1.0);
                break;
            case AWAITING_CLICK:
                showMsg("Please click the text box");
                chatheadImg.setImageResource(R.drawable.encryptany_logo_yellow);
                chatheadImg.setAlpha((float)1.0);
                break;
            case BUSY:
                showMsg("Performing operations");
                chatheadImg.setImageResource(R.drawable.encryptany_logo_red);
                chatheadImg.setAlpha((float)1.0);
                break;
            case INACTIVE:
                chatheadImg.setImageResource(R.drawable.encryptany_logo_blue);
                chatheadImg.setAlpha((float)0.6);
                newMessage = null;
                break;
            case READY:
                chatheadImg.setImageResource(R.drawable.encryptany_logo_green);
                chatheadImg.setAlpha((float)1.0);
                break;
            default:
                break;
        }
        this.uiStatus = uistatus;
    }

    @Override
    public void waitForUserSend() {
        setUIStatus(UIStatus.AWAITING_CLICK);
    }

    @Override
    public void disable() {
        setUIStatus(UIStatus.INACTIVE);
        mAdapter.clearMessages();
    }

    @Override
    public void enable() {
        setUIStatus(UIStatus.ACTIVE);
    }

    @Override
    public void waitForProcessing() {
        setUIStatus(UIStatus.BUSY);
    }

    @Override
    public void doneWaiting() {
        setUIStatus(UIStatus.READY);
    }

    @Override
    public void setUIWindowState_Minimized() {
        chatheadView.setVisibility(View.VISIBLE);
        removeView.setVisibility(View.GONE);
        editTextView.setVisibility(View.GONE);
        overlayView.setVisibility(View.GONE);
        this.uiWindowState = UIWindowState.MINIMIZED;
    }

    @Override
    public void setUIWindowState_Showing() {

    }
}
