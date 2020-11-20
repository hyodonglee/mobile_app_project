package com.example.light_it_up;


import java.util.ArrayList;

public class shortestLamp {
    public static receiveCoordinateLight.Coord findShortestLamp(receiveCoordinateLight.Coord fds, ArrayList<receiveCoordinateLight.Coord> roadInPath,
                                                 ArrayList<receiveCoordinateLight.Coord> streetInPath, ArrayList<receiveCoordinateLight.Coord> aroundLamps){
        receiveCoordinateLight.Coord result = null;
        Operator calculater = new Operator();
        double maxCost = 0;
        // 주위 가로등에서 원래 경로상에 가로등, 보안등을 제거
        for (int i = 0; i < aroundLamps.size(); i++) {
            for (receiveCoordinateLight.Coord temp: roadInPath) {
                if(aroundLamps.contains(temp))
                    aroundLamps.remove(temp);
            }
            for (receiveCoordinateLight.Coord temp: streetInPath) {
                if(aroundLamps.contains(temp))
                    aroundLamps.remove(temp);
            }
        }
        for (int i = 0; i < aroundLamps.size(); i++) {
            double distance = calculater.pointDistance2(fds, aroundLamps.get(i));
            if(maxCost < distance){
                maxCost = distance;
                result = aroundLamps.get(i);
            }
        }
        return result;
    }
}
