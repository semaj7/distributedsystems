package ch.ethz.inf.vs.a2.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Andres on 12.10.15.
 */
public class RawHttpClient implements SimpleHttpClient {
    @Override
    //expects request to be an instance of HttpRawRequestImpl
    public String execute(Object request) {

        //return if the request is not an instance of HttpRawRequestImpl
        if (!(request instanceof HttpRawRequestImpl)) return null;
        HttpRawRequest r = (HttpRawRequestImpl) request;

        PrintWriter message_out = null;
        BufferedReader message_in = null;

        String host = r.getHost();
        int port = r.getPort();
        if (host == null) return null;

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));

            message_out = new PrintWriter( socket.getOutputStream(), true);

            //reader for socket
            message_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }
        catch(Exception e) {

            System.err.println("Something went wrong while connecting to host : " + host +" with port" + port);
            System.exit(1);

        }

        String sendString = r.generateRequest();
        message_out.println(sendString);
        message_out.flush();

        String response = null;

        try {
            while ((response = message_in.readLine()) != null) {
                System.out.println(response);
            }
        }
        catch (IOException e) {
            System.err.println("Something went wrong while receiving response from the host : " + host);
            System.exit(1);
        }
        return response;
    }
}
