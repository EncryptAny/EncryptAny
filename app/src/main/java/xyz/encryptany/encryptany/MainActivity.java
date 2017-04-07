package xyz.encryptany.encryptany;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import xyz.encryptany.encryptany.services.UIService;
import xyz.encryptany.encryptany.testing.FakeUIAdapter;
import xyz.encryptany.encryptany.services.AccessibilityAppAdapter;
import xyz.encryptany.encryptany.testing.NoOpArchiver;
import xyz.encryptany.encryptany.testing.NoOpEncryptor;

public class MainActivity extends AppCompatActivity {

    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    public Button btnStartService, btnShowMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = (Button)findViewById(R.id.btnStartService);
        btnShowMsg = (Button)findViewById(R.id.btnMsg);

        btnStartService.setOnClickListener(lst_StartService);
        btnShowMsg.setOnClickListener(lst_ShowMsg);
    }

    Button.OnClickListener lst_StartService = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(Utils.canDrawOverlays(MainActivity.this)) {
                startChatHead();
            } else {
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
            }
        }
    };

    Button.OnClickListener lst_ShowMsg = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(Utils.canDrawOverlays(MainActivity.this))
                showChatHeadMsg();
            else{
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG);
            }
        }
    };

    private void startChatHead(){
        startService(new Intent(MainActivity.this, UIService.class));
    }
    private void showChatHeadMsg(){
        Intent it = new Intent(MainActivity.this, UIService.class);
        startService(it);
    }

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }
}
