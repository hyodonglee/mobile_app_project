package lamp;

import map.Request;
import operator.*;
import java.util.ArrayList;

public class shortestLamp {
    public static Request.Coord findShortestLamp(Request.Coord fds, ArrayList<Request.Coord> aroundLamps){
        Request.Coord result = null;
        Calculate calculater = new Calculate();
        double minCost = 1000;
        for (int i = 0; i < aroundLamps.size(); i++) {
            double distance = calculater.pointDistance(fds, aroundLamps.get(i));
            if(minCost > distance){
                minCost = distance;
                result = aroundLamps.get(i);
            }
        }
        return result;
    }
}
