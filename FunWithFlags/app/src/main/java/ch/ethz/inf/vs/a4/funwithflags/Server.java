package ch.ethz.inf.vs.a4.funwithflags;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

    /*
    Users(email:String, passwordHash:String, salt:String, registerDate:Date)
    Flags(flagId:Int, userName:String, content:String, latitude:Int, longitude:Int, categoryName:String, date:Date)
    Ratings(userName:String, lfagId:Int, rating:Boolean)
    Categories(categoryName:String)
    Favorites(userName:String, flagId:Int)
    Following(followedUser:String, followingUser:String)


    */


public class Server {


    //FLAGS-----------------------------------------------------------//

    //just downloads the flags and stores them in allFlags
    public static void getFlagsFromServer(final android.content.Context  context){

        //TODO: get ALL flags! query is default limited to 100 objects.
        //set the limit to 1000 and with skip download all
        //for example do a while loop and always skip a 1000

        ParseQuery<ParseObject> flagQuery=new ParseQuery<ParseObject>("Flag");
        flagQuery.setLimit(1000);
        flagQuery.setSkip(0);
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

    public static void submitFlag(Flag f){

        /*
        From the Report:

        Flags(flagId:Int, userName:String, content:String, latitude:Int,
                longitude:Int, categoryName:String, date:Date)
        */

        final ParseObject parseFlag = new ParseObject("Flag");


        //parseFlag.put("flagId",f. TODO);
        parseFlag.put("userName",f.getUserName());
        parseFlag.put("content",f.getText());
        parseFlag.put("geoPoint",new ParseGeoPoint(f.getLatLng().latitude, f.getLatLng().longitude));
        parseFlag.put("categoryName",f.getCategory().name);
        parseFlag.put("date", f.getDate());
        parseFlag.saveInBackground();

        //TODO: when this saveInBackground completed, execute:
        //getFlags();

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

    /*
    todo: set this stuff (data.java)
    public final static Flag[] favouriteFlags = new Flag[MapsActivity.MAX_NUMBER_OF_FAVOURITES];
    public final static Flag[] topRankedFlags = new Flag[MapsActivity.TOP_RANKED_FLAGS_AMOUNT];
     */

    //FAVOURITES-----------------------------------------------------------//

    public static void getFavouriteFlagsFromServer(final android.content.Context  context){
        //TODO
        ParseQuery<ParseObject> favouritesQuery = new ParseQuery<ParseObject>("Favourite");
        favouritesQuery.whereMatches("userName",getCurrentLoggedInUserName());
        favouritesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Data.favouriteFlags=new Flag[MapsActivity.MAX_NUMBER_OF_FAVOURITES];
                for (int i = 0; i < objects.size(); i++) {
                    ParseQuery<ParseObject> flagQuery=new ParseQuery<ParseObject>("Flag");
                    Data.favouriteFlagsList.clear();
                    flagQuery.getInBackground((String) objects.get(i).get("FlagId"), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, com.parse.ParseException e) {


                                String ID = (String) object.getObjectId();
                                String userName = (String) object.get("userName");
                                String text = (String) object.get("content");
                                ParseGeoPoint geoPoint = (ParseGeoPoint) object.get("geoPoint");
                                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                Category category = Category.getByName((String) object.get("categoryName"));
                                Date date = (Date) object.get("date");

                                Data.favouriteFlagsList.add(new Flag(ID, userName, text, latLng, category, date, context));

                        }
                    });
                }
            }
        });
    }
    public static void submitFavouriteToServer(Flag f){
        final ParseObject favourite = new ParseObject("Favourite");
        favourite.put("userName", getCurrentLoggedInUserName());
        favourite.put("flagId",f.getID());
        favourite.saveInBackground();
    }
    public static void deleteFavouriteFromServer(Flag f){
        ParseQuery<ParseObject> favouritesQuery=new ParseQuery<ParseObject>("Favourite");
        favouritesQuery.whereMatches("flagId",f.getID());
        favouritesQuery.whereMatches("userName", getCurrentLoggedInUserName());
        favouritesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                objects.get(0).deleteEventually();
            }
        });

    }

    //RATING---------------------------------------------------------------//

    public static void getTopRankedFlagsFromServer(){
        //TODO
    }
    public static void submitRatingToServer(Flag f, boolean upOrDown){
        final ParseObject rating = new ParseObject("Rating");
        rating.put("userName", getCurrentLoggedInUserName());
        rating.put("flagId",f.getID());
        rating.put("rating", upOrDown);
        rating.saveInBackground();
    }
    /*
    maybe not necessary since we can change a rating by just submitting another one
    (e.g at first I like a flag, but then I realize that it sucks, so I can just rate again)

    public static void deleteRatingFromServer(Flag f){

    }
    */


    //HELPER FUNCTIONS------------------------------------------------------//

    public static String getCurrentLoggedInUserName() {
        //before executing this, check if user is logged in!

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUsername();
        } else {
            //this should almost never happen, because before executing getCurrentLoggedInUserName, one should always check if the user is logged in
            //but it could happen that the user is logged out right after isLoggedIn() is checked
            //TODO: handle this error with an alert or exit the app gracefully
            return "DefaultUser";
        }


    }
}
