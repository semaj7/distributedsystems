package ch.ethz.inf.vs.a2.http;

import android.util.Log;

/**
 * Created by Andres on 12.10.15.
 */
public class HttpRawRequestImpl implements HttpRawRequest{

    private String host;
    private int port;
    private String path;

    public HttpRawRequestImpl(String new_host, int new_port, String new_path) {

        host = new_host;
        port = new_port;
        path = new_path;

        Log.d("debug", "Made a new HttpRawRequestImpl");

    }
    @Override
    public String generateRequest() {


        String firstline = "GET " + path + " HTTP/1.1\r\n";
        String secondline = "Host: " + host + "\r\n";
        String thirdline = "Connection: close\r\n\r\n";

        Log.d("debug", "Generating request");

        return firstline + secondline + thirdline;


    }

    @Override
    public String getHost() {

        return host;
    }

    @Override
    public int getPort() {
        return port;
    }
}
