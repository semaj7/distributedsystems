package ch.ethz.inf.vs.a2.vs_jdermelj_webservices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.ksoap2.serialization.SoapObject;

import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;
import ch.ethz.inf.vs.a2.sensor.SensorFactory;
import ch.ethz.inf.vs.a2.sensor.Sensor;
import ch.ethz.inf.vs.a2.sensor.XmlSensor;

public class Activity_2 extends AppCompatActivity implements ch.ethz.inf.vs.a2.sensor.SensorListener{


    Sensor xmlSensor;
    Sensor soapSensor;
    TextView tempValTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        tempValTextView = (TextView) findViewById(R.id.TEMPVALTXT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_2, menu);

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


    //Get the temperature by manually invoking the SOAP request
    public void getXMLTemperature(View view){
        Log.d("debug", "button xml clicked");


        soapSensor = SensorFactory.getInstance(SensorFactory.Type.XML);
        soapSensor.registerListener(this);
    }
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.MANBUTT:
                Log.d("debug", "button xml clicked");
                xmlSensor= SensorFactory.getInstance(SensorFactory.Type.XML);
                xmlSensor.registerListener(this);
                break;

            case R.id.SOAPBUTT:
                Log.d("debug", "button soap clicked");
                xmlSensor= SensorFactory.getInstance(SensorFactory.Type.SOAP);
                xmlSensor.registerListener(this);
                break;
        }
    }

    //Get the temperature by invoking the SOAP request with the KSOAP2-Library
    public void getSoapTemperature(View view){
        Log.d("debug", "button soap clicked");

        soapSensor = SensorFactory.getInstance(SensorFactory.Type.SOAP);
        soapSensor.registerListener(this);
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
