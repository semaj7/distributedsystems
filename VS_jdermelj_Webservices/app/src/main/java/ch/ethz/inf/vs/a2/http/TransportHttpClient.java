package ch.ethz.inf.vs.a2.http;

/**
 * Created by Jimmy on 16/10/15.
 */

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
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

            //Created proxy in order to web-debug with 'Charles'.
            // URL was device-ip (couldn't use localhost because app was emulated)
        //HttpHost proxy = new HttpHost("192.168.0.122", 8888, "http");
        //cl.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        try {
            HttpEntity response = cl.execute(post).getEntity();
            if (response != null) {
                String ret = EntityUtils.toString(response);
                    return ret;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
