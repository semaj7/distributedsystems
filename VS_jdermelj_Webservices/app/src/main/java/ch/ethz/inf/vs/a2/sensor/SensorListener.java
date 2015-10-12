package ch.ethz.inf.vs.a2.sensor;

/**
 * A SensorListener offers callback functions to get notifications as soon as new sensor value arrive.
 * 
 * @author Leyna Sadamori
 *
 */
public interface SensorListener {
	/**
	 * Callback function for receiving double values.
	 * @param value A sensor value
	 */
	public void onReceiveDouble(double value);
	
	/**
	 * Callback function for receiving String messages.
	 * Could be useful for debugging.
	 * @param message A message
	 */
	public void onReceiveString(String message);
}
