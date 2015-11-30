package ch.ethz.inf.vs.a4.funwithflags;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pascalwiesmann on 30.11.15.
 */

    /*
    SERVER COMMUNICATION (trying not to mix stuff to much)

    we can always put the server functionality into the other functions later on, but I think it
    makes sense to separate it.
    */


public class Server {


    //FLAGS-----------------------------------------------------------//

    //just downloads the flags and stores them in allFlags
    public static void getFlagsFromServer(final android.content.Context  context){

        ParseQuery<ParseObject> flagQuery=new ParseQuery<ParseObject>("Flag");
        flagQuery.findInBackground(new FindCallback<ParseObject>() {
            ArrayList<Flag> ret = new ArrayList<Flag>();
            @Override
            public void done(List<ParseObject> flags, com.parse.ParseException e) {
                if (e == null) {

                    String ID;
                    String userName;
                    String text;
                    LatLng latLng;
                    Category category;
                    Date date;
                    ParseGeoPoint geoPoint;

                    for (int i = 0; i < flags.size(); i++) {
                            /*
                            from the report:
                            Flags(flagId:Int, userName:String, content:String, latitude:Int,
                            longitude:Int, categoryName:String, date:Date)
                            */
                        ID = (String) flags.get(i).getObjectId();
                        userName = (String) flags.get(i).get("userName");
                        text = (String) flags.get(i).get("content");
                        geoPoint = (ParseGeoPoint) flags.get(i).get("geoPoint");
                        latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        category = Category.getByName((String) flags.get(i).get("categoryName"));
                        date = (Date) flags.get(i).get("date");

                        ret.add(new Flag(ID, userName, text, latLng, category, date, context));
                    }

                }
                Data.setAllFlags(ret);

            }

        });

    }
    public static void deleteFlagFromServer(Flag f){
        //don't forget to remove all the local references to this flag, by e.g. calling deleteFlag
        ParseQuery<ParseObject> flagQuery=new ParseQuery<ParseObject>("Flag");
        flagQuery.getInBackground(f.getID(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, com.parse.ParseException e) {
                try {
                    object.delete();
                } catch (com.parse.ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });


    }

    //FAVOURITES-----------------------------------------------------------//

    public static void getFavouriteFlagsFromServer(){
        //TODO
    }
    public static void submitFavouriteToServer(){
        //TODO
    }
    public static void deleteFavouriteFromServer(){
        //TODO
    }

    //RATING----------------------------------------------------------------//

    public static void getTopRankedFlagsFromServer(){
        //TODO
    }
    public static void submitRatingToServer(){
        //TODO
    }
    /*
    maybe not necessary since we can change a rating by just submitting another one
    (e.g at first I like a flag, but then I realize that it sucks, so I can just rate again)

    public static void deleteRatingFromServer(Flag f){

    }
    */


}
