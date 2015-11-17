package ch.ethz.inf.vs.a4.funwithflags;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andres on 13.11.15.
 */
public class Flag {

    private LatLng latLng;
    private String text;

    private long ID;

    private Category category;

    private GPSTracker gpsTracker;

    public static String NOT_IN_RANGE_MESSAGE;

    public Flag(LatLng latLng, Category category, String text, Context context){
        this.latLng = latLng;
        this.category = category;
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

    public boolean isOwner = false;

    public LatLng getLatLng() {
        return latLng;
    }

    public Category getCategory() {
        return category;
    }

    private boolean isInRange(){
        LatLng phoneLatLong = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());

        // check if flag is a favourite -> always visible
        if(Data.containsFavourite(this))
            return true;

        // this is an approximation of the distance that works best if close by, but since we want to only see close by flags, this should work just fine
        int R = 6371; // km
        double x = (getLatLng().longitude - phoneLatLong.longitude) * Math.cos((phoneLatLong.latitude + getLatLng().latitude) / 2);
        double y = (getLatLng().latitude - phoneLatLong.latitude);
        double distance = Math.sqrt(x * x + y * y) * R;

        if (distance <= MapsActivity.MAX_FLAG_VISIBILITY_RANGE)
            return true;
        else
            return false;
    }
}
