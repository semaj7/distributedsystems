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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.UUID;

import ch.ethz.inf.vs.a3.R;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;

public class MainActivity extends AppCompatActivity {


    EditText nameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEdit = (EditText) findViewById(R.id.editUserName);

        //Display stored username
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


    //user pressed join-button (wird später anders ausgelöst)
    public void start_chat_activity(View view) {

        // Save the username
        nameEdit = (EditText) findViewById(R.id.editUserName);
        setUsername(this,nameEdit.getText().toString());

        //Start activity
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

    public void register(){
        //Create a register-message
        String name = getUsername(this);
        String url = getSharedPreferences("myAppPackage", 0).getString("url", "");
        String port = getSharedPreferences("myAppPackage", 0).getString("port", "");
        Message m = new Message(name, url+port, null, MessageTypes.REGISTER, "");

        //Create a JSON object
        try {
            JSONObject obj = new JSONObject(m.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Send JSON via UDP
    }
}
