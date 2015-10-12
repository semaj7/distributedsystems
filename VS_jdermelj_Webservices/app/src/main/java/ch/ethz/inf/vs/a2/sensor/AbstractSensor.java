package ch.ethz.inf.vs.a2.sensor;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import ch.ethz.inf.vs.a2.http.SimpleHttpClient;

/**
 * Implementation of a sensor representation.
 *
 * @author Leyna Sadamori
 * @see Sensor
 * @see ResponseParser
 * @see SensorListener
 * @see SimpleHttpClient
 */
public abstract class AbstractSensor implements Sensor, ResponseParser {
    protected String name = null;
    protected List<SensorListener> listeners = new ArrayList<SensorListener>();
    protected SimpleHttpClient httpClient = null;

    public AbstractSensor() {
        setHttpClient();
    }

    /**
     * Depending of the actual implementation, different clients can be used.
     * Do not forget to set the client!
     */
    protected abstract void setHttpClient();

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void registerListener(SensorListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(SensorListener listener) {
        listeners.remove(listener);
    }

    /**
     * AsyncTask to execute the request in a seperate thread. This AsyncTask
     * makes use of the Requester interface to support different implementations
     * of making a request. The response will be parsed according to the
     * implementation of the ResponseParser interface. Finally, all listeners
     * are notified about the result.
     *
     * @author Leyna Sadamori
     * @see SimpleHttpClient
     * @see ResponseParser
     */
    public class AsyncWorker extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            return httpClient.execute(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            double value = parseResponse(result);
            if (value != Double.NaN) {
                for (SensorListener listener : listeners) {
                    listener.onReceiveDouble(value);
                }
            } else {
                for (SensorListener listener : listeners) {
                    listener.onReceiveString(result);
                }
            }
        }
    }
}
