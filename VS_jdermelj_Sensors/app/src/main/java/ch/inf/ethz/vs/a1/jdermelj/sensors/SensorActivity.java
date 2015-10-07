package ch.inf.ethz.vs.a1.jdermelj.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sm;
    List<Sensor> sensors;
    int sensor_index;
    Sensor current_sensor;
    ArrayAdapter<String> sensor_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        final ListView listview = (ListView) findViewById(R.id.listView);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors=sm.getSensorList(Sensor.TYPE_ALL);
        sensor_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listview.setAdapter(sensor_adapter);
        sensor_index=getIntent().getExtras().getInt("SENSORINDEX");
        current_sensor=sensors.get(sensor_index);
        sm.registerListener(this, current_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensor_adapter.clear();
        float[] values=event.values;
        for(int i=0;i<values.length;i++){
            sensor_adapter.add(String.valueOf(values[i]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
