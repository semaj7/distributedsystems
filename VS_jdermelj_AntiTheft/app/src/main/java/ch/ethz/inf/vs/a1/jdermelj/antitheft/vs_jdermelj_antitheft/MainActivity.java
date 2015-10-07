package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_SETTINGS = 1;
    Intent intentToStartAntiTheftService;
    ToggleButton toggle;
    //TODO: use a Switch for nicer GUI


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButtonCreation();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }


    //onStart is called when the app is openend by an intent
    //this means we can check if the Intent comes from the notification that wants to stop the alarm
    @Override
    protected void onStart() {

        Intent callingIntent = getIntent();
        if(callingIntent.getBooleanExtra(AntiTheftService.DEACTIVATION_SOURCE, false))
        {
            if(toggle.isChecked()) {
                toggle.setChecked(false);
            }
            else {
                deactivateAlarm();
            }
        }
        super.onStart();
    }

    public void toggleButtonCreation() {

        toggle = (ToggleButton) findViewById(R.id.toggleButton);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //TODO: these two might be switched in the layout, laut Jimmy

                if (isChecked) {
                    //button is now set to Off
                    Toast.makeText(MainActivity.this, getString(R.string.alarm_deactivated), Toast.LENGTH_SHORT).show();

                    activateAlarm();

                } else {
                    // The toggle is set to On
                    Toast.makeText(MainActivity.this, getString(R.string.alarm_activated), Toast.LENGTH_SHORT).show();

                    deactivateAlarm();
                }
            }
        });


    }

    public void activateAlarm() {

        intentToStartAntiTheftService = new Intent(this, AntiTheftService.class);
        startService(intentToStartAntiTheftService);

    }

    public void deactivateAlarm() {

        intentToStartAntiTheftService = new Intent(this, AntiTheftService.class);
        stopService(intentToStartAntiTheftService);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
