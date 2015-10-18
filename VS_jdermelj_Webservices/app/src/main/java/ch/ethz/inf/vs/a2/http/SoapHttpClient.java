package ch.ethz.inf.vs.a2.http;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class SoapHttpClient implements SimpleHttpClient {

    private SoapSerializationEnvelope envelope;
    private String URL = "http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice";

    SoapHttpClient(){

        String namespace = "http://webservices.vslecture.vs.inf.ethz.ch/";
        SoapObject request = new SoapObject(namespace, "getSpot");
        request.addProperty("id", "Spot3");

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

    }



    @Override
    public String execute(Object request) {

//        AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(URL);
//        androidHttpTransport.call(SOAP_ACTION, envelope);
//
//        SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
        HttpTransportSE transport = new HttpTransportSE(URL);

        try {
            transport.call("", envelope); //TODO: exception happens here
            SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
            return resultString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
