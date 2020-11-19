package lamp;

import map.Request;
import operator.*;
import java.util.ArrayList;

public class shortestLamp {
    public static Request.Coord findShortestLamp(Request.Coord fds, ArrayList<Request.Coord> roadInPath,
                                                 ArrayList<Request.Coord> streetInPath, ArrayList<Request.Coord> aroundLamps){
        Request.Coord result = null;
        Calculate calculater = new Calculate();
        double maxCost = 0;
        // 주위 가로등에서 원래 경로상에 가로등, 보안등을 제거
        for (int i = 0; i < aroundLamps.size(); i++) {
            for (Request.Coord temp: roadInPath) {
                if(aroundLamps.contains(temp))
                    aroundLamps.remove(temp);
            }
            for (Request.Coord temp: streetInPath) {
                if(aroundLamps.contains(temp))
                    aroundLamps.remove(temp);
            }
        }
        for (int i = 0; i < aroundLamps.size(); i++) {
            double distance = calculater.pointDistance(fds, aroundLamps.get(i));
            if(maxCost < distance){
                maxCost = distance;
                result = aroundLamps.get(i);
            }
        }
        return result;
    }
}
