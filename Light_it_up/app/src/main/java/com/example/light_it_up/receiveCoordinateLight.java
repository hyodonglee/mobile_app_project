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

public class receiveCoordinateLight {



    private static ArrayList<receiveCoordinateLight.Coord> ansList = new ArrayList<>();

    public static ArrayList<Coord> coordinates = new ArrayList<>();

    public static ArrayList<Coord> roadInPath = new ArrayList<>();
    public static ArrayList<Coord> streetInPath = new ArrayList<>();


    public static ArrayList<ArrayList<Coord>> boundLamp;
    public static ArrayList<Coord> roadBound;
    public static ArrayList<Coord> streetBound;

    public static Coord firstDetourStart;
    public static Coord firstDetourEnd;
    public static int firstDetourStartIndex;
    public static int firstDetourEndIndex;
    public static boolean isDetour = false;

    public static Double startX;
    public static Double startY;
    public static Double endX;
    public static Double endY;




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

    public receiveCoordinateLight(TMapView mapview){
        TMapView=mapview;
        this.client = new OkHttpClient();
    }


    public ArrayList<TMapPoint> sendDataLight(Double startX,Double startY,Double endX,Double endY) {

        this.startX=startX;
        this.startY=startY;
        this.endX=endX;
        this.endY=endY;

        postThread request = new postThread(this.startX,this.startY,this.endX,this.endY);
        request.run();

        try{
            request.join();
        }
        catch(Exception e){

        }
        return pointList;
    }

    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(Callback callback) {
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
                            Coord coord = new Coord(vertex.get(1).toString(), vertex.get(0).toString());
                            coordinates.add(coord);
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            LampExtraction lamp = new LampExtraction();
            boundLamp = lamp.getBoundCoord(startX, startY, endX, endY); // 경계 내부에 있는 (가로등, 보안등) 좌표 얻어옴.
            roadBound = boundLamp.get(0); // 경계 내부에 있는 가로등 좌표
            streetBound = boundLamp.get(1); // 경계 내부에 있는 보안등 좌표

            navigate();


            ArrayList<receiveCoordinateLight.Coord> aroundLamps = AroundSearchLamp.aroundLamps(firstDetourStartIndex,firstDetourEndIndex, coordinates, roadBound,roadInPath,streetBound, streetInPath);

            // 가장 가까운 가로등 좌표
            receiveCoordinateLight.Coord nextLamp = shortestLamp.findShortestLamp(firstDetourStart, roadInPath, streetInPath, aroundLamps);
            //System.out.println();

            if(isDetour)
            {
                ansList.addAll(coordinates.subList(0, firstDetourStartIndex));
                receiveCoordinateLight reqMidPath = new receiveCoordinateLight(TMapView);
                reqMidPath.sendDataLight(Double.parseDouble(firstDetourStart.first()), Double.parseDouble(firstDetourStart.second()), Double.parseDouble(nextLamp.first()), Double.parseDouble(nextLamp.second()));
                ansList.addAll(reqMidPath.coordinates);
                //test(nextLamp.first(), nextLamp.second(), endX, endY);
            }
            else{
                ansList.addAll(coordinates);
                ArrayList<TMapPoint> temp=addTMapPoint(coordinates);
                drawLine(temp);

            }


        }


