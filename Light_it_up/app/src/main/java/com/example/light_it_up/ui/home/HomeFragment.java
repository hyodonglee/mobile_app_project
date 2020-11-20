package com.example.light_it_up.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import com.example.light_it_up.*;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class HomeFragment extends Fragment {

    static receiveCoordinate receive;
    static receiveCoordinateLight receiveLight;

    static View view;
    RelativeLayout mapView;
    static TMapView tMapView;

    String apiKey = "l7xx9e4f453a79804608bc16947e4ed09909";
    Button report;

    FloatingActionButton gps;
    Button findRoad;


    Button showroad;
    Button showRoadLight;

    static ArrayList<String> searchListStart = new ArrayList<>();
    static ArrayList<String> searchListEnd = new ArrayList<>();

    static HashMap<String, String> poiMapStart = new HashMap<>();
    static HashMap<String, String> poiMapEnd = new HashMap<>();

    static AutoCompleteTextView autoCompleteTextViewStart;
    static AutoCompleteTextView autoCompleteTextViewEnd;

    static boolean viewRoadCheck = false;
    static boolean viewRoadLightCheck = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mapView = (RelativeLayout) view.findViewById(R.id.layoutMap);


        report = (Button) view.findViewById(R.id.report);

        // AutoCompleteTextView 에 아답터를 연결한다.
        autoCompleteTextViewStart = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteAddressStart);
        autoCompleteTextViewEnd = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteAddressEnd);

        autoCompleteTextViewStart.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setList(s.toString(), searchListStart, poiMapStart);
                autoCompleteTextViewStart.setAdapter(new ArrayAdapter<>(getContext(),
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
                setList(s.toString(), searchListEnd, poiMapEnd);
                autoCompleteTextViewEnd.setAdapter(new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, searchListEnd));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        gps = view.findViewById(R.id.btn_GPS);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            0);
                } else {

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
                    markerGPS.setTMapPoint(GpsPoint); // 마커의 좌표 지정
                    markerGPS.setName("현위치"); // 마커의 타이틀 지정
                    tMapView.addMarkerItem("markerGPS", markerGPS); // 지도에 마커 추가
                    tMapView.setCenterPoint(locate_longitude, locate_latitude);
                }
            }
        });


        findRoad = view.findViewById(R.id.btn_findRoad);
        findRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textStart = autoCompleteTextViewStart.getText().toString();
                String textGoal = autoCompleteTextViewEnd.getText().toString();


                String LocationStart = convertToLocation(textStart, 1);
                String LocationEnd = convertToLocation(textGoal, 2);

                StringTokenizer stS = new StringTokenizer(LocationStart);
                StringTokenizer stG = new StringTokenizer(LocationEnd);

                stS.nextToken();
                double startY = Double.parseDouble(stS.nextToken());
                stS.nextToken();
                double startX = Double.parseDouble(stS.nextToken());
                stG.nextToken();
                double endY = Double.parseDouble(stG.nextToken());
                stG.nextToken();
                double endX = Double.parseDouble(stG.nextToken());

                viewRoadCheck = true;
                setMarker(startX, startY, endX, endY);

                tMapView.zoomToSpan(Math.abs(startY - endY), Math.abs(startX - endX));
                receive = new receiveCoordinate(tMapView);
                receive.sendData(startX, startY, endX, endY);

                receiveCoordinateLight receiveLight;
                receiveLight=new receiveCoordinateLight(tMapView,getContext());
                receiveLight.sendDataLight(startX,startY,endX,endY);
            }
        });

        //버튼 클릭 리스너

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //우선 위치정보부터 받아옴
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            0);
                } else {
                    gpsTracker locate = new gpsTracker(getContext());
                    double locate_latitude = locate.getLatitude();
                    double locate_longitude = locate.getLongitude();

                    SmsManager smsManager = SmsManager.getDefault();
                    String sendTo = "01081319117";
                    //String sendTo = "01028638656";
                    //String sendTo = "01090856697";
                    //String sendTo = "01030604595";


                    new TMapData().convertGpsToAddress(locate_latitude, locate_longitude,
                            new TMapData.ConvertGPSToAddressListenerCallback() {
                                @Override
                                public void onConvertToGPSToAddress(String strAddress) {
                                    Log.d("address",strAddress);
                                    System.out.println(strAddress);

                                    String myMessage = "위급상황 발생!!!!\n" + "현재 신고자 위치 좌표 : \n"
                                            + "위도 = " + locate_latitude + "\n" + "경도 = " + locate_longitude + "\n" + "현위치 = " + strAddress+"\n"
                                            + "신속 출동 바람.";
                                    smsManager.sendTextMessage(sendTo, null, myMessage, null, null);
                                    Toast.makeText(getContext(), "메세지 신고 완료", Toast.LENGTH_SHORT).show();
                                    // 긴급 메세지 신고 전송 기능 구현

                                }
                            });



                    Intent intent1 = new Intent(getActivity(), VideoActivity.class);
                    startActivity(intent1);
                }
            }
        });

        showroad = (Button) view.findViewById(R.id.btn_showLoad);
        showroad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (viewRoadCheck) { // on
                        receive.deleteRoadLine();
                        viewRoadCheck = false;
                    } else { // off
                        receive.redrawRoadLine();
                        viewRoadCheck = true;
                    }

                }
            });

        showRoadLight = (Button) view.findViewById(R.id.bnt_showLoadlight);
        showRoadLight.setOnClickListener(new View.OnClickListener() {//가로등경로 리스너
            @Override
            public void onClick(View view) {

                receiveCoordinateLight receiveLight;
                receiveLight=new receiveCoordinateLight(tMapView,getContext());
                receiveLight.sendDataLight(startX,startY,endX,endY);
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

    public String convertToLocation(String address, int option) {


        if (option == 1) { // start
            if (poiMapStart.containsKey(address))
                return poiMapStart.get(address);

        } else if (option == 2) { // end
            if (poiMapEnd.containsKey(address))
                return poiMapEnd.get(address);
        }

        return null;
    }


    public static void setList(String search, ArrayList<String> addlist, HashMap<String, String> hashMap) {


        TMapData tmapdata = new TMapData();

        tmapdata.findAllPOI(search, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
                    if (addlist.contains(item.getPOIName()))
                        continue;
                    addlist.add(item.getPOIName());
                    hashMap.put(item.getPOIName().toString(), item.getPOIPoint().toString());
                }
            }
        });


    }


    public void setMarker(double startX, double startY, double endX, double endY) {
        TMapMarkerItem markerItem1 = new TMapMarkerItem();
        TMapMarkerItem markerItem2 = new TMapMarkerItem();

        TMapPoint tMapPoint1 = new TMapPoint(startY, startX);
        TMapPoint tMapPoint2 = new TMapPoint(endY, endX);

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker2);

        markerItem1.setIcon(bitmap1); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint(tMapPoint1); // 마커의 좌표 지정
        markerItem1.setName("출발"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
        tMapView.setCenterPoint(startX, startY);

        markerItem2.setIcon(bitmap2); // 마커 아이콘 지정
        markerItem2.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem2.setTMapPoint(tMapPoint2); // 마커의 좌표 지정
        markerItem2.setName("도"); // 마커의 타이틀 지정
        tMapView.addMarkerItem("markerItem1차2", markerItem2); // 지도에 마커 추가

    }

}