package ch.ethz.inf.vs.a2.http;


public class HttpRawRequestFactory {
	public static HttpRawRequest getInstance(String host, int port, String path) {
		// return HttpRawRequest implementation
		return new HttpRawRequestImpl(host, port, path);
	}
}
