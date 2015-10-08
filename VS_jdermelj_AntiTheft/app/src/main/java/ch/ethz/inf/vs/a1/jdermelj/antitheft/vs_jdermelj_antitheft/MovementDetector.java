package ch.ethz.inf.vs.a1.jdermelj.antitheft.vs_jdermelj_antitheft;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.util.Pair;

import java.util.Queue;
import java.util.Iterator;
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

    private Queue<Pair<Long, Double>> data = new LinkedBlockingQueue<Pair<Long, Double>>();

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

        Log.d("doAlarmLogic", String.valueOf(values[0]) + " " + String.valueOf(values[1]) + " " + String.valueOf(values[2]));
        long currentTime = System.currentTimeMillis();

        Pair<Long, Double> nw = new Pair<Long, Double>(currentTime, Math.max(abs(values) - 9.8, 0));
        data.add(nw);


        //return if the service is younger than 5 seconds
        if ((currentTime - creationTime) < msUntilAlarmGoesOff || context == null) return false;


        //just to test
        //if (sensitivity > 50)

        long t0 = currentTime - 5000;
        Iterator<Pair<Long, Double>> iter = data.iterator();

        //sum and counter are needed to calculate the average
        double sum = 0;
        int counter = 0;

        while(iter.hasNext()){
            Pair<Long, Double> i = iter.next();
            if(i.first < t0){
                iter.remove();
            }
            else{
                sum+= i.second;
                counter++;
            }
        }
        int sensitivity = Settings.sensitivity; //default is recommended
        System.out.println(sensitivity);
        double thresh = 3-(sensitivity * 29 / 1000); //Threshold (chosen empirically), ranges from 0.2 to 3.2
        if( Math.ceil(sum) / counter < thresh)
            return false;
        else
            return true;

    }

    public void destroy() {
        sensorManager.unregisterListener(this);
    }

    private double abs(float[] values){ //Calculates the absolute acceleration
        return (Math.sqrt((double)(values[1]*values[1]) + (double)(values[2]*values[2]) + (double)(values[0]*values[0])));
    }

}
