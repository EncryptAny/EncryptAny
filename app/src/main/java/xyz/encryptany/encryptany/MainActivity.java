package xyz.encryptany.encryptany;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import xyz.encryptany.encryptany.services.UIService;
import xyz.encryptany.encryptany.testing.FakeUIAdapter;
import xyz.encryptany.encryptany.services.AccessibilityAppAdapter;
import xyz.encryptany.encryptany.testing.NoOpArchiver;
import xyz.encryptany.encryptany.testing.NoOpEncryptor;

public class MainActivity extends AppCompatActivity {

    private static final String START_SERVICE_TOAST = "Please scroll down and enable \"EncryptAny\" under \"Services\"";

    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    public Button btnStartService, btnShowMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = (Button)findViewById(R.id.btnStartService);

        btnStartService.setOnClickListener(lst_StartService);
    }

    Button.OnClickListener lst_StartService = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(Utils.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                Toast.makeText(MainActivity.this, START_SERVICE_TOAST,
                        Toast.LENGTH_LONG).show();
                startActivity(intent);
            } else {
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
            }
        }
    };

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
