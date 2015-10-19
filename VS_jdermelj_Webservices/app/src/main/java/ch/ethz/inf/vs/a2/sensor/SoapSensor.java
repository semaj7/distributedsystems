package ch.ethz.inf.vs.a2.sensor;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpsTransportSE;
import org.ksoap2.transport.HttpTransportSE;


import java.io.IOException;
import java.lang.Exception;
import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapPrimitive;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import ch.ethz.inf.vs.a2.http.RemoteServerConfiguration;
import ch.ethz.inf.vs.a2.http.SimpleHttpClientFactory;
import ch.ethz.inf.vs.a2.http.SoapHttpClient;

public class SoapSensor extends ch.ethz.inf.vs.a2.sensor.AbstractSensor{

    public AsyncWorker worker;
    public SoapObject request;

    @Override
    protected void setHttpClient() {

        httpClient = SimpleHttpClientFactory.getInstance(SimpleHttpClientFactory.Type.SOAP);

        //The Request object
        request = new SoapObject(RemoteServerConfiguration.SOAP_NAMESPACE, RemoteServerConfiguration.METHOD_NAME);
        request.addProperty("id", RemoteServerConfiguration.SPOT);
    }

    @Override
    public void getTemperature() throws NullPointerException {

        worker = new AsyncWorker();
        worker.execute(request);
    }

    @Override
    public double parseResponse(String response) {
        Log.i("debug", "output: " + response);

       // return Double.valueOf(response); //this will be decommented as soon as exception-problem is fixed
        return RemoteServerConfiguration.ERROR_TEMPERATURE;
    }

}