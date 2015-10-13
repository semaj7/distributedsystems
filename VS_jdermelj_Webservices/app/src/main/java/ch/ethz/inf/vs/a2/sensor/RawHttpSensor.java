package ch.ethz.inf.vs.a2.sensor;

import android.util.Log;

import ch.ethz.inf.vs.a2.http.HttpRawRequestImpl;
import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;
import ch.ethz.inf.vs.a2.http.SimpleHttpClientFactory;

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
        httpClient = SimpleHttpClientFactory.getInstance(SimpleHttpClientFactory.Type.RAW);

        Log.d("debug", "Set up RawHttpClient");


    }

    @Override
    public double parseResponse(String response) {
        Log.d("debug", "Trying to parse");

        ResponseParserImpl responseParser = new ResponseParserImpl();
        return responseParser.parseResponse(response);

    }

    @Override
    public void getTemperature() throws NullPointerException {

        worker = new AsyncWorker();
        worker.execute(requester);

    }
}
