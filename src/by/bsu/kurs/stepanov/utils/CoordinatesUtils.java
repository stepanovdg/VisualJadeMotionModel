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
    private static Double xMax;
    private static Double yMax;
    private static Double latitudeMax;
    private static Double latitudeMin;
    private static Double longitudeMax;
    private static Double longitudeMin;

    private static Double mapCenterLongitude;
    private static Double mapCenterLatitude;
    private static Integer zoomLevel;

    private static Double[] zoomLevels = new Double[]{156367.87, 78183.93, 39091.97, 19545.98,
            9772.99, 4886.50, 2443.25, 1221.62, 610.81, 305.41, 152.70, 76.35, 38.18, 19.09, 9.54, 4.77,
            2.39, 1.19, 0.60};

    public CoordinatesUtils() {
    }

    private static CoordinatesUtils getInstance() {
        if (coordinatesUtils == null) {
            coordinatesUtils = new CoordinatesUtils();
        }
        return coordinatesUtils;
    }

    public static void reset() {
        coordinatesUtils = null;
    }

    public static void setResolution(Double x, Double y) {
//        System.out.println("x=" + x + " y=" + y);
        xMax = x;
        yMax = y;
    }

    public static void setMapCenterZoom(Double lon, Double lat, Integer zoom) {
//        System.out.println("SetMapCenter called");
        mapCenterLatitude = lat;
        mapCenterLongitude = lon;
        zoomLevel = zoom;
        /*setMapBorders(lat + yMax / 2 * zoomLevels[zoom], lat - yMax / 2 * zoomLevels[zoom],
                lon + xMax / 2 * zoomLevels[zoom], lon - xMax / 2 * zoomLevels[zoom]);*/
    }

    public static void setMapBorders(Double latMax, Double latMin, Double longMax, Double longMin) {
//        System.out.println("SetMapBorders called");
        latitudeMax = latMax;
        latitudeMin = latMin;
        longitudeMax = longMax;
        longitudeMin = longMin;
    }

    public static double yFromWorld(Double latitude) {
        Double dif = difLatitude(latitudeMax, latitudeMin);
        Double zoom = dif / yMax;
        dif = difLatitude(latitude, latitudeMin);
//        System.out.println(dif / (zoom) + "=y, zoom y =" + zoom);
        return yMax - (dif / (zoom));
    }

    public static double xFromWorld(Double longtitude) {
        Double dif = difLongtitude(longitudeMax, longitudeMin);
        Double zoom = dif / xMax;
        dif = difLongtitude(longtitude, longitudeMin);
//        System.out.println(dif / (zoom) + "=x, zoom x =" + zoom);
        return dif / (zoom);
    }

    public static double xFromWorld(Coordinates from) {
        return xFromWorld(from.getLongitude());
    }

    public static double yFromWorld(Coordinates from) {
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
}
