package ch.ethz.inf.vs.a4.funwithflags;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Andres on 18.11.15.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "0tOkgHhdbWKjMHWtHlmnVEzFq83LoangMuIHIIG8", "t1apg1Ly1rHK6BhDZ5QloteIVFlNDcjbDuk9cz6c");

    }

}
