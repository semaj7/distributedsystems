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



    public final static List<Category> filteredCategories = new ArrayList<Category>();

    public final static String[] slideMenuStrings = new String[]{"Search", "Favourites", "Filters","Ranking", "What's new","Settings"};

    public final static List<Flag> flagsToShow = new ArrayList<Flag>();

    public final static List<Flag> allFlags = new ArrayList<Flag>();

}
