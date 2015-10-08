package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Call super :
        super.onCreate(savedInstanceState);

        // Set the activity's fragment :
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }


    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

        private SeekBarPreference _seekBarSensitivity;
        private SeekBarPreference _seekBarTimeout;

        private int sens; //access this field to get the sensitivity that was set
        private int time; //access this field to get the timeout that was set

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.general_preferences);


            // Get widgets :
            _seekBarSensitivity = (SeekBarPreference) this.findPreference("SENSKEY");
            _seekBarTimeout = (SeekBarPreference) this.findPreference("TIMEKEY");

            // Set listener :
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            // Set seekbar summary :
            sens = Settings.sensitivity;
            sens = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SENSKEY", sens);
            _seekBarSensitivity.setSummary(getString(R.string.settings_summary_sens).replace("$1", "" + sens));


            time = Settings.timeout;
            time = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("TIMEKEY", time);
            _seekBarTimeout.setSummary(getString(R.string.settings_summary_time).replace("$1", "" + time));

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Set seekbar summary :
            sens = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SENSKEY", 50);
            _seekBarSensitivity.setSummary(this.getString(R.string.settings_summary_sens).replace("$1", "" + sens));

            Settings.sensitivity = sens;



            time = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("TIMEKEY", 50);
            _seekBarTimeout.setSummary(this.getString(R.string.settings_summary_time).replace("$1", "" + time));

            Settings.timeout = time;
        }
    }
}