package ch.ethz.inf.vs.a2.http;


/**
 * Collection of constant definitions for the remote server.
 * 
 * @author Leyna Sadamori
 *
 */
public interface RemoteServerConfiguration {
	public static final String HOST = "vslab.inf.ethz.ch";
	public static final int REST_PORT = 8081;
	public static final int SOAP_PORT = 8080;
	public static final String PATH_TO_SPOT1_TEMPERATURE = "/sunspots/Spot1/sensors/temperature";
	public static final double ERROR_TEMPERATURE = -1000; //it shouldn't get this cold ;P

	public static final String SOAP_HOST = "http://vslab.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice";
	public static final String SOAP_NAMESPACE = "http://webservices.vslecture.vs.inf.ethz.ch/";
	public static final String XML_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header/><S:Body><ns2:getSpot xmlns:ns2=\"http://webservices.vslecture.vs.inf.ethz.ch/\"><id>Spot3</id></ns2:getSpot></S:Body></S:Envelope>";
	public static final String SOAP_SCHEMA = "http://schemas.xmlsoap.org/soap/envelope/";
	public static final String METHOD_NAME = "getSpot";
	public static final String SOAP_ACTION = "";
	public static final String XML_VERSION_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String SPOT = "Spot3";

}
