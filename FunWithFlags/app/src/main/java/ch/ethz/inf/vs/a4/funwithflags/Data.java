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
        System.out.println("debug, check if top flag");
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
