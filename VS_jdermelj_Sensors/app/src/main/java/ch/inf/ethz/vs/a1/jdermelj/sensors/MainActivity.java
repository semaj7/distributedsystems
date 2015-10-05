package ch.inf.ethz.vs.a1.jdermelj.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager sm;
    List<Sensor> sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listview = (ListView) findViewById(R.id.listView);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors=sm.getSensorList(Sensor.TYPE_ALL);
        listview.setAdapter(new ArrayAdapter<Sensor>(this, android.R.layout.simple_list_item_1,  sensors));

    }
}
