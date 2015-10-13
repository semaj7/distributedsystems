package ch.ethz.inf.vs.a2.sensor;

import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;
import ch.ethz.inf.vs.a2.http.SimpleHttpClientFactory;

/**
 * Created by Andres on 12.10.15.
 */
public class HtmlSensor extends AbstractSensor {

    HttpGet getRequest;
    AsyncWorker worker;

    @Override
    protected void setHttpClient() {

        int port = RemoteServerConfiguration.REST_PORT;
        String host = RemoteServerConfiguration.HOST;
        String path = RemoteServerConfiguration.PATH_TO_SPOT1_TEMPERATURE;


        URIBuilder uriBuilder = new URIBuilder().setHost(host).setPort(port).setScheme("http").setPath("");
        URI uri = null;
       



     //   getRequest = new HttpGet(uri);


        httpClient = SimpleHttpClientFactory.getInstance(SimpleHttpClientFactory.Type.LIB);

        Log.d("debug", "Set up LibHttpClient");


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
        worker.execute(getRequest);

    }
}

