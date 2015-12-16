package ch.ethz.inf.vs.a4.funwithflags;

import android.content.Context;
import android.os.AsyncTask;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pascalwiesmann on 30.11.15.
 */

    /*
    SERVER COMMUNICATION
    */



public class Server {

    public static AtomicInteger threadsInThisClass = new AtomicInteger(0);


    public static AtomicBoolean done = new AtomicBoolean(true);
    //FLAGS-----------------------------------------------------------//

    //asynchronously just downloads the flags and stores them in allFlags
    public static void getFlagsFromServer(final android.content.Context  context){

        if (done.compareAndSet(true, false)) {

            threadsInThisClass.incrementAndGet();

            new AsyncTask<Void, Void, Boolean>() {


                protected Boolean doInBackground(Void... params) {
                    int i = 0;
                    ParseQuery<ParseObject> flagQuery = new ParseQuery<ParseObject>("Flag");
                    List<ParseObject> flags = new ArrayList<ParseObject>();
                    while (!done.get()) {
                        flagQuery.setLimit(1000);
                        flagQuery.setSkip(i * 1000);
                        try {
                            flags.addAll(flagQuery.find());
                        } catch (ParseException e) {
                            //empty or failed -> finish
                            done.set(true);
                        }
                        i++;
                    }

                    final ArrayList<Flag> ret = new ArrayList<Flag>();
                    for (i = 0; i < flags.size(); i++) {
                        ParseObject parseFlag = flags.get(i);
                        ret.add(parseFlagToFlag(context, parseFlag));
                    }

                    Data.setAllFlags(ret);

                    threadsInThisClass.decrementAndGet();
                    return null;
                }

            }.execute();

        }

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
        //TODO: Pascal: is this still needed??? seems to work without it...

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

        Log.d("debug", "tried to download a flag that was not yet uploaded");
        return null;
    }


    //FAVOURITES-----------------------------------------------------------//

    private static AtomicBoolean getFavouritesLock = new AtomicBoolean(false);

    public static void getFavouritesFromServer(final android.content.Context  context){
        //TODO: right now one can have an undefined amount of favourites, should change this
        //TODO: Pascal: how should this limit behave?

        if (getFavouritesLock.compareAndSet(false, true)) {

            new AsyncTask<Void, Void, Boolean>() {
                protected Boolean doInBackground(Void... params) {
                    threadsInThisClass.incrementAndGet();


                    Log.d("Pascal debug", "downloading favourites...");
                    ParseUser user = ParseUser.getCurrentUser();
                    if (user != null) {
                        ParseRelation<ParseObject> favouritesRelation = user.getRelation("favourite");
                        ParseQuery<ParseObject> favouritesRelationQuery = favouritesRelation.getQuery();
                        //TODO: ok, like this?
                        favouritesRelationQuery.setLimit(MapsActivity.MAX_NUMBER_OF_FAVOURITES);
                        //
                        try {

                            saveFavouritesLocally(context, favouritesRelationQuery.find());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }



                    }

                    threadsInThisClass.decrementAndGet();

                    getFavouritesLock.set(false);

                    return null;
                }

            }.execute();

        }

    }

    private static void saveFavouritesLocally(Context context, List<ParseObject> objects){
        //TODO: now we have both a list and a array of favourites...
        //TODO: Pascal: in my opinion we can remove the list, it is never used, right?
        Data.favouriteFlagsList.clear();
        for (int i = 0; i < objects.size() ; i++) {
            Log.d("Pascal debug", "added a favourite to the local list!!");
            Data.favouriteFlagsList.add(parseFlagToFlag(context, objects.get(i)));
            Data.favouriteFlags[i] = Data.favouriteFlagsList.get(i);
        }
    }

