package ch.ethz.inf.vs.a2.sensor;

import org.apache.http.client.methods.HttpPost;
import java.net.URI;

/**
 * Created by Andres on 12.10.15.
 */
/*public class XmlSensor implements Sensor {
    @Override
    public void getTemperature() throws NullPointerException {


        //SOAP-request
        SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
        HttpPost post = new HttpPost(createRequest());

    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void registerListener(SensorListener listener) {

    }

    @Override
    public void unregisterListener(SensorListener listener) {

    }
    private URI createRequest(){
        // Constructor for URI(String scheme, String userInfo, String host, int port, String path, String query, String fragment)
        return new URI( );
    }

}*/

public class XmlSensor extends ch.ethz.inf.vs.a2.sensor.AbstractSensor{

    public int temperature;

    @Override
    protected void setHttpClient() {

    }

    @Override
    public void getTemperature() throws NullPointerException {

        String response = "resp";
        parseResponse(response);
        return;
    }


    // Parse the HTTP response and extract the temperature value.
    @Override
    public double parseResponse(String response) {
        double val = 0;
        return val;
    }

    public double measureTemperature(){
        return 0;
    }

}
