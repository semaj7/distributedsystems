package ch.ethz.inf.vs.a4.funwithflags;

/**
 * Created by Andres on 13.11.15.
 */
public class Flag {

    private double longitude;
    private double latitude;

    private String text;

    public Flag(double lon, double lat, String t){
        longitude = lon;
        latitude = lat;
        text = t;

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getText() {
        return text;
    }
}
