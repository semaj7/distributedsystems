package ch.ethz.inf.vs.a4.funwithflags;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres on 13.11.15.
 */
public class Data {

    private Data() {
        //since this class should be static, the constructor should not get invoked
        throw new AssertionError();
    }

    public final static List<Flag> ownFlagsSet = new ArrayList<Flag>();

    public final static List<Flag> allFlags = new ArrayList<Flag>();

}
