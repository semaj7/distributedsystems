package ch.ethz.inf.vs.a2.http;

/**
 * Created by Andres on 12.10.15.
 */
public class HttpRawRequestImpl implements HttpRawRequest{

    private String host;
    private int port;
    private String path;

    public HttpRawRequestImpl(String h, int p, String pa) {

        host = h;
        port = p;
        path = pa;

    }
    @Override
    public String generateRequest() {
        return null;
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
