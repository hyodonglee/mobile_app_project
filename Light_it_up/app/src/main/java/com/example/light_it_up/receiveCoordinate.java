package com.example.light_it_up;

import android.graphics.Color;
import android.util.Log;

import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;



import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class receiveCoordinate {


        ArrayList<TMapPoint> previousList = new ArrayList<>();
        ArrayList<TMapPoint> pointList = new ArrayList<TMapPoint>();

        TMapView TMapView;

        TMapPolyLine tMapPolyLine;

        private receiveCoordinate httpConn = receiveCoordinate.getInstance();
        private static OkHttpClient client;
        private  static receiveCoordinate instance = new receiveCoordinate();
        public static receiveCoordinate getInstance() {
            return instance;
        }

        receiveCoordinate(TMapView mapview){
            TMapView=mapview;
        }


        public ArrayList<TMapPoint> sendData(Double startX,Double startY,Double endX,Double endY) {
            postThread request = new postThread(startX,startY,endX,endY);
            request.run();

            try{
                request.join();
            }
            catch(Exception e){

            }
            return pointList;
        }


        public receiveCoordinate(){ this.client = new OkHttpClient(); }

        /** 웹 서버로 요청을 한다. */
        public void requestWebServer(Double startX,Double startY,Double endX,Double endY,Callback callback) {
            RequestBody body = new FormBody.Builder()
                    .add("appKey","l7xx0dffe4a89bff4b39b4cf9a19a0d5292a")
                    .add("startX", startX.toString())
                    .add("startY", startY.toString())
                    .add("angle", "1")
                    .add("endX", endX.toString())
                    .add("endY", endY.toString())
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
                                pointList.add(new TMapPoint(Double.parseDouble(coord.second()),Double.parseDouble(coord.first())));
                            }
                        }
                    }

                    drawLine(pointList);




//                    for (Coord coord : coordinates) {
//                        System.out.println(coord.first() + " " + coord.second());
//                    }



                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.d("Main", "콜백오류:"+e.getMessage());
            }


        };

    public void drawLine(ArrayList<TMapPoint> pointList){

        tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.BLUE);
        tMapPolyLine.setLineWidth(4);

        for( int i=0; i<pointList.size(); i++ ) {
            tMapPolyLine.addLinePoint( pointList.get(i) );
        }

        TMapView.addTMapPolyLine("Line1", tMapPolyLine);
    }

    public void deleteRoadLine(){
        previousList=tMapPolyLine.getLinePoint();
        tMapPolyLine= new TMapPolyLine();
        TMapView.addTMapPolyLine("Line1",tMapPolyLine);
    }

    public void redrawRoadLine(){
        drawLine(previousList);
    }


    private class postThread extends Thread {

        Double startX,startY,endX,endY;

        postThread(Double startX, Double startY, Double endX, Double endY){
            this.startX=startX;
            this.startY=startY;
            this.endX=endX;
            this.endY=endY;
        }

        public void run() {
            httpConn.requestWebServer(startX, startY, endX, endY, callback);
        }
    }


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

