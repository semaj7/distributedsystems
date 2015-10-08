package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.util.CircularArray;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Andres on 06.10.15.
 */
public class MovementDetector extends AbstractMovementDetector {


    private Context context;


    private final int msUntilAlarmGoesOff = 5000;

    private long creationTime;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private CircularArray<Long> times;
    private CircularArray<float[]> data;
    public MovementDetector() {

        context = null;
        creationTime = System.currentTimeMillis();

    }


    public void setContext(Context c) {

        context = c;

        sensorManager = (SensorManager)c.getSystemService(c.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected boolean doAlarmLogic(float[] values) {
        Log.d("doAlarmLogic",String.valueOf(values[0])+" " +String.valueOf(values[1])+" " +String.valueOf(values[2]));
        long currentTime = System.currentTimeMillis();

        times.addLast(currentTime);
        data.addLast(values);

        //return if the service is younger than 5 seconds
        if ((currentTime - creationTime) < msUntilAlarmGoesOff || context == null) return false;

        int sensitivity = Settings.sensitivity;

        //just to test
        //if (sensitivity > 50)

        //activates alarm if movement is bigger than 12
        return (values[0]+values[1]+values[2])>12;

        //delete everything in the times and data array that is not from the last 5 sec
        //while(false);

    }

    public void destroy() {
        sensorManager.unregisterListener(this);
    }
}
