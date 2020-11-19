package com.example.light_it_up;

import java.util.ArrayList;

public class Operator {
    public double distance(Double sx, Double sy, Double ex, Double ey, Double targetX, Double targetY){
        if(ex - sx == 0) return -1;
        double m =(ey-sy)/(ex-sx);
        double c = sy-(m*sx);
        double distance = Math.abs(m * targetX - targetY + c) / (Math.sqrt(Math.pow(m, 2) + 1));
        return distance;
    }

    public static ArrayList<Double> intersection(ArrayList<Double> L1, ArrayList<Double> L2) {
        double D  = L1.get(0) * L2.get(1) - L1.get(1) * L2.get(0);
        double Dx = L1.get(2) * L2.get(1) - L1.get(1) * L2.get(2);
        double Dy = L1.get(0) * L2.get(2) - L1.get(2) * L2.get(0);
        if(D != 0){
            double x = Dx / D, y = Dy / D;
            ArrayList<Double> ret = new ArrayList<>();
            ret.add(x); ret.add(y);
            return ret;
        }
        return null;
    }

    public static ArrayList<Double> line(ArrayList<Double> p1, ArrayList<Double> p2){
        double A = (p1.get(1) - p2.get(1));
        double B = (p2.get(0) - p1.get(0));
        double C = (p1.get(0)*p2.get(1) - p2.get(0)*p1.get(1));
        ArrayList<Double> res = new ArrayList<>();
        res.add(A); res.add(B); res.add(-C);
        return res;
    }

    public static double pointDistance(receiveCoordinateLight.Coord x, receiveCoordinateLight.Coord y) {
        return Math.sqrt(Math.pow(Double.parseDouble(x.first()) - Double.parseDouble(y.first()), 2) +
                Math.pow(Double.parseDouble(x.second()) - Double.parseDouble(y.second()), 2));
    }
    //public
}

