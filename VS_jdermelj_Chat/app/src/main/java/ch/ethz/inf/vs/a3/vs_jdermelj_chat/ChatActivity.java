package ch.ethz.inf.vs.a3.vs_jdermelj_chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ch.ethz.inf.vs.a3.R;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;
import ch.ethz.inf.vs.a3.solution.message.UDPClient;


public class ChatActivity extends AppCompatActivity{

    private  String name;
    private  String url;
    private  String port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        //Get the important variables
        name = getSharedPreferences("myAppPackage", 0).getString("username", "");
        url = getSharedPreferences("myAppPackage", 0).getString("url", "");
        port = getSharedPreferences("myAppPackage", 0).getString("port", "");

        System.out.println("Variables " + name + " " + url + " " + port + " loaded into chatActivity.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void retrieveChatLog(View v){

        //Create a RETRIEVE_CHAT_LOG-message
        Message m = new Message(name, url+port, null, MessageTypes.RETRIEVE_CHAT_LOG, "");

        //Send JSON via UDP
        UDPClient cl = new UDPClient(m.toString(), url, port);
        cl.send();

        TextView logTextView = (TextView) findViewById(R.id.logTextView);
        logTextView.setText(cl.retrieveLog());
    }

    @Override
    public void onBackPressed() {
        if (deregister()){
            //toast:  deregistered
            Toast toast = Toast.makeText(this, "deregistered successfully", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            //toast:  did not deregister correctly //TODO: What do then?
            Toast toast = Toast.makeText(this, "Unproper deregistration", Toast.LENGTH_SHORT);
            toast.show();
        }
        super.onBackPressed();
    }

    public boolean deregister(){
        Message m = new Message(name, url+port, null, MessageTypes.DEREGISTER, "");
        //Send JSON via UDP
        UDPClient cl = new UDPClient(m.toString(), url, port);
        if (cl.safeSend()){//send and wait for ack
            return true;
        }
        return false;
    }
}
