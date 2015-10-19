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

    public SoapSensor(){
    }

    @Override
    protected void setHttpClient() {
        httpClient = SimpleHttpClientFactory.getInstance(SimpleHttpClientFactory.Type.TRANS);
    }

    @Override
    public void getTemperature() throws NullPointerException {


        AsyncCallWS task = new AsyncCallWS();
        task.execute();



    }

    @Override
    public double parseResponse(String response) {
        Log.i("debug", response);

        //TODO: implement it
        return RemoteServerConfiguration.ERROR_TEMPERATURE;
    }
    private String TAG ="Vik";

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            calculate();
            Log.i(TAG, "finished doing in Background");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

    }

    public void calculate() {

        try {
            SoapObject request = new SoapObject(RemoteServerConfiguration.SOAP_NAMESPACE, RemoteServerConfiguration.METHOD_NAME);
            request.addProperty("id", "Spot3");

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(RemoteServerConfiguration.SOAP_HOST);
            transport.setXmlVersionTag(RemoteServerConfiguration.XML_VERSION_TAG);

            transport.call(RemoteServerConfiguration.SOAP_ACTION, soapEnvelope);

            SoapPrimitive resultString = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage()); //TODO: we always catch this exception. HTTP Status 500
        }


    }

}