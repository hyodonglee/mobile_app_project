package map;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import lamp.*;
import operator.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Request {
    public static class Coord{
        String x;
        String y;

        public Coord(String _x, String _y){
            this.x = _x;
            this.y = _y;
        }

        public String first(){
            return this.x;
        }
        public String second(){
            return this.y;
        }
    }
    // for saving coordinates on the way to my destination.
    public static ArrayList<Request.Coord> coordinates = new ArrayList<Request.Coord>();

    public static ArrayList<Request.Coord> roadInPath = new ArrayList<>();
    public static ArrayList<Request.Coord> streetInPath = new ArrayList<>();


    public static ArrayList<ArrayList<Request.Coord>> boundLamp;
    public static ArrayList<Request.Coord> roadBound;
    public static ArrayList<Request.Coord> streetBound;

    public static Request.Coord firstDetourStart;
    public static Request.Coord firstDetourEnd;
    public static int firstDetourStartIndex;
    public static int firstDetourEndIndex;
    public static boolean isDetour = false;

    public static void requestAPI(String startX, String startY, String endX, String endY) throws Exception {
        // sample coordinates from start to end
//        startX = "35.897428";
//        startY = "128.600901"; //# 북부 제일교회
//        endX = "35.901257";
//        endY = "128.607252";// # 코스모스 모텔
        String url = "https://api2.sktelecom.com/tmap/routes/pedestrian";

        HttpsURLConnection httpClient = (HttpsURLConnection) new URL(url).openConnection();

        //add reuqest header
        httpClient.setRequestMethod("POST");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpClient.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "version=1&format=json&appKey=l7xx0dffe4a89bff4b39b4cf9a19a0d5292a" +
                "&startX=" + startY + "&startY=" + startX +
                "&angle=172" + "&endX=" + endY + "&endY=" + endX +
                "&reqCoordType=WGS84GEO&startName=출발지&endName=도착지" +
                "&searchOption=0&resCoordType=WGS84GEO";

        // Send post request.request
        httpClient.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(httpClient.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

        int responseCode = httpClient.getResponseCode();
        System.out.println("\nSending 'POST' request.request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(httpClient.getInputStream()))) {

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            //test response
            //System.out.println(response.toString());

            try{
                JSONParser jsonParse = new JSONParser();
                JSONObject jsonObj = (JSONObject)jsonParse.parse(String.valueOf(response));
                JSONArray featuresArray = (JSONArray) jsonObj.get("features");
                for (int i = 0; i < featuresArray.size(); i++) {
                    JSONObject feature = (JSONObject) featuresArray.get(i);
                    JSONObject geometry = (JSONObject) feature.get("geometry");

                    if(geometry.get("type").toString().equals("LineString"))
                    {
                        JSONArray coords = (JSONArray)geometry.get("coordinates");
                        for(int j=0; j<coords.size(); ++j)
                        {
                            JSONArray vertex = (JSONArray)coords.get(j);
                            Coord coord = new Coord(vertex.get(1).toString(), vertex.get(0).toString());
                            coordinates.add(coord);
                        }
                    }
                }

                // Print the saved coordinates in List
//                for(Coord coord: coordinates){
//                    System.out.println(coord.first() + " " + coord.second());
//                }

            } catch (ParseException e){
                e.printStackTrace();
            }
        }

        //////////////////////// 좌표값을 가지고 있는 변수 : coordinates
        LampExtraction lamp = new LampExtraction();
        double sx = Double.parseDouble(startX), sy = Double.parseDouble(startY);
        double ex = Double.parseDouble(endX), ey = Double.parseDouble(endY);
        boundLamp = lamp.getBoundCoord(sx, sy, ex, ey); // 경계 내부에 있는 (가로등, 보안등) 좌표 얻어옴.
        roadBound = boundLamp.get(0); // 경계 내부에 있는 가로등 좌표
        streetBound = boundLamp.get(1); // 경계 내부에 있는 보안등 좌표
    }

    public static void navigate(String startX, String startY, String endX, String endY) throws IOException {
        // 경로 탐색 가능 판단용 부분 변수
        int startPoint=0, roadLampCount=0, streetLampCount=0;
        double partialDistance = 0, targetX = 0, targetY = 0;
        boolean firstPoint = true;
        // 다양한 수학적 연산이 가능한 클래스
        Calculate calculator = new Calculate();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            double x1 = Double.parseDouble(coordinates.get(i).first()), y1 = Double.parseDouble(coordinates.get(i).second());
            double x2 = Double.parseDouble(coordinates.get(i + 1).first()), y2 = Double.parseDouble(coordinates.get(i + 1).second());

            if(startPoint == 0) startPoint = i;

            int temp_road = 0, temp_street = 0;

            double baseDistance = 0.0005; //0.00025;// 약 25m
            partialDistance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2- y1, 2)) + partialDistance;

            /// 경계 내 보안등 좌표
            for (Request.Coord road: roadBound) {
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
                        roadInPath.add(new Request.Coord(Double.toString(targetX), Double.toString(targetY)));
                        temp_road += 1;
                    }
                }
            }
            roadLampCount += temp_road;

            /// 경계 내 가로등 좌표
            for (Request.Coord street: streetBound) {
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
                        streetInPath.add(new Request.Coord(Double.toString(targetX), Double.toString(targetY)));
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
}