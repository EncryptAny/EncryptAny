package xyz.encryptany.encryptany;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import xyz.encryptany.encryptany.testing.FakeUIAdapter;
import xyz.encryptany.encryptany.concrete.OTREncryptor;
import xyz.encryptany.encryptany.services.AccessibilityAppAdapter;
import xyz.encryptany.encryptany.testing.NoOpEncryptor;

public class MainActivity extends AppCompatActivity {

    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    public Button btnStartService, btnShowMsg;
    private Mediator m = null;

    // MAXWELL DEBUG
    FakeUIAdapter fui = new FakeUIAdapter();
    // END MAXWELL DEBUG


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = (Button)findViewById(R.id.btnStartService);
        btnShowMsg = (Button)findViewById(R.id.btnMsg);

        btnStartService.setOnClickListener(lst_StartService);
        btnShowMsg.setOnClickListener(lst_ShowMsg);

        m = new Mediator(new AccessibilityAppAdapter(), fui, new NoOpEncryptor(), null);
        Log.d("MAXWELL", "Activity started");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MAXWELL", "Activity Paused");
    }

    Button.OnClickListener lst_StartService = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d(Utils.LogTag, "lst_StartService -> Utils.canDrawOverlays(Main.this): " + Utils.canDrawOverlays(MainActivity.this));

            if(Utils.canDrawOverlays(MainActivity.this)) {
                // Add debug code to run here
                // BEGIN MAXWELL DEBUG
                //fui.fakeDelay();
                // END MAXWELL DEBUG
                startChatHead();
            } else {
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
            }
        }

    };

    Button.OnClickListener lst_ShowMsg = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(Utils.canDrawOverlays(MainActivity.this))
                showChatHeadMsg();
            else{
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG);
            }

        }

    };

    private void startChatHead(){
        startService(new Intent(MainActivity.this, ChatHeadService.class));
    }
    private void showChatHeadMsg(){
        java.util.Date now = new java.util.Date();
        String str = "test by henry  " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        Intent it = new Intent(MainActivity.this, ChatHeadService.class);
        it.putExtra(Utils.EXTRA_MSG, str);
        startService(it);
    }

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }
}
