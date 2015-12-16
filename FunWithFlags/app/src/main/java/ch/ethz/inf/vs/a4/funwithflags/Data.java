package ch.ethz.inf.vs.a4.funwithflags;

import android.location.Location;

import com.google.android.gms.maps.model.Marker;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Andres on 13.11.15.
 */
public final class Data {


    private Data() {
        //since this class should be static, the constructor should not get invoked
        throw new AssertionError();
    }


    //these act as a combination of static boolean and only have 1 value or none in the list
    public final static List<Category> filteringEnabled = new ArrayList<Category>();

    public static int userRating;

    public static List<Flag> downvotedFlags = new CopyOnWriteArrayList<Flag>();

    public static List<Flag> upvotedFlags = new CopyOnWriteArrayList<Flag>();

    public static List<String> followingUsers = new CopyOnWriteArrayList<String>();

    public static List<String> followerUsers = new CopyOnWriteArrayList<String>();

    public final static String[] slideMenuStrings = new String[]{"Search", "Favourites", "Filters","Ranking", "What's new"};

    public final static List<Flag> flagsToShow = new CopyOnWriteArrayList<Flag>();

    public static List<Flag> allFlags = new CopyOnWriteArrayList<Flag>();

    public static HashMap<Flag, Marker> flagMarkerHashMap = new HashMap<Flag, Marker>();

    public static CopyOnWriteArrayList<Flag>closeFlags = new CopyOnWriteArrayList<Flag>();

    public static CopyOnWriteArrayList<Flag>myFlags = new CopyOnWriteArrayList<>();

    //following the KISS principle:

    public static Location lastLocation;

    public static ParseUser user;


    public static Flag[] favouriteFlags = new Flag[MapsActivity.MAX_NUMBER_OF_FAVOURITES];
    public static Flag[] topRankedFlags = new Flag[MapsActivity.TOP_RANKED_FLAGS_AMOUNT];

    public static List<Flag>favouriteFlagsList = new ArrayList<Flag>();

    public static final void calculateUserRating() {

        Data.userRating = 0;
        for(Flag f : Data.myFlags){
            Data.userRating += f.getVoteRateAbsolut();
        }
    }

    public static final void updateCloseFlagsFromAll() {

        List<Flag> closeFlags = new ArrayList<Flag>();
        List<Flag> allFlags = Data.allFlags;

        Location lastLocation = Data.lastLocation;
        for (Flag flag : allFlags) {
            if (flag.isInRange(lastLocation))
                closeFlags.add(flag);

        }

        Data.closeFlags = new CopyOnWriteArrayList<Flag>(closeFlags);
    }

    public static final void dataSetChanged(List<Flag> flags) {
        System.out.println("debug: dataSetChanged");
        Data.setAllFlags(flags);
        updateCloseFlagsFromAll();
        Data.updateMyFlagsFromAll();
    }

    public static final void checkIfTopAndAdd(Flag flag){

        if (containsFlag(flag, topRankedFlags)) {
            System.out.println("debug, already a top flag");
            sortTopFlags();
            return;
        }
        
        for(int i = 0; i < topRankedFlags.length; i++){
            if(topRankedFlags[i] == null){
                topRankedFlags[i] = flag;
                System.out.println("debug, new top flag, since there was an empty place");
                sortTopFlags();
                return;
            }
            if(flag.getVoteRateAbsolut() > topRankedFlags[i].getVoteRateAbsolut()){
                replaceWithMinimumTopFlag(flag);
                System.out.println("debug, found a new top flag");
                sortTopFlags();
                return;
            }
        }
    }

    static Random r = new Random();

    public static final List<Flag> quickSortListByDate(List<Flag> closeFlags) {

        if (closeFlags.size() <= 1)
            return closeFlags;
        int rotationplacement = r.nextInt(closeFlags.size());
        Flag rotation = closeFlags.get(rotationplacement);
        closeFlags.remove(rotationplacement);
        ArrayList<Flag> lower = new ArrayList<Flag>();
        ArrayList<Flag> higher = new ArrayList<Flag>();
        for (Flag f : closeFlags)
            if (f.getDate().after(rotation.getDate()))
                lower.add(f);
            else
                higher.add(f);
        quickSortListByDate(lower);
        quickSortListByDate(higher);

        closeFlags.clear();
        closeFlags.addAll(lower);
        closeFlags.add(rotation);
        closeFlags.addAll(higher);
        return closeFlags;
    }

    private static void sortTopFlags() { // i know this is not the most efficient way to sort, but it is easy :P
        // at position 0 there is the flag with the most points
        // at the end there is null, if not filled yet, or the flag with the least points

        for(int i = 0; i < MapsActivity.TOP_RANKED_FLAGS_AMOUNT; i ++){
            int max = i;
            for(int e = i; e < MapsActivity.TOP_RANKED_FLAGS_AMOUNT; e ++){
                if(topRankedFlags[e] != null && topRankedFlags[max]!= null){
                    if(topRankedFlags[e].getVoteRateAbsolut() > topRankedFlags[max].getVoteRateAbsolut())
                        max = e;
                }
            }
            Flag temp = topRankedFlags[i];
            topRankedFlags[i] = topRankedFlags[max];
            topRankedFlags[max] = temp;
        }
    }

