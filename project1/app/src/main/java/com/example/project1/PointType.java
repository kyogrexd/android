package com.example.project1;

public class PointType {
    private  Double PositionLat ;
    private  Double PositionLon ;
    private  String GeoHash;
    public Double getPositionLat() {
        return PositionLat;
    }
    public void setPositionLat(Double positionLat) {
        PositionLat = positionLat;
    }
    public Double getPositionLon() {
        return PositionLon;
    }
    public void setPositionLon(Double positionLon) {
        PositionLon = positionLon;
    }

    @Override
    public String toString() {
        return "[" + PositionLat + "," + PositionLon + "]";
    }
}
