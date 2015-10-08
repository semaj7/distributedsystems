package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Created by Andres on 06.10.15.
 */
public class MovementDetector extends AbstractMovementDetector {

    private Context context;


    private final int msUntilAlarmGoesOff = 5000;

    private long creationTime;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;


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

        long currentTime = System.currentTimeMillis();

        //return if the service is younger than 5 seconds
        if ((currentTime - creationTime) < msUntilAlarmGoesOff) return false;

        int sensitivity = Settings.sensitivity;

        //just to test
        if (sensitivity > 50)

        //return x;
        return true;

        else return false;


    }

    public void destroy() {
        sensorManager.unregisterListener(this);
    }
}
