package com.example.light_it_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import static java.lang.Thread.sleep;


public class viewHome extends AppCompatActivity {


    static receiveCoordinate receive;

    static TMapView tMapView;
    static ArrayList<String> searchListStart= new ArrayList<>();
    static ArrayList<String> searchListEnd = new ArrayList<>();

    static HashMap<String,String> poiMapStart = new HashMap<>();
    static HashMap<String,String> poiMapEnd = new HashMap<>();

    static AutoCompleteTextView autoCompleteTextViewStart;
    static AutoCompleteTextView autoCompleteTextViewEnd;

    static boolean viewRoadCheck=false;
    static boolean viewRoadLightCheck=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_home);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.layoutMap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx9e4f453a79804608bc16947e4ed09909");
        linearLayoutTmap.addView(tMapView);


        autoCompleteTextViewStart = (AutoCompleteTextView) findViewById(R.id.autoCompleteAddressStart);
        autoCompleteTextViewEnd = (AutoCompleteTextView) findViewById(R.id.autoCompleteAddressEnd);

        autoCompleteTextViewStart.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setList(s.toString(),searchListStart,poiMapStart);
                autoCompleteTextViewStart.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, searchListStart));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteTextViewEnd.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setList(s.toString(),searchListEnd,poiMapEnd);
                autoCompleteTextViewEnd.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, searchListEnd));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }



    public static void setList(String search, ArrayList<String> addlist, HashMap<String,String> hashMap){


        TMapData tmapdata = new TMapData();

        tmapdata.findAllPOI(search, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList poiItem) {
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem  item = (TMapPOIItem) poiItem.get(i);
                    if(addlist.contains(item.getPOIName()))
                        continue;
                    addlist.add(item.getPOIName());
                    hashMap.put(item.getPOIName().toString(),item.getPOIPoint().toString());
                }
            }
        });


    }



    public void findRoadClick(View view) throws Exception {
        String textStart = autoCompleteTextViewStart.getText().toString();
        String textGoal = autoCompleteTextViewEnd.getText().toString();


        String LocationStart=convertToLocation(textStart,1);
        String LocationEnd=convertToLocation(textGoal,2);

        StringTokenizer stS = new StringTokenizer(LocationStart);
        StringTokenizer stG = new StringTokenizer(LocationEnd);

        stS.nextToken();
        double startY=Double.parseDouble(stS.nextToken());
        stS.nextToken();
        double startX=Double.parseDouble(stS.nextToken());
        stG.nextToken();
        double endY=Double.parseDouble(stG.nextToken());
        stG.nextToken();
        double endX=Double.parseDouble(stG.nextToken());

        viewRoadCheck=true;
        setMarker(startX,startY,endX,endY);
        tMapView.zoomToSpan(Math.abs(startY-endY),Math.abs(startX-endX));
        receive = new receiveCoordinate(tMapView);
        receive.sendData(startX,startY,endX,endY);

        // 여기 위는 일반 가로등 찾기 기


    }

    public String convertToLocation(String address,int option){


        if(option==1){ // start
            if(poiMapStart.containsKey(address))
                return poiMapStart.get(address);

        }
        else if(option==2){ // end
            if(poiMapEnd.containsKey(address))
                return poiMapEnd.get(address);
        }

        return null;
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


    public void GPS(View view) {


        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        else {

            gpsTracker locate = new gpsTracker(this);
            double locate_latitude = locate.getLatitude();
            double locate_longitude = locate.getLongitude();

            Toast.makeText(getApplicationContext(),
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

    public void showLoad(View view,int option) {

        final int normal=1;
        final int light=2;

        if(option==normal) {
            if (viewRoadCheck) { // on
                receive.deleteRoadLine();
                viewRoadCheck = false;
            } else { // off
                receive.redrawRoadLine();
                viewRoadCheck = true;
            }
        }
        else if(option==light){
            if (viewRoadLightCheck) { // light on
                //receive.deleteRoadLine();
                viewRoadLightCheck = false;
            } else { // light off
                //receive.redrawRoadLine();
                viewRoadLightCheck = true;
            }

        }

    }

}






