package ch.ethz.inf.vs.a2.sensor;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpsTransportSE;
import org.ksoap2.transport.HttpTransportSE;


import java.io.IOException;
import java.lang.Exception;
import java.util.InputMismatchException;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapPrimitive;

public class SoapSensor extends ch.ethz.inf.vs.a2.sensor.AbstractSensor{

    private Integer spot;
    private Integer temperature; //Modified by 'getTemperature()', used by 'measureTemperature()'


    //Fields used for the SOAP request
    private static final String SOAP_ACTION = "http://tempuri.org/GetInteger2";
    private static final String METHOD_NAME = "GetInteger2";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://10.0.22:4711/Service1.asmx";


    public SoapSensor(){
        spot = 3;
    }


    public double measureTemperature(){
        sendGetTemp(spot);
        return temperature;
    }

    @Override
    protected void setHttpClient() {
        String url = "http://webservices.vslecture.vs.inf.ethz.ch/";

//        HttpContext context = new DefaultClientConnection()
//
//        HttpClient cl = new DefaultHttpClient();
//        cl.execute(url, url, )

    }

    @Override
    public void getTemperature() throws NullPointerException {
        

        SoapObject sobj = new SoapObject("http://webservices.vslecture.vs.inf.ethz.ch/", "getSpot");
        System.out.println("-----------------------------------------");
        System.out.println(sobj.toString());
        sobj.addProperty("id", "Spot " + spot);
        System.out.println(sobj.toString());

        temperature = 2;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("i");
        pi.setValue(123);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpsTransportSE htse = new HttpsTransportSE(URL);

        htse.call(SOAP_ACTION, envelope);
        SoapObject response = (SoapObject)envelope.getResponse();
        C.CategoryId =  Integer.parseInt(response.getProperty(0).toString());
        C.Name =  response.getProperty(1).toString();
        C.Description = (String) response.getProperty(2).toString();






        HttpTransportSE androidHttpTransport = new AndroidHttpTransportSE(URL);
        androidHttpTransport.call(SOAP_ACTION, envelope);

        SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
        return Integer.parseInt(result.toString());

    }

    @Override
    public double parseResponse(String response) {
        ResponseParserImpl responseParser = new ResponseParserImpl();
        return responseParser.parseResponse(response);
    }

//    public void sendGetTemp(int i){
//        if(i==3 || i==4){
//            getTemperature();
//        }
//        else{
//            throw new InputMismatchException("Argument must be 4 or 3! ");
//        }
//    }

}