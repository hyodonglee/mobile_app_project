package com.example.light_it_up;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_home);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.layoutMap);
        TMapView tMapView = new TMapView(this);

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

        HttpConnection test = new HttpConnection();
        test.sendData();
    }


}


    class HttpConnection {


        ArrayList<Coord> coordinates = new ArrayList<Coord>();

        private HttpConnection httpConn = HttpConnection.getInstance();
        private static OkHttpClient client;
        private  static HttpConnection instance = new HttpConnection();
        public static HttpConnection getInstance() {
            return instance;
        }


        public void sendData() {
            new Thread() {
                public void run() {
                    httpConn.requestWebServer(callback);
                }
            }.start();;
        }

        public HttpConnection(){ this.client = new OkHttpClient(); }

        /** 웹 서버로 요청을 한다. */
        public void requestWebServer(Callback callback) {
            RequestBody body = new FormBody.Builder()
                    .add("appKey","l7xx0dffe4a89bff4b39b4cf9a19a0d5292a")
                    .add("startX", "126.92365493654832")
                    .add("startY", "37.556770374096615")
                    .add("angle", "1")
                    .add("endX", "126.92432158129688")
                    .add("endY", "37.55279861528311")
                    .add("reqCoordType", "WGS84GEO")
                    .add("startName", "출발지")
                    .add("endName", "도착지")
                    .add("resCoordType", "WGS84GEO")
                    .build();


            Request request = new Request.Builder()
                    .url("https://api2.sktelecom.com/tmap/routes/pedestrian")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);
        }


        private final Callback callback = new Callback() {
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {

                String body = response.body().string();
                Log.d("Main", "서버에서 응답한 Body:" + body);

                try {
                    JSONParser jsonParse = new JSONParser();
                    JSONObject jsonObj = (JSONObject) jsonParse.parse(body);
                    JSONArray featuresArray = (JSONArray) jsonObj.get("features");
                    for (int i = 0; i < featuresArray.size(); i++) {
                        JSONObject feature = (JSONObject) featuresArray.get(i);
                        JSONObject geometry = (JSONObject) feature.get("geometry");

                        if (geometry.get("type").toString().equals("LineString")) {
                            JSONArray coords = (JSONArray) geometry.get("coordinates");
                            for (int j = 0; j < coords.size(); ++j) {
                                JSONArray vertex = (JSONArray) coords.get(j);
                                Coord coord = new Coord(vertex.get(0).toString(), vertex.get(1).toString());
                                coordinates.add(coord);
                            }
                        }
                    }

                    for (Coord coord : coordinates) {
                        System.out.println(coord.first() + " " + coord.second());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.d("Main", "콜백오류:"+e.getMessage());
            }
        };


        private static class Coord {
            String x;
            String y;

            public Coord(String _x, String _y) {
                this.x = _x;
                this.y = _y;
            }

            public String first() {
                return this.x;
            }
            public String second() {
                return this.y;
            }
        }

    }