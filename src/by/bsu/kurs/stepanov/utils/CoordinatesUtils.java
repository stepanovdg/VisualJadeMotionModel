package by.bsu.kurs.stepanov.utils;

import by.bsu.kurs.stepanov.types.Coordinates;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 23.02.14
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class CoordinatesUtils {
    private static CoordinatesUtils coordinatesUtils;
    private Double xMax;
    private Double yMax;
    private Double latitudeMax;
    private Double latitudeMin;
    private Double longitudeMax;
    private Double longitudeMin;

    private Double mapCenterLongitude;
    private Double mapCenterLatitude;
    private Integer zoomLevel;

    private static Double[] zoomLevels = new Double[]{156367.87, 78183.93, 39091.97, 19545.98,
            9772.99, 4886.50, 2443.25, 1221.62, 610.81, 305.41, 152.70, 76.35, 38.18, 19.09, 9.54, 4.77,
            2.39, 1.19, 0.60};

    public CoordinatesUtils() {
    }

    public static CoordinatesUtils getInstance() {
        if (coordinatesUtils == null) {
            coordinatesUtils = new CoordinatesUtils();
        }
        return coordinatesUtils;
    }

    public static void reset() {
        coordinatesUtils = null;
    }

    public void setResolution(Double x, Double y) {
        xMax = x;
        yMax = y;
    }

    public void setMapCenterZoom(Double lon, Double lat, Integer zoom) {
        mapCenterLatitude = lat;
        mapCenterLongitude = lon;
        zoomLevel = zoom;
    }

    public void setMapBorders(Double latMax, Double latMin, Double longMax, Double longMin) {
        latitudeMax = latMax;
        latitudeMin = latMin;
        longitudeMax = longMax;
        longitudeMin = longMin;
    }

    public double yFromWorld(Double latitude) {
        Double dif = difLatitude(latitudeMax, latitudeMin);
        Double zoom = dif / yMax;
        dif = difLatitude(latitude, latitudeMin);
        return yMax - (dif / (zoom));
    }

    public double xFromWorld(Double longtitude) {
        Double dif = difLongtitude(longitudeMax, longitudeMin);
        Double zoom = dif / xMax;
        dif = difLongtitude(longtitude, longitudeMin);
        return dif / (zoom);
    }

    public double xFromWorld(Coordinates from) {
        return xFromWorld(from.getLongitude());
    }

    public double yFromWorld(Coordinates from) {
        return yFromWorld(from.getLatitude());
    }

    private static double difLongtitude(Double max, Double min) {
        Double dif = max - min;
        if (dif < 0) {
            dif = 360 + dif;
        }
        return dif;
    }

    private static double difLatitude(Double max, Double min) {
        Double dif = max - min;
        return dif;
    }

    public void setMapCenterZoom(String lonC, String latC, String zoom) {
        setMapCenterZoom(Double.valueOf(lonC), Double.valueOf(latC), Integer.valueOf(zoom));
    }
}
