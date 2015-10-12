package ch.ethz.inf.vs.a2.http;


/**
 * HttpRawRequest generates a simple HTTP GET request of HTTP version 1.1.
 * The request should at least set the optional header field "Connection" to "close".
 *
 * Classes that implement this interface should take
 * 1) String representation of the host address
 * 2) Destination port
 * 3) Absolute path of the requested resource (See <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.2" target="_blank">Request-URI</a>)
 * as constructor arguments.
 * 
 * @author Leyna Sadamori
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616.html" target="_blank">RFC2616</a> for the HTTP/1.1 specification.
 *
 */
public interface HttpRawRequest {

	/**
	 * @return HTTP GET request
	 */
	public String generateRequest();

	/**
	 * @return String representation of the host address
	 */
	public String getHost();

	/**
	 * @return Destination port
	 */
	public int getPort();
}