        @Override
        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            Log.d("Main", "콜백오류:"+e.getMessage());
        }


    };

    public ArrayList<TMapPoint> addTMapPoint(ArrayList<receiveCoordinateLight.Coord> list){
        ArrayList<TMapPoint> returnList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            returnList.add(new TMapPoint(Double.parseDouble(list.get(i).second()),Double.parseDouble(list.get(i).first())));
        }

        return returnList;
    }

    public void drawLine(ArrayList<TMapPoint> pointList){

        tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.YELLOW);
        tMapPolyLine.setLineWidth(4);

        for( int i=0; i<pointList.size(); i++ ) {
            tMapPolyLine.addLinePoint( pointList.get(i) );
        }

        TMapView.addTMapPolyLine("Line1", tMapPolyLine);
    }



    public static void navigate(){
        // 경로 탐색 가능 판단용 부분 변수
        int startPoint=0, roadLampCount=0, streetLampCount=0;
        double partialDistance = 0, targetX = 0, targetY = 0;
        boolean firstPoint = true;
        // 다양한 수학적 연산이 가능한 클래스
        Operator calculator = new Operator();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            double x1 = Double.parseDouble(coordinates.get(i).first()), y1 = Double.parseDouble(coordinates.get(i).second());
            double x2 = Double.parseDouble(coordinates.get(i + 1).first()), y2 = Double.parseDouble(coordinates.get(i + 1).second());

            if(startPoint == 0) startPoint = i;

            int temp_road = 0, temp_street = 0;

            double baseDistance = 0.0005; //0.00025;// 약 25m
            partialDistance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2- y1, 2)) + partialDistance;

            /// 경계 내 보안등 좌표
            for (receiveCoordinateLight.Coord road: roadBound) {
                targetX = Double.parseDouble(road.first());
                targetY = Double.parseDouble(road.second());

                double dist = calculator.distance(x1, y1, x2, y2, targetX, targetY);
                if(dist < 0) continue;
                if(dist < 0.00013) // 약 15m
                {
                    double m = (y2 - y1) / (x2 - x1);
                    double randomX = targetX - 1, randomY = -1/m*randomX + targetY + (targetX / m);

                    ArrayList<Double> p1 = new ArrayList<Double>(); p1.add(x1); p1.add(y1);
                    ArrayList<Double> p2 = new ArrayList<Double>(); p2.add(x2); p2.add(y2);
                    ArrayList<Double> L1 = calculator.line(p1, p2);

                    ArrayList<Double> q1 = new ArrayList<Double>(); q1.add(targetX); q1.add(targetY);
                    ArrayList<Double> q2 = new ArrayList<Double>(); q2.add(randomX); q2.add(randomY);
                    ArrayList<Double> L2 = calculator.line(q1, q2);

                    ArrayList<Double> R = calculator.intersection(L1, L2);

                    double R0 = 0;
                    if(R != null) R0= R.get(0);
                    else continue;

                    if((x1 <= R0 && R0 <= x2) || (x2 <= R0 && R0 <= x1)){
                        roadInPath.add(new receiveCoordinateLight.Coord(Double.toString(targetX), Double.toString(targetY)));
                        temp_road += 1;
                    }
                }
            }
            roadLampCount += temp_road;

            /// 경계 내 가로등 좌표
            for (receiveCoordinateLight.Coord street: streetBound) {
                targetX = Double.parseDouble(street.first());
                targetY = Double.parseDouble(street.second());

                double dist = calculator.distance(x1, y1, x2, y2, targetX, targetY);
                if(dist < 0) continue;
                if(dist < 0.00013) // 약 15m
                {
                    double m = (y2 - y1) / (x2 - x1);
                    double randomX = targetX - 1, randomY = -1/m*randomX + targetY + (targetX / m);

                    ArrayList<Double> p1 = new ArrayList<Double>(); p1.add(x1); p1.add(y1);
                    ArrayList<Double> p2 = new ArrayList<Double>(); p2.add(x2); p2.add(y2);
                    ArrayList<Double> L1 = calculator.line(p1, p2);

                    ArrayList<Double> q1 = new ArrayList<Double>(); q1.add(targetX); q1.add(targetY);
                    ArrayList<Double> q2 = new ArrayList<Double>(); q2.add(randomX); q2.add(randomY);
                    ArrayList<Double> L2 = calculator.line(q1, q2);

                    ArrayList<Double> R = calculator.intersection(L1, L2);

                    double R0 = 0;
                    if(R != null) R0= R.get(0);
                    else continue;

                    if((x1 <= R0 && R0 <= x2) || (x2 <= R0 && R0 <= x1)){
                        streetInPath.add(new receiveCoordinateLight.Coord(Double.toString(targetX), Double.toString(targetY)));
                        temp_street += 1;
                    }
                }
            }
            streetLampCount += temp_street;

            //System.out.printf("[%d~%d] road_LampCount : %d , street_LampCount : %d\n",i+1,i+2,temp_road, temp_street);
            if (partialDistance >= baseDistance || i ==  coordinates.size() - 2)// i == len(coordinates[:-2])-1)
            {
                //System.out.printf("[node %d ~ %d] 가로등 갯수 : %d  보안등 갯수 : %d\n", startPoint + 1, i + 2, roadLampCount, streetLampCount);
                int lampSum = streetLampCount + roadLampCount;

                if (lampSum< 2) {
                    isDetour = true;
                    //System.out.println("탐색하면 안되는 길 입니다.");
                    if(firstPoint)
                    {
                        //System.out.println(coordinates.get(startPoint + 1).first() + " " + coordinates.get(startPoint + 1).second());
                        //System.out.println(coordinates.get(i + 2).first() + " " + coordinates.get(i + 2).second());

                        firstDetourStart = new Coord(coordinates.get(startPoint + 1).first(), coordinates.get(startPoint + 1).second());
                        firstDetourEnd = new Coord(coordinates.get(i + 2).first(), coordinates.get(i + 2).second());
                        firstDetourStartIndex = startPoint + 1;
                        firstDetourEndIndex = i + 2;
                        firstPoint = false;
                    }
                }
                else
                    //System.out.println("지나가도 되는 길 입니다.");

                    partialDistance = 0;
                streetLampCount = 0;
                roadLampCount = 0;
                startPoint = 0;
            }
        }

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


    public static class Coord {
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

