package ch.ethz.inf.vs.a2.sensor;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import ch.ethz.inf.vs.a2.http.SimpleHttpClientFactory;

/**
 *  Set of functions for:
 *  creating a XML Request,
 *  trigger the http POST transmission of this request,
 *  extracting the temperature from any xml-string
 */

public class XmlSensor extends ch.ethz.inf.vs.a2.sensor.AbstractSensor{

    HttpPost postRequest;
    AsyncWorker worker;

    @Override
    protected void setHttpClient() {

        //Creating XML-Request
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header/><S:Body><ns2:getSpot xmlns:ns2=\"http://webservices.vslecture.vs.inf.ethz.ch/\"><id>Spot3</id></ns2:getSpot></S:Body></S:Envelope>";
        StringEntity ent = new StringEntity(xml, "UTF-8");
        ent.setContentType("text/xml");

        //Creating HTTP Post-Request
        String url = "http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice";
        postRequest = new HttpPost(url);
        postRequest.addHeader("Connection", "close"); //Header added, as advised by slides
        postRequest.setEntity(ent);

        //Setting HTML client
        httpClient = SimpleHttpClientFactory.getInstance(SimpleHttpClientFactory.Type.TRANS);
    }

    @Override
    public void getTemperature() throws NullPointerException {

        //The worker executes the sending and receiving asynchronously
        worker = new AsyncWorker();
        worker.execute(postRequest);

    }

    @Override
    public double parseResponse(String response) {

        //Extracting the Temperature-value from the XML-Response
        ResponseParserXml responseParser = new ResponseParserXml();
        return responseParser.parseResponse(response);

    }

}
