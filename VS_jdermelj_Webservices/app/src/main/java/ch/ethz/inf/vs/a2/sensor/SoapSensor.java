package ch.ethz.inf.vs.a2.sensor;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import java.lang.Exception;
import java.util.InputMismatchException;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.SoapEnvelope;

public class SoapSensor extends ch.ethz.inf.vs.a2.sensor.AbstractSensor{

    private Integer spot;
    private Integer temperature; //Modified by 'getTemperature()', used by 'measureTemperature()'


    //Fields used for the SOAP request
    private static final String SOAP_ACTION = "http://tempuri.org/GetInteger2";
    private static final String METHOD_NAME = "GetInteger2";
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://10.0.22:4711/Service1.asmx";


    public SoapSensor(){
        spot = 3;
    }


    public double measureTemperature(){
        sendGetTemp(spot);
        return temperature;
    }

    @Override
    protected void setHttpClient() {

    }

    @Override
    public void getTemperature() throws NullPointerException {
        

        SoapObject sobj = new SoapObject("http://webservices.vslecture.vs.inf.ethz.ch/", "getSpot");
        System.out.println("-----------------------------------------");
        System.out.println(sobj.toString());
        sobj.addProperty("id", "Spot " + spot);
        System.out.println(sobj.toString());

        temperature = 2;

    }

    @Override
    public double parseResponse(String response) {
        return 0;
    }

    public void sendGetTemp(int i){
        if(i==3 || i==4){
            getTemperature();
        }
        else{
            throw new InputMismatchException("Argument must be 4 or 3! ");
        }
    }


    public int GetInteger2() throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("i");
        pi.setValue(123);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(URL);
        androidHttpTransport.call(SOAP_ACTION, envelope);

        SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
        return Integer.parseInt(result.toString());
}