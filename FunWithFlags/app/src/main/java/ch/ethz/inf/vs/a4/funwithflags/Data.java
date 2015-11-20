package ch.ethz.inf.vs.a4.funwithflags;

import java.util.ArrayList;
import java.util.List;

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

    public final static String[] slideMenuStrings = new String[]{"Search", "Favourites", "Filters","Ranking", "What's new","Settings"};

    public final static List<Flag> flagsToShow = new ArrayList<Flag>();

    public static List<Flag> allFlags = new ArrayList<Flag>();


    // todo: get and put this information (fav, and top) from server, keep stuff consistent
    public final static Flag[] favouriteFlags = new Flag[MapsActivity.MAX_NUMBER_OF_FAVOURITES];
    public final static Flag[] topRankedFlags = new Flag[MapsActivity.TOP_RANKED_FLAGS_AMOUNT];

    public static final void checkIfTopAndAdd(Flag flag){
        if (containsFlag(flag, topRankedFlags)) {
            return;
        }
        
        for(int i = 0; i < topRankedFlags.length; i++){
            if(topRankedFlags[i] == null){
                topRankedFlags[i] = flag;
                return;
            }
            if(flag.getVoteRateAbsolut() > topRankedFlags[i].getVoteRateAbsolut()){
                replaceWithMinimumTopFlag(flag);
                System.out.println("debug, found a new top flag");
                return;
            }
        }
    }

    // todo: if we have enough time, implement a more efficient way to do do this, and the whole storage of the top flags
    private static void replaceWithMinimumTopFlag(Flag flag) {
        int minimumRating=0;
        for(int i = 0; i < MapsActivity.TOP_RANKED_FLAGS_AMOUNT; i++){
            if(topRankedFlags[i] == null){
                topRankedFlags[i] = flag;
                return;
            } else {

                // normal case
                if(topRankedFlags[i].getVoteRateAbsolut() < topRankedFlags[minimumRating].getVoteRateAbsolut()){
                    // new minimum is found
                    minimumRating = i;
                }
            }
        }
        topRankedFlags[minimumRating] = flag;
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
                if (flags[i].getID() == flag.getID())
                    return true;
            }
        }
        return false;
    }


    public static void setAllFlags(List<Flag> flags){
        allFlags.clear();
        allFlags=new ArrayList<Flag>(flags);
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
}
