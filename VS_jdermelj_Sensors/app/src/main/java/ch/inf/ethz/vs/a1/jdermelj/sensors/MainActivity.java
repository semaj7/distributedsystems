package ch.inf.ethz.vs.a1.jdermelj.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager sm;
    List<Sensor> sensors;
    ListView listview;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.button);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors=sm.getSensorList(Sensor.TYPE_ALL);
        List<String> sensor_names;
        sensor_names=new ArrayList<String>();

        for (int i=0;i<sensors.size();i++){
            sensor_names.add(((Sensor)sensors.get(i)).getName());
        }

        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sensor_names));
        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent;
                myIntent = new Intent(MainActivity.this, SensorActivity.class);
                myIntent.putExtra("SENSORINDEX", position);
                MainActivity.this.startActivity(myIntent);
            }


        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent;
                myIntent = new Intent(MainActivity.this, ActuatorsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

    }
}
