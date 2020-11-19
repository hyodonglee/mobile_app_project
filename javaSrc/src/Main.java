import lamp.*;
import map.*;

import java.util.ArrayList;

public class Main {

    // 최종 좌표.
    private static ArrayList<Request.Coord> ansList = new ArrayList<Request.Coord>();

    public static void main(String[] args) throws Exception {
        String startX = "35.897428";
        String startY = "128.600901"; //# 북부 제일교회
        String endX = "35.901257";
        String endY = "128.607252";
        test(startX, startY, endX, endY);
//        Request req = new Request();
//
//        String startX = "35.897428";
//        String startY = "128.600901"; //# 북부 제일교회
//        String endX = "35.901257";
//        String endY = "128.607252";
//
//        // 출발지~목적지 최단경로 좌표 획득 -> req.coordinates
//        req.requestAPI(startX, startY, endX, endY);
//
//        // 목적지 이전까지 탐색하며 우회해야할 경로 확인
//        req.navigate(startX, startY, endX, endY);
//
//        // 처음으로 우회해야할 지점의 시작점과 끝점
//        map.Request.Coord fds = req.firstDetourStart;
//        map.Request.Coord fde = req.firstDetourEnd;
//
//        // 특정 구간 주위의 가로등, 보안등 좌표 획득
//        ArrayList<Request.Coord> aroundLamps = AroundLampSearch.aroundLamps(req.firstDetourStartIndex, req.firstDetourEndIndex, req.coordinates, req.roadBound, req.roadInPath, req.streetBound, req.streetInPath);
//
//        // 가장 가까운 가로등 좌표
//        Request.Coord nextLamp = lamp.shortestLamp.findShortestLamp(fds, aroundLamps);
//        System.out.println();
//
//        if(req.isDetour)
//        {
//            ansList.addAll(req.coordinates.subList(0, req.firstDetourStartIndex));
//            Request reqMidPath = new Request();
//            reqMidPath.requestAPI(fds.first(), fds.second(), nextLamp.first(), nextLamp.second());
//            ansList.addAll(reqMidPath.coordinates);
//        }
//        else{
//            ansList.addAll(req.coordinates);
//        }
//
//
//
//        System.out.println("-----------------------------------------");
//        for(Request.Coord elem : ansList)
//            System.out.println(elem.first() + " " + elem.second());
//        //System.out.println(ansList.toString());
//
//        //System.out.println(req.firstDetourStartIndex + " " + req.firstDetourEndIndex);
//        System.out.println("가장 가까운 경로 가로등 " + nextLamp.first() + " " + nextLamp.second());
        // 해야할 거
        // fds -> 가장 최적의 가로등,보안등 좌표 한개 api 이용
        // 그 최적의 좌표에서 원래 목적지 다시 api 이용
        // 원래 출발지 -> fds, fds -> 최적의 가로등 좌표, 최적의 가로등 좌표 -> 원래 목적지


//        System.out.println("\n우회해야할 경로 구간입니다.");
//        System.out.println(fds.first() + " " + fds.second() + "부터 " + fde.first() + " " + fde.second());
//
//
//        System.out.println("\n\n특정 노드 [1 ~ 7] 주변 가로등, 보안등 좌표");
//        for (int i = 0; i < aroundLamps.size(); i++) {
//            System.out.println(aroundLamps.get(i).first() + " " + aroundLamps.get(i).second());
//        }
    }
    public static void test(String startX, String startY, String endX, String endY) throws Exception {
        Request req = new Request();

        // 출발지~목적지 최단경로 좌표 획득 -> req.coordinates
        req.requestAPI(startX, startY, endX, endY);

        // 목적지 이전까지 탐색하며 우회해야할 경로 확인
        req.navigate(startX, startY, endX, endY);

        // 처음으로 우회해야할 지점의 시작점과 끝점
        map.Request.Coord fds = req.firstDetourStart;
        map.Request.Coord fde = req.firstDetourEnd;

        // 특정 구간 주위의 가로등, 보안등 좌표 획득
        ArrayList<Request.Coord> aroundLamps = AroundLampSearch.aroundLamps(req.firstDetourStartIndex, req.firstDetourEndIndex, req.coordinates, req.roadBound, req.roadInPath, req.streetBound, req.streetInPath);

        // 가장 가까운 가로등 좌표
        Request.Coord nextLamp = lamp.shortestLamp.findShortestLamp(fds, aroundLamps);
        System.out.println();

        if(req.isDetour)
        {
            ansList.addAll(req.coordinates.subList(0, req.firstDetourStartIndex));
            Request reqMidPath = new Request();
            reqMidPath.requestAPI(fds.first(), fds.second(), nextLamp.first(), nextLamp.second());
            ansList.addAll(reqMidPath.coordinates);
            test(nextLamp.first(), nextLamp.second(), endX, endY);
        }
        else{
            ansList.addAll(req.coordinates);
        }

    }
}
