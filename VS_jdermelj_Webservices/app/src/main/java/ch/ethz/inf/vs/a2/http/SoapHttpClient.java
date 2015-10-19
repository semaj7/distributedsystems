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

    SoapHttpClient(){

    }

    @Override
    public String execute(Object request) {

        //Create Envelope
        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.dotNet = true;

        //Add request to envelope
        SoapObject soapRequest = (SoapObject) request;
        soapEnvelope.setOutputSoapObject(soapRequest);

        //Create transport object
        HttpTransportSE transport = new HttpTransportSE(RemoteServerConfiguration.SOAP_HOST);
        transport.setXmlVersionTag(RemoteServerConfiguration.XML_VERSION_TAG);
        transport.debug = true;

        try { //Execute transport //TODO: exception happens here. no idea why
            transport.call("", soapEnvelope); //first argument is the soap action. i think this should be empty. but i tried many other inputs, none of them worked.
            SoapObject result = (SoapObject) soapEnvelope.getResponse();
            return result.toString();

        } catch (Exception e) {
            Log.i("ERROR", "we reached an exception in soapClient execute");

            e.printStackTrace();
        }
        return null;

    }
}
