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


import java.util.List;
import java.util.StringTokenizer;





public class viewHome extends AppCompatActivity {


    static TMapView tMapView;
    static List<String> searchList= new ArrayList<>();
    static List<String> tempsearchList = new ArrayList<>();
    static AutoCompleteTextView autoCompleteTextView;


    public static TMapView getMapView() {
        return tMapView;
    }

    public static List<String> getSerchList() {
        return searchList;
    }

    public static AutoCompleteTextView getAutoCompleteTextView(){ return autoCompleteTextView;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_home);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.layoutMap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx9e4f453a79804608bc16947e4ed09909");
        linearLayoutTmap.addView(tMapView);


        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteAddress);


        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            //AsyncaddList asyncadd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //asyncadd = new AsyncaddList();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setList(s.toString());
                searchList=tempsearchList;
                autoCompleteTextView.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, tempsearchList));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    
    public static void setList(String search){


        TMapData tmapdata = new TMapData();

        tmapdata.findAllPOI(search, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList poiItem) {
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem  item = (TMapPOIItem) poiItem.get(i);
                    tempsearchList.add(item.getPOIName());
                    Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
                            "Point: " + item.getPOIPoint().toString());
                }
            }
        });


    }

    public void findRoadClick(View view) throws Exception {
        EditText editTextStart = (EditText) findViewById(R.id.editTextStart);
        EditText editTextGoal = (EditText) findViewById(R.id.editTextGoal);

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


}






