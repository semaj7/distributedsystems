package ch.ethz.inf.vs.a2.sensor;

import android.util.Log;

import ch.ethz.inf.vs.a2.http.HttpRawRequestImpl;
import ch.ethz.inf.vs.a2.http.RawHttpClient;
import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;

/**
 * Created by Andres on 12.10.15.
 */
public class RawHttpSensor extends AbstractSensor {

    HttpRawRequestImpl requester;
    AsyncWorker worker;

    @Override
    protected void setHttpClient() {

        int port = RemoteServerConfiguration.REST_PORT;
        String host = RemoteServerConfiguration.HOST;
        String path = RemoteServerConfiguration.PATH_TO_SPOT1_TEMPERATURE;

        requester = new HttpRawRequestImpl(host, port, path);
        httpClient = new RawHttpClient();

        Log.d("debug", "Set up RawHttpClient");

        worker = new AsyncWorker();


    }

    @Override
    public double parseResponse(String response) {
        Log.d("debug", "Trying to parse");
        return 1.0;
    }

    @Override
    public void getTemperature() throws NullPointerException {

        //may throw NullPointerException, if requester is still null
        //always invoke setHttpClient before invoking getTemperature() (this is done in the abstract class)
        String response = worker.doInBackground(requester);

        if (response != null) Log.d("debug", response);

        worker.onPostExecute(response);

      //  double temp = parseResponse(response);


    }
}
