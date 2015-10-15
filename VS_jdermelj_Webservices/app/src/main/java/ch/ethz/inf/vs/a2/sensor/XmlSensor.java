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
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"> \n <S:Header/> \n<S:Body> \n<ns2:getSpot xmlns:ns2=\"http://webservices.vslecture.vs.inf.ethz.ch/\"> \n<id>Spot3</id> \n</ns2:getSpot> \n</S:Body> \n</S:Envelope>\"";
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
