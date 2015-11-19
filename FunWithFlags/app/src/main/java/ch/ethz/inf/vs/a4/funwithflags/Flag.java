package ch.ethz.inf.vs.a4.funwithflags;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

import java.sql.Timestamp;

/**
 * Created by Andres on 13.11.15.
 */

public class Flag {

    /*
    from the report:
    Flags(flagId:Int, userName:String, content:String, latitude:Int,
    longitude:Int, categoryName:String, date:Date)
     */

    private String ID;
    private String userName;
    private String text;
    private LatLng latLng;
    private Category category;
    private Timestamp date;
    private float alpha;

    private GPSTracker gpsTracker;

    public static String NOT_IN_RANGE_MESSAGE;

    public Flag(String ID,String userName, String text, LatLng latLng, Category category, Timestamp date, Context context){
        this.latLng = latLng;
        this.category = category;
        this.userName = userName;
        this.text = text;
        gpsTracker = new GPSTracker(context);
        if(NOT_IN_RANGE_MESSAGE == null) {
            NOT_IN_RANGE_MESSAGE = context.getString(R.string.not_in_range_message);
        }
    }

    public void setLatLng(LatLng latLng){
        this.latLng = latLng;
    }
    public void setCategory(Category category){
        this.category = category;
    }
    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        if (isInRange())
            return text;
        else {
            return NOT_IN_RANGE_MESSAGE;
        }
    }

    public float getAlpha () {
        if (isInRange())
            return 1.0f;
        else
            return 0.5f;

    }

    public boolean isOwner = false;

    public LatLng getLatLng() {
        return latLng;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isInRange(){

        if(Data.containsFavourite(this))
            return true;

        ParseGeoPoint phoneGeoPoint = new ParseGeoPoint(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        ParseGeoPoint flagGeoPoint = new ParseGeoPoint(getLatLng().latitude, getLatLng().longitude);

        if (phoneGeoPoint.distanceInKilometersTo(flagGeoPoint) < MapsActivity.MAX_FLAG_VISIBILITY_RANGE)
            return true;
        else
            return false;

        /*
        LatLng phoneLatLong = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
        // check if flag is a favourite -> always visible


        // this is an approximation of the distance that works best if close by, but since we want to only see close by flags, this should work just fine
        int R = 6371; // km
        double x = (getLatLng().longitude - phoneLatLong.longitude) * Math.cos((phoneLatLong.latitude + getLatLng().latitude) / 2);
        double y = (getLatLng().latitude - phoneLatLong.latitude);
        double distance = Math.sqrt(x * x + y * y) * R;

        if (distance <= MapsActivity.MAX_FLAG_VISIBILITY_RANGE)
            return true;
        else
            return false;

            */
    }

    public String getUserName() {
        return userName;
    }

    public void setID(String ID){
        this.ID=ID;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getID() {
        return ID;
    }

}