    public static void submitFavouriteToServer(Flag f){

        ParseObject parseFlag= getParseFlag(f);
        if(parseFlag==null) {
            Log.d("debug", "Trying to add a flag to favourites that is not uploaded yet");
            return;
        }

        ParseUser user=ParseUser.getCurrentUser();
        Log.d("Pascal debug", user.getUsername());
        if(user!=null) {
            ParseRelation<ParseObject> favouritesRelation = user.getRelation("favourite");
            favouritesRelation.add(parseFlag);
            user.saveEventually();
        }

    }
    public static void deleteFavouriteFromServer(Flag f){
        ParseObject parseFlag= getParseFlag(f);
        if(parseFlag==null) {
            Log.d("debug", "Trying to remove a flag from favourites that is not uploaded yet");
            return;
        }

        Log.d("debug", "Trying to remove a flag from favourites...");

        ParseUser user=ParseUser.getCurrentUser();

        if (user != null) {
            ParseRelation<ParseObject> favouritesRelation = user.getRelation("favourite");
            favouritesRelation.remove(getParseFlag(f));
            user.saveInBackground();

        }

    }

    //RATING---------------------------------------------------------------//

    public static void submitRatingToServer(Flag f, boolean upOrDown){
        //TODO: could be made more efficient
        ParseObject flag=getParseFlag(f);

        if(flag==null) {
            Log.d("debug", "Trying to rate a flag that is not submitted yet");
            return;
        }

        ParseRelation<ParseObject> upVotesRelation=flag.getRelation("upVotes");
        ParseRelation<ParseObject> downVotesRelation=flag.getRelation("downVotes");

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null){

            upVotesRelation.remove(currentUser);
            downVotesRelation.remove(currentUser);


            if(upOrDown){
                upVotesRelation.add(currentUser);
            }
            else{
                downVotesRelation.add(currentUser);
            }
            int newUpVoteCount=0;
            int newDownVoteCount=0;
            //update the count
            try {
                flag.save();
                newUpVoteCount=upVotesRelation.getQuery().count();
                Log.d("pascal debug", "updating the up/down-votes counters");
                flag.put("upVotesCount", newUpVoteCount);
                Log.d("pascal debug", "upvotes: " + newUpVoteCount);
                newDownVoteCount=downVotesRelation.getQuery().count();
                flag.put("downVotesCount", newDownVoteCount);
                Log.d("pascal debug", "downvotes: " + newDownVoteCount);
                flag.save();

            } catch (ParseException e) {
                e.printStackTrace();
            }
            f.setDownVotes(newDownVoteCount);
            f.setUpVotes(newUpVoteCount);
        }

    }


    //FOLLOWING---------------------------------------------------------------//

    private static AtomicBoolean getFollowingLock = new AtomicBoolean(false);

    public static void getFollowingUsers(){

        if (getFollowingLock.compareAndSet(false, true)) {

            threadsInThisClass.incrementAndGet();
            ParseUser user = ParseUser.getCurrentUser();

            if (user != null) {
                ParseRelation<ParseObject> followingRelation = user.getRelation("following");
                ParseQuery<ParseObject> followingRelationQuery = followingRelation.getQuery();
                followingRelationQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        saveFollowingUsersLocally(objects);
                        threadsInThisClass.decrementAndGet();
                        getFollowingLock.set(false);
                    }
                });
            }
        }
    }

    private static void saveFollowingUsersLocally(List<ParseObject> objects){
        if (objects != null) {
            Data.followingUsers.clear();
            for (int i = 0; i < objects.size(); i++) {
                Data.followingUsers.add((String) (((ParseUser) objects.get(i)).getUsername()));
                Log.d("Pascal debug", "following user " + (String) (((ParseUser) objects.get(i)).getUsername()));
            }
        }

    }

    public static void follow(String userName){
        final ParseUser otherUser=getParseUser(userName);
        final ParseUser user=ParseUser.getCurrentUser();

        if (user != null && otherUser != null ) {

            ParseRelation<ParseObject> followingRelation = user.getRelation("following");
            followingRelation.add(otherUser);

            ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("followers");
            userQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> usersFollowersList, com.parse.ParseException e) {
                    if (e == null) {
                        boolean done = false;
                        int n = usersFollowersList.size();
                        for (int i = 0; i < n; i++) {
                            ParseObject currentUsersFollowers = usersFollowersList.get(i);
                            ParseUser currentUser = (ParseUser) currentUsersFollowers.get("user");

                            String currentUserName = null;
                            try {
                                currentUserName = currentUser.fetchIfNeeded().getUsername();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            String otherUserName = otherUser.getUsername();
                            if (currentUserName.equals(otherUserName)){
                                Log.d("pascal debug", "a user that has already followers gets one more");

                                currentUsersFollowers.getRelation("followers").add(user);
                                currentUsersFollowers.saveInBackground();
                                i = n;
                                done = true;
                            }
                        }
                        if (!done) {
                            ParseObject usersFollowers = new ParseObject("followers");
                            usersFollowers.put("user", otherUser);
                            ParseRelation<ParseObject> followerRelation = usersFollowers.getRelation("followers");
                            followerRelation.add(user);
                            usersFollowers.saveInBackground();
                        }
                    }
                }

            });

            user.saveInBackground();
        }

    }

    public static void unFollow(String userName){
        final ParseUser user=ParseUser.getCurrentUser();
        final ParseUser otherUser=getParseUser(userName);

        if (user != null && otherUser != null ) {
            ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("followers");
            userQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> usersFollowersList, com.parse.ParseException e) {
                    if (e == null) {
                        boolean done = false;
                        int n = usersFollowersList.size();
                        for (int i = 0; i < n; i++) {
                            ParseObject currentUsersFollowers = usersFollowersList.get(i);
                            ParseUser currentUser = (ParseUser) currentUsersFollowers.get("user");
                            String currentUserName = null;
                            try {
                                currentUserName = currentUser.fetchIfNeeded().getUsername();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            String otherUserName = otherUser.getUsername();

                            Log.d("pascal debug", currentUser.getUsername() + " ! = " + otherUser.getUsername());
                            if (currentUserName.equals(otherUserName)) {

                                currentUsersFollowers.getRelation("followers").remove(user);
                                currentUsersFollowers.saveInBackground();
                                i = n;
                            }
                        }

                    }
                }

            });

        }

    }

    private static AtomicBoolean getFollowersLock = new AtomicBoolean(false);

    public static void getFollowers(){

        if (getFollowersLock.compareAndSet(false, true)) {

            threadsInThisClass.incrementAndGet();

            final ParseUser user = ParseUser.getCurrentUser();

            if (user != null) {
                ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("followers");
                userQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> usersFollowersList, com.parse.ParseException e) {

                        if (e == null) {
                            boolean done = false;
                            int n = usersFollowersList.size();
                            for (int i = 0; i < n; i++) {
                                ParseObject currentUsersFollowers = usersFollowersList.get(i);
                                ParseUser currentUser = (ParseUser) currentUsersFollowers.get("user");
                                if (currentUser != null) {
                                    String userName = user.getUsername();
                                    String currentUserName = null;
                                    try {
                                        currentUserName = currentUser.fetchIfNeeded().getUsername();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    if (currentUserName.equals(userName)) {
                                        //Log.d("pascal debug","Cyberdogs entry was found");
                                        currentUsersFollowers.getRelation("followers").getQuery().findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> followers, com.parse.ParseException e) {
                                                saveFollowerUsersLocally(followers);
                                            }

                                        });
                                        i = n;
                                    }
                                }
                            }

                        }


                        threadsInThisClass.decrementAndGet();

                        getFollowersLock.set(false);
                    }

                });
            }
        }
    }

    private static void saveFollowerUsersLocally(List<ParseObject> objects){
        if (objects != null) {
            Data.followerUsers = new CopyOnWriteArrayList<>();
            for (int i = 0; i < objects.size(); i++) {
                Data.followerUsers.add((String) (((ParseUser) objects.get(i)).getUsername()));
                //Log.d("pascal debug","cyberdogs follower: "+(((ParseUser) objects.get(i)).getUsername()));
            }
        }

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

    public static ParseUser getParseUser(String userName) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        Log.d("pascal debug","trying to get user "+userName);
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

        return flag;
    }
}
