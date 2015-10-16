package ch.ethz.inf.vs.a2.http;

/**
 * Created by Jimmy on 16/10/15.
 */

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TransportHttpClient implements SimpleHttpClient {
    @Override
    public String execute(Object request) {
        HttpClient cl = new DefaultHttpClient();
        HttpPost post = (HttpPost) request;
        try {
            HttpEntity response = cl.execute(post).getEntity();
            if (response != null) {
                String ret = EntityUtils.toString(response);
//                InputStream instream = response.getContent();
//                BufferedReader rd = new BufferedReader(
//                        new InputStreamReader(instream));
               // try {
                    System.out.println("we got a return!: " + ret);
                    return ret;
//                } finally {
//                    instream.close();
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "" + RemoteServerConfiguration.ERROR_TEMPERATURE;

    }
}
