package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.Random;

/**
 * Created by Andres on 06.10.15.
 */
public class MovementDetector extends AbstractMovementDetector {

    private Context context;


    private final int msUntilAlarmGoesOff = 5000;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    public MovementDetector() {
        context = null;
    }

    public void setContext(Context c) {
        context = c;
        sensorManager = (SensorManager)c.getSystemService(c.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected boolean doAlarmLogic(float[] values) {


        //just to test
        Boolean x;

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        x = rand.nextBoolean();

        //return x;
        return true;

        //TODO: get sensitivity value (which is from 0 to 100) from the SettingsActivity

        //TODO: do fancy stuff with the accelerometerSensor
    }

    public void destroy() {
        sensorManager.unregisterListener(this);
    }
}
