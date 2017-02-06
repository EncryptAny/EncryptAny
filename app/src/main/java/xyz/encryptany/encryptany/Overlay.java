package xyz.encryptany.encryptany;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Overlay extends Activity {
    public static boolean active = false;
    public static Activity overlayActivity;

    private RecyclerView recyclerView;
    private OverlayRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] dummyData = {"dummymsg1","dummymsg2","dummymsg3","dummymsg4","dummymsg5"};

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

        mAdapter = new OverlayRecyclerViewAdapter(dummyData);
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