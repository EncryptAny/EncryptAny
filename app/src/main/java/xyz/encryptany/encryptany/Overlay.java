package xyz.encryptany.encryptany;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import xyz.encryptany.encryptany.concrete.EncryptedMessage;

public class Overlay extends Activity {
    public static boolean active = false;
    public static Activity overlayActivity;

    private RecyclerView recyclerView;
    private OverlayRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EncryptedMessage[] GenDummyData()
    {
        final int dummyAmount = 20;
        EncryptedMessage[] dummydata = new EncryptedMessage[dummyAmount];
        for (int i = 1; i != dummyAmount+1; ++i)
        {
            dummydata[i-1] = new EncryptedMessage("Dummy encrypted text " + i,"Dummy source " + i,"Dummy app " + i, new Date().getTime());
        }
        return dummydata;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay);
        recyclerView = (RecyclerView) findViewById(R.id.overlayRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new OverlayRecyclerViewAdapter(GenDummyData());
        recyclerView.setAdapter(mAdapter);

        overlayActivity = Overlay.this;
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        active = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        active = false;
    }



}