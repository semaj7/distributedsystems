package ch.ethz.inf.vs.a2.sensor;

/**
 * A parser that extracts the temperature value from an HTTP response.
 * 
 * @author Leyna Sadamori
 *
 */
public interface ResponseParser {
	/**
	 * Parse the HTTP response and extract the temperature value.
	 * @param response A String representation of the HTTP response.
	 * @return A temperature value or {@link Double#NaN} if no temperature can be found.
	 */
	public double parseResponse(String response);
}
