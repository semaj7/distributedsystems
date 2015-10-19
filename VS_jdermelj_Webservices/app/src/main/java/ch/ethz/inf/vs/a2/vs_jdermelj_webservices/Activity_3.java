package ch.ethz.inf.vs.a2.vs_jdermelj_webservices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Activity_3 extends AppCompatActivity {

    Intent server_intent;
    final int port = 8088;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        TextView textViewIpAddress= (TextView) findViewById(R.id.ip_address);
        TextView textViewPort= (TextView) findViewById(R.id.port_nr);

        InetAddress localIpAddress=getLocalIpAddress();
        if(localIpAddress!=null)
        {

            //textViewIpAddress.setText("IP Address: " + localIpAddress.getHostAddress().toString().substring(0, localIpAddress.getHostAddress().indexOf("%")));
            textViewIpAddress.setText("IP Address: " + localIpAddress.getHostAddress().toString());
            textViewPort.setText("Port: " + String.valueOf(port));
        }
        server_intent=new Intent(this, ServerService.class);
        server_intent.putExtra("PORT",port);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    public void start_server(View view){
        if(server_intent==null)
            Log.d("debugging", "intent is null");
        Log.d("debugging", "about to start intent");
        startService(server_intent);
    }

    public void stop_server(View view){
        stopService(server_intent);
    }

    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                Log.d("debug","interface found: "+intf.getDisplayName());
                if(intf.getDisplayName().equals("wlan0")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                            return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        Log.d("debug","no wlan found");
        return null;
    }
}
