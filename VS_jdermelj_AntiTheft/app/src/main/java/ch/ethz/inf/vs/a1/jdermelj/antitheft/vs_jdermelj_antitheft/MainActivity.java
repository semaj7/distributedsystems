package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_SETTINGS = 1;
    Intent intentToStartAntiTheftService;
    ToggleButton toggleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        if (Settings.sensitivity == -1) Settings.sensitivity = Settings.SENSITIVITY_DEFAULT;

        if (Settings.timeout == -1) Settings.timeout = Settings.TIMEOUT_DEFAULT;

        toggleButtonCreation();

        intentToStartAntiTheftService = new Intent(this, AntiTheftServiceImpl.class);
        activateAlarm();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }



    @Override
    protected void onStart() {

        Intent callingIntent = getIntent();

        //check if activity was started by clicking on Notification, which includes the deactivation_code
        if(callingIntent.getBooleanExtra(AntiTheftServiceImpl.DEACTIVATION_CODE, false))
        {
            if(toggleButton.isChecked()) {

                toggleButton.setChecked(false);
            }
            else {
                deactivateAlarm();
            }
        }
        super.onStart();
    }

    public void toggleButtonCreation() {

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // The toggleButton is set to On

                    Toast.makeText(MainActivity.this, getString(R.string.alarm_activated), Toast.LENGTH_SHORT).show();

                    activateAlarm();


                } else {

                    //button is now set to Off
                    Toast.makeText(MainActivity.this, getString(R.string.alarm_deactivated), Toast.LENGTH_SHORT).show();

                    deactivateAlarm();

                }
            }
        });


    }

    public void activateAlarm() {

        Log.d("asdf", "starting service");

        startService(intentToStartAntiTheftService);

    }

    public void deactivateAlarm() {

        Log.d("asdf", "ending service");

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
