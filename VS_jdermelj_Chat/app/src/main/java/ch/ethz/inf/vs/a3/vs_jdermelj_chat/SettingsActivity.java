package ch.ethz.inf.vs.a3.vs_jdermelj_chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import ch.ethz.inf.vs.a3.R;
import java.util.List;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import ch.ethz.inf.vs.a3.R;


public class SettingsActivity extends AppCompatActivity{


    private EditText addressEdit;
    private EditText portEdit;
    private TextView settingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //Set the Views
        addressEdit = (EditText) findViewById(R.id.editAddress);
        portEdit = (EditText) findViewById(R.id.editPort);
        settingView  = (TextView) findViewById(R.id.settingView);

        showSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onClickSave(View view){

        //Store Address
        String address = addressEdit.getText().toString();
        setAddress(this, address);

        //Store Port
        String port = portEdit.getText().toString();
        setPort(this, port);

        showSettings();
    }

    //Displays current settings on the textviews
    public void showSettings(){
        String address = getAddress(this);
        String port = getPort(this);
        addressEdit.setText(address);
        portEdit.setText(port);
        settingView.setText("Current settings:\nURL: " + address + "\nPort: " + port);
    }


    // Stores the currently entered username in sharedPreferences (from stackoverflow)
    public static void setAddress(Context context, String address) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("address", address);
        editor.commit();
    }
    // Stores the currently entered port in sharedPreferences (from stackoverflow)
    public static void setPort(Context context, String port) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("port", port);
        editor.commit();
    }
    // Gets the address stored in sharedPreferences (from stackoverflow)
    public static String getAddress(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        return prefs.getString("address", "");
    }
    // Gets the port stored in sharedPreferences (from stackoverflow)
    public static String getPort(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        return prefs.getString("port", "");
    }


}
