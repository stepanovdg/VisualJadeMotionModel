package by.bsu.kurs.stepanov.types;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 26.02.13
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class Coordinates {

    private Double latitude;   //Широта
    private Double longitude;  //Долгота

    public Double getLatitude() {
        return latitude;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinates(String command) {
        String[] arg = command.split(Constants.COORDINATE_SPLITTER);
        this.latitude = Double.valueOf(arg[0]);
        this.longitude = Double.valueOf(arg[1]);
    }

    public static Double countLength(Coordinates a,Coordinates b) {
        double dy = a.getLatitude()-b.getLatitude();
        double dx = a.getLongitude()-b.getLongitude();
        if (dx>180){
            dx = 360 - dx;
        }
        if(dx<-180){
            dx = 360 + dx;
        }
        return  Math.sqrt(dx*dx+dy*dy);
    }
}
