package ch.ethz.inf.vs.a2.vs_jdermelj_webservices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;
import ch.ethz.inf.vs.a2.sensor.Sensor;
import ch.ethz.inf.vs.a2.sensor.SensorFactory;

public class Activity_1 extends AppCompatActivity implements ch.ethz.inf.vs.a2.sensor.SensorListener{

    Sensor rawTempSensor;
    Sensor simpleTempSensor;
    TextView tempValTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        tempValTextView = (TextView) findViewById(R.id.TEMPVALTXT);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_1, menu);
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

    public void getRawTemperature(View view) {

        rawTempSensor = SensorFactory.getInstance(SensorFactory.Type.RAW_HTTP);
        rawTempSensor.registerListener(this);

    }

    public void getHtmlTemperature(View view) {

        simpleTempSensor = SensorFactory.getInstance(SensorFactory.Type.HTML);
        simpleTempSensor.registerListener(this);

    }



    @Override
    public void onReceiveDouble(double value) {

        if (value == RemoteServerConfiguration.ERROR_TEMPERATURE) {

            tempValTextView.setText(getString(R.string.invalid_temperature) + String.valueOf(value));

        }

        else {

            tempValTextView.setText(getString(R.string.temperatureIs) + String.valueOf(value));

        }


    }

    @Override
    public void onReceiveString(String message) {

    }




}
