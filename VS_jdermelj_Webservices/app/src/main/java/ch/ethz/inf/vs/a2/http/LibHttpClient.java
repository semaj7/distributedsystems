package ch.ethz.inf.vs.a2.http;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Andres on 12.10.15.
 */
public class LibHttpClient implements SimpleHttpClient {
    @Override
    public String execute(Object request) {

        Log.d("debug", "Executing a request the simple way");

        if (!(request instanceof HttpGet)) return null;
        HttpGet getRequest = (HttpGet) request;

        HttpClient httpClient = new DefaultHttpClient();

        getRequest = new HttpGet("http://localhost/");
        // add request header
      //  getRequest.addHeader("User-Agent", ""); //TODO: get real User-Agent through context or whatever

        HttpResponse response = null;

        try {
            //trying to get a response after sending the getRequest
            response = httpClient.execute(getRequest);

        } catch (IOException e) {

            System.err.println("Something went wrong while getting simple request");
            System.err.println(e.toString());
            return null;
        }

        Log.d("debug", "Response Code : " + String.valueOf(response.getStatusLine().getStatusCode()));


        BufferedReader rd = null;
        try {
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {


            System.err.println("Something went wrong while producing a reader");
            System.err.println(e.toString());
            return null;
        }

        StringBuffer result = new StringBuffer();
        String line = "";
        try {
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            System.err.println("Something went wrong while reading the response");
            System.err.println(e.toString());
            return null;
        }

        return result.toString();
    }
}
