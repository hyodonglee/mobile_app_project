import lamp.AroundLampSearch;
import map.Request;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {

        Request req = new Request();

        String startX = "35.897428";
        String startY = "128.600901"; //# 북부 제일교회
        String endX = "35.901257";
        String endY = "128.607252";

        // 출발지~목적지 최단경로 좌표 획득 -> req.coordinates
        req.requestAPI(startX, startY, endX, endY);

        // 목적지 이전까지 탐색하며 우회해야할 경로 확인
        req.navigate(startX, startY, endX, endY);

        // 특정 구간 주위의 가로등, 보안등 좌표 획득
        ArrayList<Request.Coord> aroundLamps = AroundLampSearch.aroundLamps(1, 7, req.coordinates, req.roadBound, req.roadInPath, req.streetBound, req.streetInPath);

        // 처음으로 우회해야할 지점의 시작점과 끝점
        map.Request.Coord fds = req.firstDetourStart;
        map.Request.Coord fde = req.firstDetourEnd;

        System.out.println("\n우회해야할 경로 구간입니다.");
        System.out.println(fds.first() + " " + fds.second() + "부터 " + fde.first() + " " + fde.second());

//        System.out.println("\n\n특정 노드 [1 ~ 7] 주변 가로등, 보안등 좌표");
//        for (int i = 0; i < aroundLamps.size(); i++) {
//            System.out.println(aroundLamps.get(i).first() + " " + aroundLamps.get(i).second());
//        }
    }
}

