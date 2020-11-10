package com.example.light_it_up;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapView;

public class viewHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_home);

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.layoutMap);
        TMapView tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey("l7xx9e4f453a79804608bc16947e4ed09909");
        linearLayoutTmap.addView( tMapView );
    }
}