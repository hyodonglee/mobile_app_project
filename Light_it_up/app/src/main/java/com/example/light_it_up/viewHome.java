package com.example.light_it_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.jetbrains.annotations.NotNull;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;


import java.util.StringTokenizer;


import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;



public class viewHome extends AppCompatActivity {


    static TMapView tMapView;


    public static TMapView getMapView(){
        return tMapView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_home);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.layoutMap);
        tMapView=new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx9e4f453a79804608bc16947e4ed09909");
        linearLayoutTmap.addView(tMapView);
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
        }



    }



}






