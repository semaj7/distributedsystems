package ch.ethz.inf.vs.a4.funwithflags;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.Date;

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
    private Date date;
    private float alpha;
    private int upVotes;
    private int downVotes;  // temporarily added both, up and downvotes, so that we can delete flags that have a particular up/down vote RATIO, instead of just an absolute number. we can easily change this though :)

  //  private static GPSTracker gpsTracker; //this is static so that all flags can use the same gpsTracker (otherwise overkill)

    public static String NOT_IN_RANGE_MESSAGE;
    private static final int MINIMAL_UP_TO_DOWN_VOTE_RATIO = -5; // todo: maybe find a better value for this

    public Flag(String ID,String userName, String text, LatLng latLng, Category category, Date date, Context context){

        this.ID = ID;
        this.upVotes = 0;
        this.downVotes = 0;
        this.date = date;
        this.latLng = latLng;
        this.category = category;
        this.userName = userName;
        this.text = text;
     //   gpsTracker = new GPSTracker(context, null);
        if(NOT_IN_RANGE_MESSAGE == null) {
            NOT_IN_RANGE_MESSAGE = context.getString(R.string.not_in_range_message);
        }
    }

    public void upVote(){
        if(Data.downvotedFlags.contains(this))
            downVotes --;
        if(!Data.upvotedFlags.contains(this))
            upVotes++;
        Data.putUpvoted(this);
        Data.checkIfTopAndAdd(this);
    }

    // this function returns true if that downvote lead a the point where the ratio got too bad, and the flag should get deleted, this should be checked everytime the method is used
    public boolean downVote(){
        if(Data.upvotedFlags.contains(this))
            upVotes--;
        if(!Data.downvotedFlags.contains(this))
            downVotes++;
        if (getVoteRateAbsolut() <= MINIMAL_UP_TO_DOWN_VOTE_RATIO)
            return true;
        Data.putDownvoted(this);
        return false;
    }

    public int getVoteRateAbsolut(){ return (upVotes - downVotes); }

    //public float getVoteRate(){ return (downVotes != 0 ) ? (upVotes / downVotes) : upVotes; }

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
        if (isVisible())
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

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Flag)
        {
            Flag f = (Flag) o;
            //if both IDs are not null, check if their IDs are equal
            if (f.getID() != null && this.getID() != null) {
                return f.getID().equals(this.getID());
            }
            else {
                if (f.getID() == null && this.getID() == null) {
                    //check if they are at exactly the same location and time
                    return f.getLatLng().equals(this.getLatLng()) && f.getDate().equals(this.getDate());
                }
                //otherwise its false

            }
        }
        return false;
    }

    public boolean isVisible(){
        if(Data.user != null)
            if(Data.user.getUsername().equals(this.getUserName()))
                return true;
        if(Data.containsFlag(this, Data.favouriteFlags) )
        /*| Data.containsFlag(this, Data.topRankedFlags)*/
            return true;
        return isInRange();
    }

    public boolean isInRange(Location lastLocation) {

        ParseGeoPoint phoneGeoPoint = new ParseGeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
        ParseGeoPoint flagGeoPoint = new ParseGeoPoint(getLatLng().latitude, getLatLng().longitude);

        if (phoneGeoPoint.distanceInKilometersTo(flagGeoPoint) < MapsActivity.MAX_FLAG_VISIBILITY_RANGE)
            return true;
        else
            return false;

    }

    public boolean isInRange(){

        if(Data.containsFlag(this, Data.favouriteFlags) /*| Data.containsFlag(this, Data.topRankedFlags)*/)
            return true;

        if (Data.lastLocation == null) return false;

        return isInRange(Data.lastLocation);

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

    public String getUserName() {return userName;}

    public Date getDate() {return date;}

    //may return null, if it was not yet on the server
    public String getID() {
        return ID;
    }

}
