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
    SoapHttpClient(){

        SoapObject request = new SoapObject(RemoteServerConfiguration.SOAP_NAMESPACE, RemoteServerConfiguration.METHOD_NAME);
        request.addProperty("id", RemoteServerConfiguration.SPOT);

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

    }



    @Override
    public String execute(Object request) {

        HttpTransportSE transport = new HttpTransportSE(RemoteServerConfiguration.SOAP_HOST);
        transport.setXmlVersionTag(RemoteServerConfiguration.XML_VERSION_TAG);

        try {
            transport.call(RemoteServerConfiguration.SOAP_ACTION, envelope); //TODO: exception happens here
            SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
            return resultString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
