package com.example.light_it_up;


import android.widget.Toast;

import java.util.ArrayList;

public class AroundSearchLamp {
    public static ArrayList<receiveCoordinateLight.Coord> aroundLamps(int src_index, int dst_index, ArrayList<receiveCoordinateLight.Coord> coordinates,
                                                       ArrayList<receiveCoordinateLight.Coord> roadBound, ArrayList<receiveCoordinateLight.Coord> roadInPath,
                                                       ArrayList<receiveCoordinateLight.Coord> streetBound, ArrayList<receiveCoordinateLight.Coord> streetInPath) {
        ArrayList<receiveCoordinateLight.Coord> around_lamp = new ArrayList<receiveCoordinateLight.Coord>();
        Operator calculater = new Operator();
        //다음 경로까지의 길이를 반경 값으로 설정.
        double radius = calculater.pointDistance2(coordinates.get(src_index), coordinates.get(dst_index));
        receiveCoordinateLight.Coord coord = coordinates.get(src_index); // #해당 좌표값.

        for(receiveCoordinateLight.Coord lamp : roadBound)
        {
            double dist = calculater.pointDistance(lamp, coord);
            if(dist <= radius && roadInPath.contains(lamp) == false)
                around_lamp.add(lamp);
        }
        for(receiveCoordinateLight.Coord lamp : streetBound)
        {
            double dist = calculater.pointDistance(lamp, coord);
            if(dist <= radius && streetInPath.contains(lamp) == false)
                around_lamp.add(lamp);
        }
        if(around_lamp.isEmpty())
        {
            return null;
        }
        return around_lamp;
    }
}

