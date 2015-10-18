package ch.ethz.inf.vs.a2.http;

/**
 * Created by leynas on 25.09.15.
 */
public abstract class SimpleHttpClientFactory {
    public static SimpleHttpClient getInstance(Type type) {
        switch (type) {
            case RAW:
                return new RawHttpClient();
            case LIB:
                return new LibHttpClient();
            case TRANS:
                return new TransportHttpClient();
            case SOAP:
                return new SoapClient();
            default:
                return null;
        }
    }

    public enum Type {
        RAW, LIB, TRANS, SOAP;
    }
}
