package ch.ethz.inf.vs.a2.vs_jdermelj_webservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerService extends Service {

    Enumeration<NetworkInterface> networkInterfaces;
    ServerSocket socket;
    NetworkInterface wlan;
    int port=8088;

    public ServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();


        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            socket=new ServerSocket(8088);
        }

        catch (SocketException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        NetworkInterface elem;
        while (networkInterfaces.hasMoreElements()){
            elem=networkInterfaces.nextElement();
            Log.d("debugging",elem.getDisplayName());

            if(elem.getDisplayName().equals("wlan0")) {
                wlan = elem;
                Log.d("debugging", "got a wlan! :D");
                break;
            }
        }



        Enumeration<InetAddress> inetAddresses=wlan.getInetAddresses();
        while(inetAddresses.hasMoreElements()){
            inetAddresses.nextElement().getAddress();
        }
        /*
        socket.b
        socket.accept()
        while(true){
            socket.
        }
        */
        /*
        Log.d("ServerService", String.valueOf(networkInterfaces.nextElement().toString()));
        */
    }

    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { return inetAddress; }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

}
