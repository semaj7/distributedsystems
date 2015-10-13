package ch.ethz.inf.vs.a2.http;

/**
 * Created by Andres on 12.10.15.
 */
public class HttpRawRequestImpl implements HttpRawRequest{

    private String host;
    private int port;
    //should be 8081

    private String path;
    //should be /sunspots/Spot1/sensors/temperature

    public HttpRawRequestImpl(String h, int p, String pa) {

        host = h;
        port = p;
        path = pa;

    }
    @Override
    public String generateRequest() {


        String firstline = "GET " + path + " HTTP/1.1\r\n";
        String secondline = "Host: " + host + "\r\n";
        String thirdline = "Connection: close\r\n\r\n";

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
