package com.example.light_it_up.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.light_it_up.R;
import com.example.light_it_up.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    static View view;
    RelativeLayout mapView;
    static TMapView tMapView;
    static List<String> serchList = new ArrayList<String>();
    String apiKey = "l7xx9e4f453a79804608bc16947e4ed09909";
    FloatingActionButton fab_main, fab_sub1, fab_sub2;
    Animation fab_open, fab_close;
    Boolean openFlag = false;
    Button gps;
    Button findroad;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mapView = (RelativeLayout) view.findViewById(R.id.map);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        fab_main = (FloatingActionButton) view.findViewById(R.id.fab_main);
        fab_sub1 = (FloatingActionButton) view.findViewById(R.id.fab_sub1);
        fab_sub2 = (FloatingActionButton) view.findViewById(R.id.fab_sub2);

        fab_sub1.startAnimation(fab_close);
        fab_sub2.startAnimation(fab_close);
        fab_sub1.setClickable(false);
        fab_sub2.setClickable(false);
        openFlag = false;
        // 초반 클릭하기 전에 메뉴바 숨기기 위한 코

        setList("대구");
        setList("경북대학교 IT");

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteAddress);

        // AutoCompleteTextView 에 아답터를 연결한다.
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, serchList ));

        gps = view.findViewById(R.id.button);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( getActivity(), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            0 );
                }
                else {

                    gpsTracker locate = new gpsTracker(getContext());
                    double locate_latitude = locate.getLatitude();
                    double locate_longitude = locate.getLongitude();

                    Toast.makeText(getContext(),
                            "위도 : " + locate_longitude + "\n" +
                                    "경도 : " + locate_latitude + "\n",
                            Toast.LENGTH_SHORT).show();

                    TMapMarkerItem markerGPS = new TMapMarkerItem();

                    TMapPoint GpsPoint = new TMapPoint(locate_latitude, locate_longitude);

                    Bitmap bitmapGps = BitmapFactory.decodeResource(getResources(), R.drawable.map_markergps);

                    markerGPS.setIcon(bitmapGps); // 마커 아이콘 지정
                    markerGPS.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
                    markerGPS.setTMapPoint( GpsPoint ); // 마커의 좌표 지정
                    markerGPS.setName("현위치"); // 마커의 타이틀 지정
                    tMapView.addMarkerItem("markerGPS", markerGPS); // 지도에 마커 추가
                    tMapView.setCenterPoint( locate_longitude,locate_latitude );
                }
            }
        });

        findroad = view.findViewById(R.id.btn_findRoad);
        findroad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextStart = view.findViewById(R.id.editTextStart);
                EditText editTextGoal = view.findViewById(R.id.editTextGoal);

                if(editTextStart.getText()==null){
                    //error handling
                }

                else if(editTextGoal.getText()==null){

                }

                else {
                    String textStart = editTextStart.getText().toString();
                    String textGoal = editTextGoal.getText().toString();
                    StringTokenizer stS = new StringTokenizer(textStart);
                    StringTokenizer stG = new StringTokenizer(textGoal);

                    double startX = Double.parseDouble(stS.nextToken());
                    double startY = Double.parseDouble(stS.nextToken());
                    double endX = Double.parseDouble(stG.nextToken());
                    double endY = Double.parseDouble(stG.nextToken());

                    setMarker(startX, startY, endX, endY);

                    receiveCoordinate receive = new receiveCoordinate();
                    drawLine(receive.sendData(startX, startY, endX, endY));
                }
            }
        });

        //버튼 클릭 리스너
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




    public static void setList(String search){


        TMapData tmapdata = new TMapData();


        tmapdata.findAllPOI(search, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList poiItem) {
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
                    serchList.add(item.getPOIName());
//                    Log.d("POI Name: ", item.getPOIName().toString() + ", " +
//                            "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
//                            "Point: " + item.getPOIPoint().toString());
                }
            }
        });


    }

    public void findRoadClick(View view) throws Exception {
        EditText editTextStart = (EditText) view.findViewById(R.id.editTextStart);
        EditText editTextGoal = (EditText) view.findViewById(R.id.editTextGoal);

        String textStart = editTextStart.getText().toString();
        String textGoal = editTextGoal.getText().toString();

        StringTokenizer stS = new StringTokenizer(textStart);
        StringTokenizer stG = new StringTokenizer(textGoal);

        double startX=Double.parseDouble(stS.nextToken());
        double startY=Double.parseDouble(stS.nextToken());
        double endX=Double.parseDouble(stG.nextToken());
        double endY=Double.parseDouble(stG.nextToken());

        setMarker(startX,startY,endX,endY);

        receiveCoordinate receive = new receiveCoordinate();
        drawLine(receive.sendData(startX,startY,endX,endY));
    }

    public void setMarker(double startX,double startY,double endX,double endY){
        TMapMarkerItem markerItem1 = new TMapMarkerItem();
        TMapMarkerItem markerItem2 = new TMapMarkerItem();

        TMapPoint tMapPoint1 = new TMapPoint(startY, startX);
        TMapPoint tMapPoint2 = new TMapPoint(endY,endX);

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker2);

        markerItem1.setIcon(bitmap1); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
        markerItem1.setName("출발"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
        tMapView.setCenterPoint( startX, startY );

        markerItem2.setIcon(bitmap2); // 마커 아이콘 지정
        markerItem2.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem2.setTMapPoint( tMapPoint2 ); // 마커의 좌표 지정
        markerItem2.setName("도"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("markerItem1차2", markerItem2); // 지도에 마커 추가

    }

    public void drawLine(ArrayList<TMapPoint> pointList){
        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.BLUE);
        tMapPolyLine.setLineWidth(2);

        for( int i=0; i<pointList.size(); i++ ) {
            tMapPolyLine.addLinePoint( pointList.get(i) );
        }

        tMapView.addTMapPolyLine("Line1", tMapPolyLine);

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

//    public void serachAddress(View view) {
//
//        EditText seraching = (EditText) findViewById(R.id.editTextAddress);
//        String strData = seraching.getText().toString();
//
//        TMapData tmapdata = new TMapData();
//
//        tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
//            @Override
//            public void onFindAllPOI(ArrayList poiItem) {
//                for(int i = 0; i < poiItem.size(); i++) {
//                    TMapPOIItem  item = (TMapPOIItem) poiItem.get(i);
//                    Log.d("POI Name: ", item.getPOIName().toString() + ", " +
//                            "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
//                            "Point: " + item.getPOIPoint().toString());
//                }
//            }
//        });
//
//    }