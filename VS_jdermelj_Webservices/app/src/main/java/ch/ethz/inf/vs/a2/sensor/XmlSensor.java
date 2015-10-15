package ch.ethz.inf.vs.a2.sensor;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;
import ch.ethz.inf.vs.a2.http.SimpleHttpClientFactory;

/* Copied some code from StackOverflow: 'http://stackoverflow.com/questions/3324717/sending-http-post-request-in-java' */

//SOAP-request
       // SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
        //HttpPost post = new HttpPost(createRequest());


public class XmlSensor extends ch.ethz.inf.vs.a2.sensor.AbstractSensor{


    HttpPost postRequest;
    AsyncWorker worker;

    @Override
    protected void setHttpClient() {
        postRequest = new HttpPost("http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice");
        //String uriString = "http://" + RemoteServerConfiguration.HOST + ":" + RemoteServerConfiguration.SOAP_PORT + RemoteServerConfiguration.;
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header/><S:Body><ns2:getSpot xmlns:ns2=\"http://webservices.vslecture.vs.inf.ethz.ch/\"><id>Spot3</id></ns2:getSpot></S:Body></S:Envelope>\"";

// Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
         params.add(new BasicNameValuePair("param-1", xml));
        //  params.add(new BasicNameValuePair("param-2", "Hello!"));
        // postRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

       // try {
          //  UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "UTF-8"); //Might cause exception

            StringEntity ent = new StringEntity(xml, "UTF-8");
            ent.setContentType("xml");
            postRequest.setEntity(ent);
            Log.d("debug", "post created");

        //Setting HTML client
        httpClient = SimpleHttpClientFactory.getInstance(SimpleHttpClientFactory.Type.RAW);

        System.out.println(postRequest);
            //Execute and get the response.

//            HttpResponse response = httpclient.execute(postRequest);
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) {
//                InputStream instream = entity.getContent();
//                try {
//                    // do something useful
//                    System.out.println("response: (intstream) " + instream.toString());
//                } finally {
//                    instream.close();
//                }
//            }
//            else
//                System.out.println("entity == null");

        //catch (Exception e) {
            //System.out.println("EXCEPTION! (in setHttpClient)");
           // e.printStackTrace();
    }

    @Override
    public void getTemperature() throws NullPointerException {

        worker = new AsyncWorker();
        worker.execute(postRequest);

    }


    // Parse the HTTP response and extract the temperature value.
    @Override
    public double parseResponse(String response) {
        // use xmlpullparser
        double val = 10;
        Log.d("debug", "val: " + val );
        return val;
    }

}
