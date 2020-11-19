package lamp;

import operator.Calculate;
import map.Request;

import java.util.ArrayList;

public class AroundLampSearch {
    public static ArrayList<Request.Coord> aroundLamps(int src_index, int dst_index, ArrayList<Request.Coord> coordinates,
                                                       ArrayList<Request.Coord> roadBound, ArrayList<Request.Coord> roadInPath,
                                                       ArrayList<Request.Coord> streetBound, ArrayList<Request.Coord> streetInPath) {
        ArrayList<Request.Coord> around_lamp = new ArrayList<Request.Coord>();
        Calculate calculater = new Calculate();
        //다음 경로까지의 길이를 반경 값으로 설정.
        double radius = calculater.pointDistance(coordinates.get(src_index), coordinates.get(dst_index));
        Request.Coord coord = coordinates.get(src_index); // #해당 좌표값.

        for(Request.Coord lamp : roadBound)
        {
            double dist = calculater.pointDistance(lamp, coord);
            if(dist <= radius && roadInPath.contains(lamp) == false)
                around_lamp.add(lamp);
        }
        for(Request.Coord lamp : streetBound)
        {
            double dist = calculater.pointDistance(lamp, coord);
            if(dist <= radius && streetInPath.contains(lamp) == false)
                around_lamp.add(lamp);
        }
        if(around_lamp.isEmpty())
        {
            System.out.println("우회경로 없음");
            return null;
        }
        return around_lamp;
    }
}