    private static void replaceWithMinimumTopFlag(Flag flag) {
        sortTopFlags();
        topRankedFlags[MapsActivity.TOP_RANKED_FLAGS_AMOUNT - 1] = flag;
    }

    /** returns true, if flag successfully added to favourites
     * false if favourites list was full
     * @param flag the flag that should get added to favourites
     * @return true if flag now in fav's, false if list was full and flag NOT in fav's.
     */
    public static final boolean addFavourite(Flag flag){
        // check if already there
        if(containsFlag(flag, favouriteFlags))
            return true;

        // look for a free place, and put it there
        for(int i = 0; i< MapsActivity.MAX_NUMBER_OF_FAVOURITES; i ++){
            if(favouriteFlags[i] == null){
                favouriteFlags[i] = flag;
                Server.submitFavouriteToServer(flag);
                return true;
            }
        }

        // no place was free, the list is full, and we could not add flag
        return false;
    }

    public static final Flag ithFavourite(int i){
        for(int c = 0; c< MapsActivity.MAX_NUMBER_OF_FAVOURITES; c ++){
            if (favouriteFlags[c] != null){
                if (i <= 0)
                    return favouriteFlags[c];
                i--;
            }
        }
        return null;
    }

    public static final boolean containsFlag(Flag flag, Flag[] flags) {

        for(int i = 0; i< flags.length; i ++) {
            if (flags[i] != null) {
                if (flags[i].equals(flag)) // simply using .equals did not work somehow. we really need to implement ID
                //now it should
                    return true;
            }
        }
        return false;
    }


    public static void setAllFlags(List<Flag> flags){
        allFlags.clear();
        myFlags.clear();
        allFlags=new CopyOnWriteArrayList<Flag>(flags);
        myFlags=new CopyOnWriteArrayList<Flag>(flags);
        for(Flag flag: flags){
            if(!Data.containsFlag(flag, topRankedFlags))
                checkIfTopAndAdd(flag);

            if(user != null) {
                if ((flag.getUserName().equals(user.getUsername())) && !myFlags.contains(flag)) {
                    myFlags.add(flag);
                    System.out.println("debug: adding " + flag.getUserName() + " to " + user.getUsername() + " s myflags list");
                }
            }
            if (!allFlags.contains(flag)) {
                allFlags.add(flag);
            }

        }
    }

    public static final void updateMyFlagsFromAll() {

        Data.myFlags = new CopyOnWriteArrayList<Flag>();
        for(Flag f: Data.allFlags){
            if(user != null) {
                if (f.getUserName().equals(Data.user.getUsername()))
                    Data.myFlags.add(f);
            }
        }
    }

    public static final Flag ithRanked(int i){
        for(int c = 0; c< MapsActivity.TOP_RANKED_FLAGS_AMOUNT; c ++){
            if (topRankedFlags[c] != null){
                if (i <= 0)
                    return topRankedFlags[c];
                i--;
            }
        }
        return null;
    }

    public static boolean deleteIthFavourite(int favouriteNRtoDelete) {
        if (favouriteNRtoDelete >= 0 && favouriteNRtoDelete < MapsActivity.MAX_NUMBER_OF_FAVOURITES) {
            Server.deleteFavouriteFromServer(favouriteFlags[favouriteNRtoDelete]);
            favouriteFlags[favouriteNRtoDelete] = null;
            return true;
        }
        return false;
    }

    public static void deleteFavouriteFlag(Flag f) {
        for(int i = 0 ; i < MapsActivity.MAX_NUMBER_OF_FAVOURITES; i ++)
            if(favouriteFlags[i].equals(f)) {
                deleteIthFavourite(i);
                return;
            }

    }

    public static void putUpvoted(Flag flag) {
        if(downvotedFlags.contains(flag)) {
            downvotedFlags.remove(flag);
        }
        upvotedFlags.add(flag);
    }

    public static void putDownvoted(Flag flag){
        if(upvotedFlags.contains(flag)) {
            upvotedFlags.remove(flag);
        }
        downvotedFlags.add(flag);
    }

    public final static void follow(String username){
        followingUsers.add(username);
    }

    public final static void unFollow(String username){
        if(followingUsers.contains(username))
            followingUsers.remove(username);
    }

    public static List<Flag> flagsSortedByRating(List<Flag> flags) {
        if (flags.size() <= 1)
            return flags;
        int rotationplacement = r.nextInt(flags.size());
        Flag rotation = flags.get(rotationplacement);
        flags.remove(rotationplacement);
        List<Flag> lower = new CopyOnWriteArrayList<Flag>();
        List<Flag> higher = new CopyOnWriteArrayList<Flag>();
        for (Flag f : flags)
            if (f.getVoteRateAbsolut() > (rotation.getVoteRateAbsolut()))
                lower.add(f);
            else
                higher.add(f);
        flagsSortedByRating(lower);
        flagsSortedByRating(higher);

        flags.clear();
        flags.addAll(lower);
        flags.add(rotation);
        flags.addAll(higher);
        return flags;    }

    public static List<Flag> flagsFrom(List<String> users) {
        List<Flag> result = new CopyOnWriteArrayList<Flag>();

        for(Flag f: allFlags){
            if(users.contains(f.getUserName())){
                result.add(f);
            }
        }

        return result;

    }
}
