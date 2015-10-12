package ch.ethz.inf.vs.a2.sensor;

/**
 * A sensor representation that provides temperature measurements. 
 * 
 * @author Leyna Sadamori
 *
 */
public interface Sensor {
	
	/**
	 * Invoke a call for a new temperature measurement. 
	 * 
	 * @throws NullPointerException If name has not been set yet
	 */
	public void getTemperature() throws NullPointerException;
	
	
	/**
	 * Set name of this sensor.
	 * @param name Name of this sensor
	 */
	public void setName(String name);


	/**
	 * Register a SensorListener to this sensor.
	 * @param listener The SensorListener to be registered
	 */
	public void registerListener(SensorListener listener);

	/**
	 * Unregister a SensorListener from this sensor.
	 * @param listener The SensorListener to be unregistered
	 */
	public void unregisterListener(SensorListener listener);
}
