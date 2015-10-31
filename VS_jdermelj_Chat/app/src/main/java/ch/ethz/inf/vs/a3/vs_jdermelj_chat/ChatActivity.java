package ch.ethz.inf.vs.a3.vs_jdermelj_chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ch.ethz.inf.vs.a3.R;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;
import ch.ethz.inf.vs.a3.solution.message.UDPClient;


public class ChatActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);


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
        String name = getUsername(this);
        String url = getSharedPreferences("myAppPackage", 0).getString("url", "");
        String port = getSharedPreferences("myAppPackage", 0).getString("port", "");
        Message m = new Message(name, url+port, null, MessageTypes.RETRIEVE_CHAT_LOG, "");

        //Send JSON via UDP
        UDPClient cl = new UDPClient();
        cl.send(m.toString(), url, port);

        TextView logTextView = (TextView) findViewById(R.id.logTextView);
        logTextView.setText(cl.retrieveLog());


    }

    // Gets the currently username which is stored in sharedPreferences (from stackoverflow)
    public String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        return prefs.getString("username", "");
    }

}
