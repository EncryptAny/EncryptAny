package xyz.encryptany.encryptany;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static Button btnStartService, btnShowMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = (Button)findViewById(R.id.btnStartService);
        btnShowMsg = (Button)findViewById(R.id.btnMsg);
    }
}
