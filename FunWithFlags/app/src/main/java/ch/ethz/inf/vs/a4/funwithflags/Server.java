package ch.ethz.inf.vs.a4.funwithflags;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
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

                        ParseObject parseFlag=flags.get(i);

                        //TODO remove this stuff, it's only for testing
                        if(i==10){
                            Server.submitFavouriteToServer(parseFlagToFlag(context, parseFlag));
                            Log.d("Pascal debug", "as always, just submitting Flag number 10 to favourites ");
                        }
                        //remove until here


                        ret.add(parseFlagToFlag(context, parseFlag));
                        //Log.d("pascal debug","just downloaded a flag");
                    }
                }
                Data.setAllFlags(ret);

            }

        });

    }

    public static void submitFlag(Flag f){


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

    public static ParseObject getParseFlag(Flag f){
        ParseQuery<ParseObject> flagQuery=new ParseQuery<ParseObject>("Flag");
        try {
            return flagQuery.get(f.getID());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("debug","tried to download a flag that was not yet uploaded");
        return null;
    }


    //FAVOURITES-----------------------------------------------------------//

    public static void getFavouritesFromServer(final android.content.Context  context){
        //TODO: right now one can have an indefined amount of favourites, should change this
        //new implementation
        Log.d("Pascal debug", "downloading favourites...");
        ParseUser user=ParseUser.getCurrentUser();
        if(user!=null) {

            ParseRelation<ParseObject> favouritesRelation = user.getRelation("favourite");
            try {
                Log.d("Pascal debug", "got 'em favourites, now saving 'em favourites locally...");
                saveFavouritesLocally(context, favouritesRelation.getQuery().find());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //old implementation
        /*
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
        */
    }

    private static void saveFavouritesLocally(Context context, List<ParseObject> objects){
        //TODO: now we have both a list and a array of favourites...
        Data.favouriteFlagsList.clear();
        for (int i = 0; i < objects.size() ; i++) {
            Log.d("Pascal debug", "added a favourite to the local list!!");
            Data.favouriteFlagsList.add(parseFlagToFlag(context, objects.get(i)));
        }
        Data.favouriteFlags=(Flag[]) Data.favouriteFlagsList.toArray();

    }

    public static void submitFavouriteToServer(Flag f){

        ParseObject parseFlag= getParseFlag(f);
        if(parseFlag==null) {
            Log.d("debug", "Trying to add a flag to favourites that is not uploaded yet");
            return;
        }

        //implementation with relation
        ParseUser user=ParseUser.getCurrentUser();
        Log.d("Pascal debug", user.getUsername());
        if(user!=null) {
            ParseRelation<ParseObject> favouritesRelation = user.getRelation("favourite");
            //if(favouritesRelation==null) {Log.d("Pascal debug", "lfw ftw");}
            //if(favouritesRelation!=null) {Log.d("Pascal debug", "it's not null. very dope");}
            //if(parseFlag==null) {Log.d("Pascal debug", "the f***ing parseFlag is null");}
            favouritesRelation.add(parseFlag);
        }
        user.saveEventually();
        //Log.d("Pascal debug", "it f***ing should have submitted a favourit, i don't have ONE idea why it didn't");
        /*
        //old implementation
        final ParseObject favourite = new ParseObject("Favourite");
        favourite.put("userName", getCurrentLoggedInUserName());
        favourite.put("flagId",f.getID());
        favourite.saveInBackground();
        */
    }
    public static void deleteFavouriteFromServer(Flag f){
        ParseObject parseFlag= getParseFlag(f);
        if(parseFlag==null) {
            Log.d("debug", "Trying to remove a flag from favourites that is not uploaded yet");
            return;
        }
        //implementation with relation
        ParseUser user=ParseUser.getCurrentUser();
        ParseRelation<ParseObject> favouritesRelation=user.getRelation("favourite");
        favouritesRelation.remove(getParseFlag(f));


        //old implementation
        /*
        ParseQuery<ParseObject> favouritesQuery=new ParseQuery<ParseObject>("Favourite");
        favouritesQuery.whereMatches("flagId",f.getID());
        favouritesQuery.whereMatches("userName", getCurrentLoggedInUserName());
        favouritesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                objects.get(0).deleteEventually();
            }
        });
        */

    }

    //RATING---------------------------------------------------------------//

    public static void submitRatingToServer(Flag f, boolean upOrDown){

        ParseObject parseFlag= getParseFlag(f);
        if(parseFlag==null) {
            Log.d("debug", "Trying to rate a flag that is not submitted yet");
            return;
        }

        ParseObject flag=getParseFlag(f);
        ParseRelation<ParseObject> upVotesRelation=flag.getRelation("upVotes");
        ParseRelation<ParseObject> downVotesRelation=flag.getRelation("downVotes");

        //remove already existing rating
        upVotesRelation.getQuery().whereMatches("upVotes",getCurrentLoggedInUserName()).findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    objects.get(i).deleteEventually();
                }
            }
        });
        upVotesRelation.getQuery().whereMatches("downVotes",getCurrentLoggedInUserName()).findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    objects.get(i).deleteEventually();
                }
            }
        });


        if(upOrDown){
            upVotesRelation.add(ParseUser.getCurrentUser());
        }
        else{
            downVotesRelation.add(ParseUser.getCurrentUser());
        }

        //update the count
        try {
            flag.put("upVotesCount",upVotesRelation.getQuery().count());
            flag.put("downVotesCount",downVotesRelation.getQuery().count());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        flag.saveInBackground();
    }


    //FOLLOWING---------------------------------------------------------------//

    public static void getFollowingUsers(){
        ParseUser user=ParseUser.getCurrentUser();
        ParseRelation<ParseObject> followingRelation=user.getRelation("following");
        ParseQuery<ParseObject> followingRelationQuery= followingRelation.getQuery();
        followingRelationQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                saveFollowingUsersLocally(objects);
            }
        });
    }

    private static void saveFollowingUsersLocally(List<ParseObject> objects){
        Data.followingUsers.clear();
        for (int i = 0; i < objects.size() ; i++) {
            Data.followingUsers.add((String)(objects.get(i).get("userName")));
        }
    }

    public static void follow(String userName){
        ParseUser user=ParseUser.getCurrentUser();
        ParseRelation<ParseObject> followingRelation=user.getRelation("following");
        followingRelation.add(getParseUser(userName));
        user.saveInBackground();
    }

    public static void unFollow(String userName){
        ParseUser user=ParseUser.getCurrentUser();
        ParseRelation<ParseObject> followingRelation=user.getRelation("following");
        followingRelation.remove(getParseUser(userName));
        user.saveInBackground();
    }

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

    public static ParseUser getParseUser(String userName){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("userName", userName);
        try {
            return query.find().get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Flag parseFlagToFlag(Context context, ParseObject parseFlag){

        String ID = (String) parseFlag.getObjectId();
        String userName = (String) parseFlag.get("userName");
        String text = (String) parseFlag.get("content");
        ParseGeoPoint geoPoint = (ParseGeoPoint) parseFlag.get("geoPoint");
        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        Category category = Category.getByName((String) parseFlag.get("categoryName"));
        Date date = (Date) parseFlag.get("date");

        Flag flag=new Flag(ID, userName, text, latLng, category, date, context);

        //if the upvotes-count on the server is already set, we get it, if not it just stays what it
        // was locally (i.e "0" in the beginning)
        Object downVotesCount=parseFlag.get("downVotesCount");
        Object upVotesCount=parseFlag.get("upVotesCount");
        if(downVotesCount!=null&&upVotesCount!=null){
            flag.setDownVotes((int)downVotesCount);
            flag.setUpVotes((int)upVotesCount);
        }

        /*
        ParseRelation<ParseObject> upVotesRelation=parseFlag.getRelation("upVotes");
        ParseRelation<ParseObject> downVotesRelation=parseFlag.getRelation("downVotes");

        try {
            flag.setDownVotes(downVotesRelation.getQuery().count());
            flag.setUpVotes(upVotesRelation.getQuery().count());
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        */
        return flag;
    }
}
