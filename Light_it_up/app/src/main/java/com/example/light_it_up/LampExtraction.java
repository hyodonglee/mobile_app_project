package com.example.light_it_up;


import com.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class LampExtraction {

    public static double latUpperBound;
    public static double latLowerBound;
    public static double lonLeftBound;
    public static double lonRightBound;
    public static ArrayList<receiveCoordinateLight.Coord> road_lamp = new ArrayList<receiveCoordinateLight.Coord>();
    public static ArrayList<receiveCoordinateLight.Coord> street_lamp = new ArrayList<receiveCoordinateLight.Coord>();

    public static ArrayList<ArrayList<receiveCoordinateLight.Coord>> getBoundCoord(double startX, double startY, double endX, double endY) throws IOException {
        double x_margin = 0.0016; // 경도 가장자리 여분
        double y_margin = 0.007; // 위도 가장자리 여분
        double lat_low = 0, lat_high = 0, lon_left = 0, lon_right = 0;
        if (startY >= endY) //위도
        {
            lon_left = endY;
            lon_right = startY;
        }
        else
        {
            lon_left = startY;
            lon_right = endY;
        }
        if (startX >= endX) // # 경도
        {
            lat_low = endX;
            lat_high = startX;
        }
        else {
            lat_low = startX;
            lat_high = endX;
        }

        latUpperBound = lat_high + x_margin; // # 위도 최대값
        latLowerBound = lat_low - x_margin; //  # 위도 최소값
        lonLeftBound = lon_left - y_margin; //  # 경도 최소값
        lonRightBound = lon_right + y_margin; //  # 경도 최대값

        ArrayList<receiveCoordinateLight.Coord> allRoadLamp = new ArrayList<receiveCoordinateLight.Coord>();
        ArrayList<receiveCoordinateLight.Coord> allStreetLamp = new ArrayList<receiveCoordinateLight.Coord>();

        readDataFromCsv2("/res/raw/bukgu_security.csv", 1);
        readDataFromCsv("/res/raw/bukgu_security.csv", 2);
        //readDataFromCsv("C:\\Users\\HyunSU\\Desktop\\2020\\2020 하반기\\코드페어\\src\\lamp\\information\대구광역시_서구_보안등정보_20190611.csv", 2);
        readDataFromCsv("/res/raw/namgu_security.csv", 2);
        readDataFromCsv("/res/raw/bukgu_security.csv", 2);
        readDataFromCsv("/res/raw/dalseo_security.csv", 2);
        //readDataFromCsv("C:\\Users\\HyunSU\\Desktop\\2020\\2020 하반기\\코드페어\\src\\lamp\\information\대구광역시_달성군_보안등정보_20190625.csv", 2);
        //readDataFromCsv("C:\\Users\\HyunSU\\Desktop\\2020\\2020 하반기\\코드페어\\src\\lamp\\information\대구광역시_수성구_보안등정보_20190924.csv", 2);
        readDataFromCsv("/res/raw/joongu_security.csv", 2);
//        for (int i = 0; i < road_lamp.size(); i++) {
//            System.out.println(road_lamp.get(i).first() + " " + road_lamp.get(i).second());
//        }
        for (int i = 0; i < road_lamp.size(); i++) {
            String lat = road_lamp.get(i).first();
            String lon = road_lamp.get(i).second();
            if(check_boundary(lat, lon))
                allRoadLamp.add(new receiveCoordinateLight.Coord(lat, lon));
        }
        for (int i = 0; i < street_lamp.size(); i++) {
            String lat = street_lamp.get(i).first();
            String lon = street_lamp.get(i).second();
            if(check_boundary(lat, lon))
                allStreetLamp.add(new receiveCoordinateLight.Coord(lat, lon));
        }
        //System.out.println("DD");
        ArrayList<ArrayList<receiveCoordinateLight.Coord>> result = new ArrayList<ArrayList<receiveCoordinateLight.Coord>>();
        result.add(allRoadLamp);
        result.add(allStreetLamp);
        return result;
    }
    public static boolean check_boundary(String lamp_lat, String lamp_lon){

        double latitude, longitude;
        for (int i = 0; i < lamp_lat.length(); i++) {
            if(lamp_lat.charAt(i) != '.' && Character.isDigit(lamp_lat.charAt(i)) == false) return false;
        }
        for (int i = 0; i < lamp_lon.length(); i++) {
            if(lamp_lon.charAt(i) != '.' && Character.isDigit(lamp_lon.charAt(i)) == false) return false;
        }
        latitude = Double.parseDouble(lamp_lat);
        longitude = Double.parseDouble(lamp_lon);
        if ((latitude >= latLowerBound && latitude <= latUpperBound) && (longitude >= lonLeftBound && longitude <= lonRightBound))
            return true;
        else return false;
    }

    public static void readDataFromCsv(String filePath,int mark) throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"), ',', '"', 1); // 1
        String [] nextLine;
        String line=null;
        while ((nextLine = reader.readNext()) != null) {   // 2
            String[] token = line.split(",");
            if(mark == 1) road_lamp.add(new receiveCoordinateLight.Coord(token[2], token[3]));
            else street_lamp.add(new receiveCoordinateLight.Coord(token[4], token[5]));

            for (int i = 0; i < nextLine.length; i++) {
                System.out.println(i + " " + nextLine[i]);
            }
            System.out.println();
        }

    }

    public static void readDataFromCsv2(String filePath, int mark) throws IOException {
        String line = null;
        File locationFile = new File(filePath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(locationFile));
            Charset.forName("UTF-8");

            while((line = br.readLine()) != null) {
                String[] token = line.split(",");
                if(mark == 1) road_lamp.add(new receiveCoordinateLight.Coord(token[2], token[3]));
                else street_lamp.add(new receiveCoordinateLight.Coord(token[4], token[5]));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }



}
