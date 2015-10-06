package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Andres on 06.10.15.
 */
public class SettingsActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.general_preferences);
    }
/*
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    String strUserName = SP.getString("username", "NA");
    boolean bAppUpdates = SP.getBoolean("applicationUpdates",false);
    String downloadType = SP.getString("downloadType","1");

    to access settings from anywhere in app
    */
}
