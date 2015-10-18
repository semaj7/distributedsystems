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

import ch.ethz.inf.vs.a2.http.SimpleHttpClientFactory;

public class SoapSensor extends ch.ethz.inf.vs.a2.sensor.AbstractSensor{

    public AsyncWorker worker;
    public SoapObject sobj;
    public SoapSerializationEnvelope envelope;
    public HttpPost post;


    //Fields used for the SOAP request
    private static final String SOAP_ACTION = "getSpot";
    private static final String METHOD_NAME = "getSpot";
    private static final String NAMESPACE = "http://webservices.vslecture.vs.inf.ethz.ch/";
    private static final String URL = "http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice";
    private static final String SCHEMA = "http://schemas.xmlsoap.org/soap/envelope/";

    public SoapSensor(){
    }

    @Override
    protected void setHttpClient() { //Is actually not HTTP Client, we just

//        httpClient = SimpleHttpClientFactory.getInstance(SimpleHttpClientFactory.Type.LIB);
//        post = new HttpPost(URL);
//
//        //SOAP Object
//        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//
//        //SOAP Properties
//        request.addProperty("id", "Spot3");
//
//        //SOAP Envelope
//        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.dotNet = true;
//        envelope.setOutputSoapObject(request);
    }

    @Override
    public void getTemperature() throws NullPointerException {


        AsyncCallWS task = new AsyncCallWS();
        task.execute();


//        //HTTP Send
//        HttpTransportSE trans = new HttpTransportSE(URL);
//        try {
//            trans.call(SOAP_ACTION, envelope);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//            try {
//                new AsyncTask<SoapSerializationEnvelope, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(SoapSerializationEnvelope... params) {
//                        SoapSerializationEnvelope e = params[0];
//                        try {
//                            e.getResponse();
//                        } catch (SoapFault soapFault) {
//                            soapFault.printStackTrace();
//                        }
//                        return null;
//                    }
//                }.execute(envelope).get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            System.out.println();
//            //envelope.getResponse();
//                //} catch (SoapFault soapFault) {
//                  //  soapFault.printStackTrace();
//                //}
////        Runnable task = new Runnable() {
////            @Override
////            public void run() {
////                try {
////                    envelope.getResponse();
////                } catch (SoapFault soapFault) {
////                    soapFault.printStackTrace();
////                }
////            }
////        };
//
////        worker = new AsyncWorker();
////        worker.execute(post);
//
//      //  worker.execute((Runnable) envelope.getResponse()); //SOmething like that would be awesome
//
//        //HTTP receive
////        SoapObject response = null;
////        try {
////            response = (SoapObject) envelope.getResponse();
////        } catch (SoapFault s) {
////            s.printStackTrace();
////        }
////
////        String s = response.toString();
////        System.out.println("Whole response: " + s);
////        String k = response.getProperty(0).toString();
////
////        System.out.println("Property 0: " + k);
//
////        SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
////        return Integer.parseInt(result.toString());

    }

    @Override
    public double parseResponse(String response) {
        Log.i("debug", response);

        //TODO: implement it
        return -2000;
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
         final String SOAP_ACTION = "";
         final String METHOD_NAME = "getSpot";
         final String NAMESPACE = "http://webservices.vslecture.vs.inf.ethz.ch/";
        final String URL = "http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice";

        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("id", "Spot3");

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);

            SoapPrimitive resultString = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }

    }

}