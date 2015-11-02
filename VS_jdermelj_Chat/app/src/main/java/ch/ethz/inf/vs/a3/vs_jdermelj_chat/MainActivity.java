package ch.ethz.inf.vs.a3.vs_jdermelj_chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.ethz.inf.vs.a3.solution.message.Callback;
import ch.ethz.inf.vs.a3.R;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;
import ch.ethz.inf.vs.a3.solution.message.UDPClient;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

public class MainActivity extends AppCompatActivity implements Callback {


    private EditText nameEdit;    //the EditText section for the username


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!getSharedPreferences("myAppPackage", 0).getString("flag", "").equals("1"))
            setDefaultSettings(); //only the first time the app is started

        //Display the stored username
        nameEdit = (EditText) findViewById(R.id.editUserName);
        String name = getUsername(this);
        nameEdit.setText(name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //user pressed settings-button
    public void start_settings_activity(View view) {
        // Save the username
        setUsername(this, nameEdit.getText().toString());

        //Start activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void onJoinClick(View view) {//user pressed join-button

        // Save the username
        nameEdit = (EditText) findViewById(R.id.editUserName);
        setUsername(this, nameEdit.getText().toString());

        register();
    }
    public void startChatActivity(){ //will be called by callback
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Stores the currently entered username in sharedPreferences (from stackoverflow)
    public static void setUsername(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.commit();
    }
    // Gets the currently username which is stored in sharedPreferences (from stackoverflow)
    public String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        return prefs.getString("username", "");
    }

    public void register(){ //Registers and returns 'true' if successful
        //Create a JSON-register-message
        String name = getUsername(this);
        String url = getSharedPreferences("myAppPackage", 0).getString("url", "");
        String port = getSharedPreferences("myAppPackage", 0).getString("port", "");
        Message m = new Message(name,  null, MessageTypes.REGISTER, null);

        //Send JSON via UDP
        UDPClient cl = new UDPClient(this);
        cl.safeSend(m.toString(), url, port);

    }
    public void setDefaultSettings(){
        SharedPreferences prefs = getSharedPreferences("myAppPackage", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("url", NetworkConsts.SERVER_ADDRESS);
        editor.commit();
        editor = prefs.edit();
        editor.putString("port", Integer.toString(NetworkConsts.UDP_PORT));
        editor.commit();
        editor = prefs.edit();
        editor.putString("name", "new_user");
        editor.commit();
        editor = prefs.edit();
        editor.putString("flag", "1"); // is this flag is set, defaultSettings won't be set in future
        editor.commit();
    }
    public void registrationFailed(){ //if registration failed, this will be called by callback
        Toast toast = Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT);
        toast.show();
    }
}
