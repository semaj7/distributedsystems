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

    public final static List<Flag> allFlags = new ArrayList<Flag>();

    public final static Flag[] favouriteFlags = new Flag[MapsActivity.MAX_NUMBER_OF_FAVOURITES];



    /** returns true, if flag successfully added to favourites
     * false if favourites list was full
     * @param flag the flag that should get added to favourites
     * @return true if flag now in fav's, false if list was full and flag NOT in fav's.
     */
    public static final boolean addFavourite(Flag flag){
        // check if already there
        if(containsFavourite(flag))
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

    public static final boolean containsFavourite(Flag flag) {

        for(int i = 0; i< MapsActivity.MAX_NUMBER_OF_FAVOURITES; i ++) {
            if (favouriteFlags[i] != null) {
                if (favouriteFlags[i].equals(flag))
                    return true;
            }
        }
        return false;
    }
}
