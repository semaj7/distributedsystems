package ch.ethz.inf.vs.a2.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Andres on 12.10.15.
 */
public class RawHttpClient implements SimpleHttpClient {
    @Override
    //expects request to be an instance of HttpRawRequestImpl
    public String execute(Object request) {

        Log.d("debug", "Executing a request");

        //return if the request is not an instance of HttpRawRequestImpl
        if (!(request instanceof HttpRawRequestImpl)) return null;
        HttpRawRequest r = (HttpRawRequestImpl) request;

        PrintWriter message_out = null;
        BufferedReader message_in = null;
        String response = "";

        String host = r.getHost();

        Log.d("debug", "Host: " + host);

        int port = r.getPort();
        if (host == null) return null;

        Log.d("debug", "Port: " + String.valueOf(port));

        try {
            Socket socket = new Socket(host, port);


            Log.d("debug", "Connected with a socket.");

            String sendString = r.generateRequest();

            Log.d("debug", "Sending: " + sendString);

            message_out = new PrintWriter( socket.getOutputStream(), true);

            message_out.print(sendString);

            message_out.flush();

            InputStream inStream = socket.getInputStream( );
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(inStream));
            String line;
            while ((line = rd.readLine()) != null) {
                response = response + line;
            }


            Log.d("debug", "Got message: " + response);



        }
        catch(Exception e) {

            System.err.println("Something went wrong while connecting to host : " + host + " with port " + port);
            System.err.println(e.toString());

            return null;

        }


        return response;
    }
}
