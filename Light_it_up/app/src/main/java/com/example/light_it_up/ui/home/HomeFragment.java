package com.example.light_it_up.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.light_it_up.R;
import com.example.light_it_up.VideoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skt.Tmap.TMapView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    View view;
    RelativeLayout mapView;
    TMapView tMapView;
    String apiKey = "l7xx9e4f453a79804608bc16947e4ed09909";
    FloatingActionButton fab_main, fab_sub1, fab_sub2;
    Animation fab_open, fab_close;
    Boolean openFlag = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mapView = (RelativeLayout) view.findViewById(R.id.map);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        fab_main = (FloatingActionButton) view.findViewById(R.id.fab_main);
        fab_sub1 = (FloatingActionButton) view.findViewById(R.id.fab_sub1);
        fab_sub2 = (FloatingActionButton) view.findViewById(R.id.fab_sub2);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim();
            }
        });

        fab_sub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getActivity(), VideoActivity.class);
                startActivity(intent1);
            }
        });

        fab_sub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "길찾기", Toast.LENGTH_SHORT).show();
            }
        });

        tMapView = new TMapView(getActivity());
        tMapView.setHttpsMode(true);

        tMapView.setSKTMapApiKey(apiKey);
        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);  //일반지도
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        mapView.addView(tMapView);

        return view;
    }

    private void anim() {
        if (openFlag) {
            fab_sub1.startAnimation(fab_close);
            fab_sub2.startAnimation(fab_close);
            fab_sub1.setClickable(false);
            fab_sub2.setClickable(false);
            openFlag = false;
        } else {
            fab_sub1.startAnimation(fab_open);
            fab_sub2.startAnimation(fab_open);
            fab_sub1.setClickable(true);
            fab_sub2.setClickable(true);
            openFlag = true;
        }
    }
}