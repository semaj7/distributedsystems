package ch.ethz.inf.vs.a2.sensor;

import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;
import ch.ethz.inf.vs.a2.http.SimpleHttpClientFactory;

/**
 * Created by Andres on 12.10.15.
 */
public class JsonSensor extends AbstractSensor {

    HttpGet getRequest;
    AsyncWorker worker;

    @Override
    protected void setHttpClient() {

        int port = RemoteServerConfiguration.REST_PORT;
        String host = RemoteServerConfiguration.HOST;
        String path = RemoteServerConfiguration.PATH_TO_SPOT1_TEMPERATURE;


        URIBuilder uriBuilder = new URIBuilder().setHost(host).setPort(port).setScheme("http").setPath("");
        URI uri = null;

        //doing this with uriBuilder didn't work somehow, so this is the ugly version
        String uriString = "http://" + host + ":" + String.valueOf(port) + path;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        getRequest = new HttpGet(uri);
        getRequest.setHeader("Accept", "application/json");

        httpClient = SimpleHttpClientFactory.getInstance(SimpleHttpClientFactory.Type.LIB);

        Log.d("debug", "Set up LibHttpClient");


    }

    @Override
    public double parseResponse(String response) {

        Log.d("debug", response);

        JSONObject obj = null;
        try {
            obj = new JSONObject(response);

            return obj.getDouble("value");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RemoteServerConfiguration.ERROR_TEMPERATURE;

    }

    @Override
    public void getTemperature() throws NullPointerException {

        worker = new AsyncWorker();
        worker.execute(getRequest);

    }
}
